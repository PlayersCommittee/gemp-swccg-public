package com.gempukku.swccgo.ai.models.rando.evaluators;

import com.gempukku.swccgo.ai.common.AiPriorityCards;
import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Action Text Evaluator
 *
 * Handles text-based action ranking by pattern matching action text.
 * Ported from Python action_text_evaluator.py (~1350 lines)
 *
 * This evaluator provides baseline rankings for common SWCCG actions
 * based on analyzing the action text.
 */
public class ActionTextEvaluator extends ActionEvaluator {

    // Rank deltas (from Python)
    private static final float VERY_GOOD_DELTA = 50.0f;
    private static final float GOOD_DELTA = 30.0f;
    private static final float BAD_DELTA = -30.0f;
    private static final float VERY_BAD_DELTA = -50.0f;

    // Pattern for extracting blueprint ID from action text HTML
    private static final Pattern BLUEPRINT_PATTERN = Pattern.compile("value='([^']+)'");

    // Track barriered targets to avoid playing multiple barriers on same card
    private Set<String> barrieredTargets = new HashSet<>();
    private int barrierTurn = 0;

    public ActionTextEvaluator() {
        super("ActionText");
    }

    @Override
    public boolean canEvaluate(DecisionContext context) {
        String decisionType = context.getDecisionType();

        // Handle CARD_ACTION_CHOICE and ACTION_CHOICE
        if ("CARD_ACTION_CHOICE".equals(decisionType) || "ACTION_CHOICE".equals(decisionType)) {
            return true;
        }

        // Also handle MULTIPLE_CHOICE for capacity slot decisions
        if ("MULTIPLE_CHOICE".equals(decisionType)) {
            String decisionText = context.getDecisionText();
            if (decisionText != null && decisionText.toLowerCase().contains("capacity slot")) {
                return true;
            }
        }

        return false;
    }

    @Override
    public List<EvaluatedAction> evaluate(DecisionContext context) {
        List<EvaluatedAction> actions = new ArrayList<>();
        GameState gameState = context.getGameState();
        SwccgGame game = context.getGame();
        List<String> actionIds = context.getActionIds();
        List<String> actionTexts = context.getActionTexts();
        List<String> cardIds = context.getCardIds();
        Set<String> blocked = context.getBlockedResponses();

        for (int i = 0; i < actionIds.size(); i++) {
            String actionId = actionIds.get(i);
            String actionText = i < actionTexts.size() ? actionTexts.get(i) : "";
            String cardId = i < cardIds.size() ? cardIds.get(i) : null;
            String textLower = actionText.toLowerCase();

            EvaluatedAction action = new EvaluatedAction(actionId, ActionType.UNKNOWN, 0.0f, actionText);

            // Check if this response is blocked (loop prevention)
            if (blocked.contains(actionId) || blocked.contains(actionText)) {
                action.addReasoning("BLOCKED (loop prevention)", -200.0f);
                logger.debug("Blocked action: {}", actionText);
            }

            // ========== Skip ALL Deploy Actions ==========
            // Deploy actions should be handled EXCLUSIVELY by DeployEvaluator.
            if (actionText.equals("Deploy") ||
                (actionText.startsWith("Deploy ") && !textLower.contains("from"))) {
                // Skip this action - let DeployEvaluator handle it
                continue;
            }

            // ========== Capacity Slot Selection (Pilot vs Passenger) ==========
            if (textLower.contains("capacity slot")) {
                if (textLower.contains("pilot capacity slot")) {
                    action.setScore(100.0f);
                    action.addReasoning("Pilot slot adds power to ship!", 100.0f);
                    action.setActionType(ActionType.MOVE);
                    logger.info("PILOT SLOT: Strongly preferring pilot capacity (+100)");
                } else if (textLower.contains("passenger capacity slot")) {
                    action.setScore(VERY_BAD_DELTA);
                    action.addReasoning("Passenger gives NO power bonus!", VERY_BAD_DELTA);
                    action.setActionType(ActionType.MOVE);
                    logger.warn("PASSENGER SLOT: Penalizing - no power contribution ({})", VERY_BAD_DELTA);
                }
                actions.add(action);
                continue;
            }

            // ========== Force Activation ==========
            if (actionText.equals("Activate Force")) {
                action.setActionType(ActionType.ACTIVATE_FORCE);
                evaluateActivateForce(action, context);
            }

            // ========== Force Drain ==========
            else if (actionText.equals("Force drain")) {
                action.setActionType(ActionType.FORCE_DRAIN);
                evaluateForceDrain(action, context, cardId);
            }

            // ========== Race Destiny ==========
            else if (actionText.equals("Draw race destiny")) {
                action.setActionType(ActionType.RACE_DESTINY);
                action.addReasoning("Race destiny always high priority", VERY_GOOD_DELTA);
            }

            // ========== Play a Card ==========
            else if (actionText.equals("Play a card")) {
                action.setActionType(ActionType.PLAY_CARD);
                evaluatePlayCard(action, context);
            }

            // ========== Fire Weapons ==========
            else if (actionText.contains("Fire")) {
                action.setActionType(ActionType.FIRE_WEAPON);
                // Check if there are valid (non-HIT) targets before firing
                // Ported from Python action_text_evaluator.py - don't fire at already-hit targets
                boolean hasValidTargets = checkForValidWeaponTargets(context);
                if (hasValidTargets) {
                    action.addReasoning("Firing weapons at valid targets", VERY_GOOD_DELTA);
                } else {
                    action.addReasoning("All targets already HIT - save weapon", BAD_DELTA);
                    logger.debug("Skipping weapon fire - no valid (unhit) targets");
                }
            }

            // ========== Add Battle Destiny ==========
            else if (textLower.contains("add") && textLower.contains("battle destiny")) {
                action.setActionType(ActionType.BATTLE_DESTINY);
                action.addReasoning("Adding battle destiny is great", VERY_GOOD_DELTA);
            }

            // ========== Battle Destiny Modifier (+1 to battle destiny) ==========
            else if ((actionText.contains("+1") || actionText.contains("+ 1") || textLower.contains("add 1"))
                     && textLower.contains("battle destiny")) {
                action.setActionType(ActionType.BATTLE_DESTINY);
                action.addReasoning("+1 to battle destiny - always use!", VERY_GOOD_DELTA);
            }

            // ========== Weapon Destiny Modifier ==========
            else if (textLower.contains("weapon destiny") &&
                     (actionText.contains("+3") || actionText.contains("+2") || textLower.contains("add"))) {
                action.setActionType(ActionType.FIRE_WEAPON);
                action.addReasoning("Boost weapon destiny - increases hit chance!", VERY_GOOD_DELTA);
            }

            // ========== Protect Battle Destiny Draws ==========
            else if (textLower.contains("prevent") && textLower.contains("cancel") &&
                     textLower.contains("battle destiny") && textLower.contains("draw")) {
                action.setActionType(ActionType.BATTLE_DESTINY);
                evaluateDestinyProtection(action, context);
            }

            // ========== Prevent Opponent Adding Battle Destiny ==========
            else if (textLower.contains("prevent") && textLower.contains("battle destiny") &&
                     !textLower.contains("cancel")) {
                action.setActionType(ActionType.BATTLE_DESTINY);
                action.addReasoning("Prevent opponent battle destiny - denies their draw!", VERY_GOOD_DELTA);
            }

            // ========== Take Admiral/General Into Hand ==========
            else if (textLower.contains("take") && textLower.contains("into hand") &&
                     (textLower.contains("admiral") || textLower.contains("general"))) {
                action.addReasoning("Retrieve admiral/general into hand", GOOD_DELTA);
            }

            // ========== Substitute Destiny ==========
            else if (textLower.contains("substitute destiny")) {
                action.setActionType(ActionType.SUBSTITUTE_DESTINY);
                action.addReasoning("Substituting destiny is good", GOOD_DELTA);
            }

            // ========== React ==========
            else if (textLower.contains("react")) {
                action.setActionType(ActionType.REACT);
                action.addReasoning("Avoid reacts (bot doesn't understand timing)", BAD_DELTA);
            }

            // ========== Steal ==========
            else if (textLower.contains("steal")) {
                action.setActionType(ActionType.STEAL);
                action.addReasoning("Stealing is good", GOOD_DELTA);
            }

            // ========== Sabacc ==========
            else if (textLower.contains("play sabacc")) {
                action.setActionType(ActionType.SABACC);
                action.addReasoning("Playing sabacc", GOOD_DELTA);
            }

            // ========== Cancel Own Cards (Bad!) ==========
            else if (textLower.contains("cancel your")) {
                action.setActionType(ActionType.CANCEL);
                action.addReasoning("Never cancel own cards", VERY_BAD_DELTA);
            }

            // ========== Cancel Opponent's Interrupt (Sense/Control) ==========
            else if (textLower.contains("cancel") &&
                     (textLower.contains("interrupt") || textLower.contains("sense") ||
                      textLower.contains("alter") || textLower.contains("effect") ||
                      textLower.contains("force drain")) &&
                     !textLower.contains("your")) {
                action.setActionType(ActionType.CANCEL);
                evaluateSenseCancel(action, context, actionText);
            }

            // ========== Cancel/Redraw Destiny ==========
            else if (textLower.contains("cancel and redraw") && textLower.contains("destiny")) {
                action.addReasoning("Redraw destiny (current may be low)", GOOD_DELTA);
            }

            // ========== Cancel Weapon Targeting ==========
            else if (textLower.contains("cancel") && textLower.contains("weapon") && textLower.contains("target")) {
                action.setActionType(ActionType.CANCEL);
                action.addReasoning("Cancel weapon targeting - protect our characters!", VERY_GOOD_DELTA);
            }

            // ========== Immune to Attrition ==========
            else if (textLower.contains("immune to attrition")) {
                action.addReasoning("Make character immune to attrition - valuable protection!", VERY_GOOD_DELTA);
            }

            // ========== Protect Forfeit ==========
            else if (textLower.contains("forfeit") &&
                     (textLower.contains("protect") || textLower.contains("preserved"))) {
                action.addReasoning("Protect forfeit value during battle", GOOD_DELTA + 10.0f);
            }

            // ========== Re-target Weapon ==========
            else if (textLower.contains("re-target") || textLower.contains("retarget")) {
                action.addReasoning("Re-target weapon at enemy - turn their weapon against them!", VERY_GOOD_DELTA);
            }

            // ========== Cancel Battle Damage (Houjix/Ghhhk) ==========
            else if (actionText.contains("Cancel all remaining battle damage")) {
                action.setActionType(ActionType.CANCEL_DAMAGE);
                evaluateHoujixGhhhk(action, context);
            }

            // ========== Take Card Into Hand ==========
            else if (actionText.contains("Take") && actionText.contains("into hand")) {
                evaluateTakeIntoHand(action, context, actionText, textLower);
            }

            // ========== Prevent Battle/Move (Barrier Cards) ==========
            else if (actionText.contains("Prevent") && actionText.contains("from battling or moving")) {
                evaluateBarrier(action, context, actionText);
            }

            // ========== Monnok-type (Reveal Hand) ==========
            else if (actionText.contains("LOST: Reveal opponent's hand")) {
                int theirHandSize = gameState != null ? gameState.getHand(context.getOpponentId()).size() : 0;
                if (theirHandSize > 6) {
                    action.addReasoning("Opponent has many cards - reveal worth it", VERY_GOOD_DELTA);
                } else {
                    action.addReasoning("Opponent has few cards - save reveal", VERY_BAD_DELTA);
                }
            }

            // ========== Dangerous Cards ==========
            else if (textLower.contains("stardust") || textLower.contains("on the edge")) {
                action.addReasoning("Known dangerous card", VERY_BAD_DELTA);
            }

            // ========== Draw Card Into Hand ==========
            else if (actionText.equals("Draw card into hand from Force Pile")) {
                action.setActionType(ActionType.DRAW);
                action.addReasoning("Draw option (see DrawEvaluator)", 0.0f);
            }

            // ========== Movement Actions ==========
            else if (actionText.contains("Move using") || actionText.contains("Shuttle") ||
                     actionText.contains("Docking bay transit") || actionText.contains("Transport")) {
                action.setActionType(ActionType.MOVE);
                action.addReasoning("Movement option (see MoveEvaluator)", 0.0f);
            }
            else if (actionText.equals("Take off") || actionText.equals("Land")) {
                action.setActionType(ActionType.MOVE);
                action.addReasoning("Take off/Land option (see MoveEvaluator)", 0.0f);
            }

            // ========== Make Opponent Lose Force ==========
            else if (actionText.contains("Make opponent lose")) {
                action.addReasoning("Making opponent lose force", GOOD_DELTA);
            }

            // ========== Deploy Docking Bay ==========
            else if (actionText.contains("Deploy docking bay")) {
                action.addReasoning("Deploying docking bay", GOOD_DELTA);
            }

            // ========== Deploy From Reserve (Risky) ==========
            else if (actionText.contains("Deploy") && actionText.contains("from")) {
                action.addReasoning("Deploying from reserve - risky", BAD_DELTA);
            }

            // ========== Embark ==========
            else if (actionText.contains("Embark")) {
                action.setActionType(ActionType.MOVE);
                evaluateEmbark(action, context, actionText);
            }

            // ========== Disembark/Relocate/Transfer ==========
            else if (actionText.contains("Disembark") || actionText.contains("Relocate") ||
                     actionText.contains("Transfer")) {
                action.setActionType(ActionType.MOVE);
                action.addReasoning("Usually avoid disembark/relocate/transfer", VERY_BAD_DELTA);
            }

            // ========== Ship-dock ==========
            else if (actionText.contains("Ship-dock")) {
                action.addReasoning("Avoid ship-docking", VERY_BAD_DELTA);
            }

            // ========== Place in Lost Pile ==========
            else if (actionText.contains("Place in Lost Pile")) {
                action.addReasoning("Avoid losing cards", VERY_BAD_DELTA);
            }

            // ========== Grab ==========
            else if (actionText.contains("Grab")) {
                evaluateGrab(action, context, actionText);
            }

            // ========== Break Cover ==========
            else if (actionText.contains("Break cover")) {
                evaluateBreakCover(action, context, actionText);
            }

            // ========== Retrieve Force ==========
            else if (textLower.contains("retrieve") || actionText.contains("Place out of play to retrieve")) {
                int lostPileSize = gameState != null ? gameState.getLostPile(context.getPlayerId()).size() : 0;
                if (lostPileSize > 15) {
                    action.addReasoning("High lost pile - retrieve worth it", GOOD_DELTA);
                } else {
                    action.addReasoning("Low lost pile - save retrieve", BAD_DELTA);
                }
            }

            // ========== Defensive Shields ==========
            else if (actionText.contains("Play a Defensive Shield")) {
                if (!context.isMyTurn()) {
                    action.addReasoning("Defensive shield during opponent's turn - prefer pass", -10.0f);
                } else {
                    action.addReasoning("Defensive shield", VERY_GOOD_DELTA);
                }
            }

            // ========== Deploy on table/location ==========
            else if (actionText.startsWith("Deploy on")) {
                if (textLower.contains("projection") && textLower.contains("side")) {
                    action.addReasoning("Never put projection on side of table", VERY_BAD_DELTA);
                } else {
                    action.addReasoning("Deploy on location/table", GOOD_DELTA);
                }
            }

            // ========== Deploy unique ==========
            else if (actionText.startsWith("Deploy unique")) {
                action.addReasoning("Special battleground deploy", GOOD_DELTA);
            }

            // ========== USED: Peek at top ==========
            else if (actionText.startsWith("USED: Peek at top")) {
                action.addReasoning("Peek for card advantage", GOOD_DELTA);
            }

            // ========== Force Drain Cancellation ==========
            else if (actionText.contains("Cancel Force drain")) {
                if (context.isMyTurn()) {
                    action.addReasoning("Don't cancel own force drain", VERY_BAD_DELTA);
                } else {
                    action.addReasoning("Cancel opponent's force drain", GOOD_DELTA);
                }
            }

            // ========== Use/Lose Force Actions ==========
            else if (textLower.startsWith("use ") && textLower.contains(" force ")) {
                // Skew toward not using force unless necessary
                float randomDelta = (float) (Math.random() < 0.7 ?
                    Math.random() * -35.0 - 5.0 :  // -40 to -5
                    Math.random() * 25.0 - 5.0);   // -5 to +20
                action.addReasoning("'Use Force' action - randomized", randomDelta);
            }
            else if (textLower.startsWith("lose ") && textLower.contains(" force ")) {
                float randomDelta = (float) (Math.random() < 0.85 ?
                    Math.random() * -40.0 - 10.0 :  // -50 to -10
                    Math.random() * 20.0 - 10.0);   // -10 to +10
                action.addReasoning("'Lose Force' action - strong skew pass", randomDelta);
            }

            // ========== Default/Unknown ==========
            else {
                action.addReasoning("Unknown action type", 0.0f);
                logger.trace("Unrecognized action: {}", actionText);
            }

            actions.add(action);
        }

        return actions;
    }

    // ========== Helper Methods ==========

    private void evaluateActivateForce(EvaluatedAction action, DecisionContext context) {
        GameState gameState = context.getGameState();
        if (gameState == null) {
            action.addReasoning("Default activate", GOOD_DELTA);
            return;
        }

        String playerId = context.getPlayerId();
        int reserveSize = gameState.getReserveDeckSize(playerId);
        int forcePile = gameState.getForcePileSize(playerId);
        int usedPile = gameState.getUsedPile(playerId).size();
        int lifeForce = reserveSize + forcePile + usedPile;

        int maxForcePile = 20;
        int reserveForDestiny = lifeForce < 10 ? 2 : 3;

        boolean wouldActivateZero = false;
        String skipReason = null;

        // Check if force pile at cap
        if (forcePile >= maxForcePile) {
            wouldActivateZero = true;
            skipReason = "Force pile at max (" + forcePile + "/" + maxForcePile + ")";
        }
        // Check if reserve too low
        else if (reserveSize <= reserveForDestiny) {
            wouldActivateZero = true;
            skipReason = "Reserve (" + reserveSize + ") needed for destiny draws";
        }

        if (wouldActivateZero) {
            action.addReasoning("Skip activation: " + skipReason, BAD_DELTA);
        } else if (reserveSize < 5) {
            action.addReasoning("Reserve critically low (" + reserveSize + ") - save for destiny", BAD_DELTA);
        } else {
            action.addReasoning("Activate force (good)", VERY_GOOD_DELTA);
        }
    }

    private void evaluateForceDrain(EvaluatedAction action, DecisionContext context, String locationCardId) {
        // Force drains are generally good unless under Battle Order rules
        // Ported from Python action_text_evaluator.py lines 351-493

        GameState gameState = context.getGameState();
        String playerId = context.getPlayerId();

        // Check if we're under Battle Order rules (force drains cost +3 extra)
        // Battle Order is typically triggered when opponent has mains + specific cards
        boolean underBattleOrder = false;
        com.gempukku.swccgo.ai.models.rando.strategy.StrategyController strategyController = context.getStrategyController();
        if (strategyController != null) {
            underBattleOrder = strategyController.isUnderBattleOrderRules();
        }

        // Get available force
        int forceAvailable = 0;
        if (gameState != null) {
            forceAvailable = gameState.getForcePileSize(playerId);
        }

        // Check if we have any deployable cards in hand
        boolean hasDeployableCard = false;
        int cheapestDeployCost = Integer.MAX_VALUE;
        if (gameState != null) {
            List<PhysicalCard> hand = gameState.getHand(playerId);
            if (hand != null) {
                for (PhysicalCard card : hand) {
                    if (card.getBlueprint() != null) {
                        CardCategory category = card.getBlueprint().getCardCategory();
                        if (category == CardCategory.CHARACTER || category == CardCategory.STARSHIP ||
                            category == CardCategory.VEHICLE) {
                            hasDeployableCard = true;
                            Float deployCost = card.getBlueprint().getDeployCost();
                            if (deployCost != null && deployCost < cheapestDeployCost) {
                                cheapestDeployCost = deployCost.intValue();
                            }
                        }
                    }
                }
            }
        }

        if (underBattleOrder) {
            // Under Battle Order rules - force drains cost extra (+3)
            int battleOrderCost = 3;

            // If we can't afford the drain (need 3+ force), skip it
            if (forceAvailable < battleOrderCost) {
                action.addReasoning("Under Battle Order but can't afford drain (need " + battleOrderCost + ", have " + forceAvailable + ")", VERY_BAD_DELTA);
                return;
            }

            // Check if we have deployable cards - if yes, save force for them
            if (hasDeployableCard && cheapestDeployCost < Integer.MAX_VALUE) {
                int forceAfterDrain = forceAvailable - battleOrderCost;
                if (forceAfterDrain < cheapestDeployCost) {
                    action.addReasoning("Under Battle Order - saving force for deploy (cost " + cheapestDeployCost + ")", VERY_BAD_DELTA);
                    return;
                }
            }

            // If NO deployable cards - drains are our only pressure! Boost them!
            if (!hasDeployableCard) {
                action.addReasoning("Under Battle Order but NO deployable cards - drain is our only pressure!", VERY_GOOD_DELTA + 20.0f);
                logger.info("🔥 FORCE DRAIN BOOST: No deployable cards under Battle Order");
                return;
            }

            // We can afford drain and have some force left - moderate score
            action.addReasoning("Under Battle Order - drain costs extra but affordable", GOOD_DELTA);

        } else {
            // Not under Battle Order - drain is generally good
            if (!hasDeployableCard) {
                // NO deployable cards - drains are our only pressure!
                action.addReasoning("Force drain (no deployable cards - our only pressure!)", VERY_GOOD_DELTA + 20.0f);
                logger.info("🔥 FORCE DRAIN BOOST: No deployable cards");
            } else {
                action.addReasoning("Force drain is good", VERY_GOOD_DELTA);
            }
        }
    }

    private void evaluatePlayCard(EvaluatedAction action, DecisionContext context) {
        int forcePile = context.getForcePileSize();
        if (forcePile == 0) {
            action.addReasoning("No Force available - can't play cards!", VERY_BAD_DELTA);
        } else if (forcePile <= 1) {
            action.addReasoning("Very low Force (" + forcePile + ") - unlikely to afford cards", BAD_DELTA);
        } else {
            // Randomize to avoid loops
            float randomDelta = (float) (Math.random() * 30.0 - 15.0);
            action.addReasoning("Generic play card - randomized", randomDelta);
        }
    }

    private void evaluateDestinyProtection(EvaluatedAction action, DecisionContext context) {
        Phase phase = context.getPhase();
        int turnNumber = context.getTurnNumber();

        // These cards only useful if battle is coming
        if (turnNumber <= 1) {
            action.addReasoning("SAVE for battle turn! Turn 1 rarely battles", VERY_BAD_DELTA);
        } else if (phase == Phase.BATTLE) {
            action.addReasoning("Protect destiny draws - IN BATTLE NOW!", VERY_GOOD_DELTA);
        } else if (phase == Phase.ACTIVATE || phase == Phase.CONTROL || phase == Phase.DEPLOY) {
            action.addReasoning("Protect destiny draws - battle opportunity exists", GOOD_DELTA);
        } else {
            action.addReasoning("Save destiny protection for clear battle turn", BAD_DELTA);
        }
    }

    private void evaluateSenseCancel(EvaluatedAction action, DecisionContext context, String actionText) {
        String textLower = actionText.toLowerCase();
        boolean isDestinyBased = textLower.contains("draw destiny") || textLower.contains("if destiny");

        // Check priority cards system for target value
        AiPriorityCards.SenseTargetResult senseResult = AiPriorityCards.getSenseTargetValue(actionText);

        if (isDestinyBased) {
            if (senseResult.isHighValue && senseResult.score >= 80) {
                action.addReasoning("Destiny cancel critical target: " + senseResult.matchedPattern, 10.0f);
            } else {
                action.addReasoning("Destiny-based cancel (unreliable, skip)", -10.0f);
            }
        } else if (senseResult.isHighValue && senseResult.score >= 80) {
            action.addReasoning("Cancel CRITICAL target: " + senseResult.matchedPattern + "!", VERY_GOOD_DELTA + 20.0f);
        } else if (senseResult.isHighValue && senseResult.score >= 60) {
            action.addReasoning("Cancel high-value target: " + senseResult.matchedPattern, VERY_GOOD_DELTA);
        } else if (senseResult.isHighValue) {
            action.addReasoning("Cancel valuable target: " + senseResult.matchedPattern, GOOD_DELTA + 15.0f);
        } else if (textLower.contains("force drain")) {
            action.addReasoning("Cancel force drain", GOOD_DELTA + 5.0f);
        } else if (!context.isMyTurn()) {
            action.addReasoning("Cancel opponent interrupt (their turn)", GOOD_DELTA);
        } else {
            action.addReasoning("Cancel opponent interrupt (our turn)", 15.0f);
        }
    }

    private void evaluateHoujixGhhhk(EvaluatedAction action, DecisionContext context) {
        // These are CRITICAL survival cards
        // For now, give moderate positive score - ideally we'd check damage remaining
        action.addReasoning("Cancel battle damage - valuable survival card", GOOD_DELTA);

        // TODO: Add proper damage analysis when we have access to battle state
        // Check attrition/damage remaining and cards available to forfeit
    }

    private void evaluateTakeIntoHand(EvaluatedAction action, DecisionContext context, String actionText, String textLower) {
        if (textLower.contains("palpatine")) {
            action.addReasoning("Avoid taking Palpatine", BAD_DELTA);
        } else {
            String blueprintId = extractBlueprintFromText(actionText);
            if (blueprintId != null) {
                // Could look up card metadata here if needed
                action.addReasoning("Take card into hand", GOOD_DELTA);
            } else {
                action.addReasoning("Taking card into hand", GOOD_DELTA);
            }
        }
    }

    /**
     * Evaluate barrier card (Imperial/Rebel Barrier) usage.
     * Ported from Python action_text_evaluator.py lines 973-1055
     *
     * Use barriers when:
     *   - Location IS contested (both players present)
     *   - Target is a significant threat (high power)
     *   - We're not already winning overwhelmingly
     * Save barriers when:
     *   - Location not contested (no point)
     *   - We're already dominating the location
     *   - Target already has a barrier on it this turn!
     */
    private void evaluateBarrier(EvaluatedAction action, DecisionContext context, String actionText) {
        String targetCardName = extractCardNameFromPreventText(actionText);
        int currentTurn = context.getTurnNumber();

        // Reset barrier tracking on new turn
        if (currentTurn != barrierTurn) {
            barrieredTargets.clear();
            barrierTurn = currentTurn;
        }

        // Check if we already barriered this target
        if (targetCardName != null && barrieredTargets.contains(targetCardName.toLowerCase())) {
            action.addReasoning("Already barriered " + targetCardName + " this turn - wasteful!", VERY_BAD_DELTA);
            return;
        }

        // Try to analyze the target and location
        GameState gameState = context.getGameState();
        String playerId = context.getPlayerId();
        float targetPower = 0;
        float ourPower = 0;
        float theirPower = 0;
        boolean locationContested = false;

        if (gameState != null && playerId != null && targetCardName != null) {
            String opponentId = gameState.getOpponent(playerId);

            // Find the target card and analyze location
            for (PhysicalCard card : gameState.getAllPermanentCards()) {
                if (card == null) continue;
                String title = card.getTitle();
                if (title == null) continue;

                // Match by name
                if (title.toLowerCase().contains(targetCardName.toLowerCase()) ||
                    targetCardName.toLowerCase().contains(title.toLowerCase())) {

                    // Found the target - check its power
                    SwccgCardBlueprint blueprint = card.getBlueprint();
                    if (blueprint != null && blueprint.hasPowerAttribute()) {
                        Float power = blueprint.getPower();
                        if (power != null) {
                            targetPower = power;
                        }
                    }

                    // Find location and calculate power
                    PhysicalCard location = card.getAtLocation();
                    if (location != null) {
                        boolean hasOurPresence = false;
                        boolean hasTheirPresence = false;

                        for (PhysicalCard locCard : gameState.getCardsAtLocation(location)) {
                            if (locCard == null) continue;
                            String owner = locCard.getOwner();
                            SwccgCardBlueprint bp = locCard.getBlueprint();
                            if (bp == null) continue;

                            // Check presence
                            if (playerId.equals(owner)) {
                                hasOurPresence = true;
                                if (bp.hasPowerAttribute()) {
                                    Float power = bp.getPower();
                                    if (power != null) ourPower += power;
                                }
                            } else if (opponentId != null && opponentId.equals(owner)) {
                                hasTheirPresence = true;
                                if (bp.hasPowerAttribute()) {
                                    Float power = bp.getPower();
                                    if (power != null) theirPower += power;
                                }
                            }
                        }
                        locationContested = hasOurPresence && hasTheirPresence;
                    }
                    break;
                }
            }
        }

        logger.debug("🚧 Barrier analysis: {} (power {}) contested={}, our={}, their={}",
            targetCardName, targetPower, locationContested, ourPower, theirPower);

        // Apply scoring based on situation
        if (!locationContested) {
            // Location NOT contested - save barrier for when we need it
            action.addReasoning("Save barrier - location not contested", BAD_DELTA);
        } else if (ourPower >= theirPower + 8) {
            // We're already dominating - don't waste the barrier
            action.addReasoning("Save barrier - already dominating (" + (int)ourPower + " vs " + (int)theirPower + ")", BAD_DELTA);
        } else if (targetPower >= 5) {
            // High-power target at contested location - VERY valuable!
            action.addReasoning("Barrier on HIGH POWER target (" + (int)targetPower + ")!", VERY_GOOD_DELTA);
            if (targetCardName != null) {
                barrieredTargets.add(targetCardName.toLowerCase());
            }
        } else if (theirPower >= ourPower) {
            // They're winning or tied - barrier is valuable
            action.addReasoning("Barrier to protect (losing " + (int)ourPower + " vs " + (int)theirPower + ")", GOOD_DELTA + 10.0f);
            if (targetCardName != null) {
                barrieredTargets.add(targetCardName.toLowerCase());
            }
        } else {
            // We're ahead but not dominating - still useful
            action.addReasoning("Barrier at contested location", GOOD_DELTA);
            if (targetCardName != null) {
                barrieredTargets.add(targetCardName.toLowerCase());
            }
        }
    }

    private void evaluateEmbark(EvaluatedAction action, DecisionContext context, String actionText) {
        String blueprintId = extractBlueprintFromText(actionText);

        // Embarking pilots onto ships is good, non-pilots is usually bad
        // For now, give neutral score - could be improved with pilot detection
        action.addReasoning("Embark action", 0.0f);
    }

    private void evaluateGrab(EvaluatedAction action, DecisionContext context, String actionText) {
        // Grabbing opponent's card is good, our own is VERY bad
        // CRITICAL: Grabbing own interrupts is a big player complaint - hard block it!
        // Ported from Python action_text_evaluator.py lines 1169-1210

        Side mySide = context.getSide();

        // Determine side from card name patterns in action text
        // Look for known Light/Dark side indicator patterns
        String textLower = actionText.toLowerCase();
        boolean looksLightSide = textLower.contains("rebel") || textLower.contains("jedi") ||
                                  textLower.contains("alliance") || textLower.contains("luke") ||
                                  textLower.contains("leia") || textLower.contains("han solo") ||
                                  textLower.contains("chewie") || textLower.contains("yoda") ||
                                  textLower.contains("obi-wan") || textLower.contains("padme");
        boolean looksDarkSide = textLower.contains("imperial") || textLower.contains("sith") ||
                                 textLower.contains("vader") || textLower.contains("emperor") ||
                                 textLower.contains("stormtrooper") || textLower.contains("death star") ||
                                 textLower.contains("maul") || textLower.contains("dooku") ||
                                 textLower.contains("boba fett") || textLower.contains("jango");

        if (mySide == Side.DARK && looksLightSide) {
            action.addReasoning("Grab Light side card (we are Dark)", GOOD_DELTA);
        } else if (mySide == Side.LIGHT && looksDarkSide) {
            action.addReasoning("Grab Dark side card (we are Light)", GOOD_DELTA);
        } else if (mySide == Side.DARK && looksDarkSide) {
            // Same side - likely our card! HARD BLOCK!
            action.setScore(-500.0f);
            action.addReasoning("🚫 BLOCKED: Likely grabbing own Dark card!", -500.0f);
            logger.warn("🚫 BLOCKED GRAB of likely own Dark card: {}", actionText);
        } else if (mySide == Side.LIGHT && looksLightSide) {
            // Same side - likely our card! HARD BLOCK!
            action.setScore(-500.0f);
            action.addReasoning("🚫 BLOCKED: Likely grabbing own Light card!", -500.0f);
            logger.warn("🚫 BLOCKED GRAB of likely own Light card: {}", actionText);
        } else {
            // Truly unknown - be cautious, don't grab
            action.addReasoning("Grab card (owner unknown - avoiding)", BAD_DELTA);
            logger.info("⚠️ Grab owner unknown, avoiding: {}", actionText);
        }
    }

    private void evaluateBreakCover(EvaluatedAction action, DecisionContext context, String actionText) {
        // Breaking opponent's spy is good, our own is VERY bad
        // CRITICAL: Breaking own spy cover is a big player complaint - hard block it!
        // Ported from Python action_text_evaluator.py lines 1212-1246

        Side mySide = context.getSide();

        // Determine side from card name patterns in action text
        String textLower = actionText.toLowerCase();
        boolean looksLightSide = textLower.contains("rebel") || textLower.contains("bothan") ||
                                  textLower.contains("alliance") || textLower.contains("leia") ||
                                  textLower.contains("mon mothma") || textLower.contains("orrimaarko");
        boolean looksDarkSide = textLower.contains("imperial") || textLower.contains("ism-agent") ||
                                 textLower.contains("empire") || textLower.contains("probe droid") ||
                                 textLower.contains("mara jade");

        if (mySide == Side.DARK && looksLightSide) {
            action.addReasoning("Break Light side spy cover (we are Dark)", GOOD_DELTA);
        } else if (mySide == Side.LIGHT && looksDarkSide) {
            action.addReasoning("Break Dark side spy cover (we are Light)", GOOD_DELTA);
        } else if (mySide == Side.DARK && looksDarkSide) {
            // Same side - our spy! HARD BLOCK!
            action.setScore(-500.0f);
            action.addReasoning("🚫 BLOCKED: Likely breaking own Dark spy cover!", -500.0f);
            logger.warn("🚫 BLOCKED break cover of likely own Dark spy: {}", actionText);
        } else if (mySide == Side.LIGHT && looksLightSide) {
            // Same side - our spy! HARD BLOCK!
            action.setScore(-500.0f);
            action.addReasoning("🚫 BLOCKED: Likely breaking own Light spy cover!", -500.0f);
            logger.warn("🚫 BLOCKED break cover of likely own Light spy: {}", actionText);
        } else {
            // Unknown spy - be cautious, default to not doing it
            action.addReasoning("Break cover (spy owner unknown - cautious)", BAD_DELTA);
            logger.info("⚠️ Break cover owner unknown, avoiding: {}", actionText);
        }
    }

    // ========== Utility Methods ==========

    private String extractBlueprintFromText(String actionText) {
        if (actionText == null) return null;
        Matcher matcher = BLUEPRINT_PATTERN.matcher(actionText);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private String extractCardNameFromPreventText(String actionText) {
        // Pattern: "Prevent <CARD NAME> from battling or moving"
        if (actionText != null && actionText.contains("Prevent") &&
            actionText.contains("from battling or moving")) {
            int startIdx = actionText.indexOf("Prevent") + "Prevent ".length();
            int endIdx = actionText.indexOf(" from battling or moving");
            if (startIdx > 0 && endIdx > startIdx) {
                return actionText.substring(startIdx, endIdx).trim();
            }
        }
        return null;
    }

    /**
     * Check if there are valid (non-HIT) weapon targets at the battle location.
     *
     * In SWCCG, firing at already-hit targets is wasteful since they're
     * already damaged. This method returns true only if there are unhit
     * enemy cards at the battle location.
     *
     * Ported from Python action_text_evaluator.py valid target check.
     */
    private boolean checkForValidWeaponTargets(DecisionContext context) {
        GameState gameState = context.getGameState();
        if (gameState == null) {
            return true;  // Default to allowing fire if we can't check
        }

        try {
            // Get the battle location
            PhysicalCard battleLocation = gameState.getBattleLocation();
            if (battleLocation == null) {
                return true;  // Not in battle, allow fire
            }

            // Find enemy cards at battle location
            String playerId = context.getPlayerId();
            String opponentId = gameState.getOpponent(playerId);
            if (opponentId == null) {
                return true;  // Can't determine opponent
            }

            // Check all enemy cards at battle location
            boolean foundUnhitEnemy = false;
            for (PhysicalCard card : gameState.getAllPermanentCards()) {
                if (card == null) continue;

                // Must be enemy card
                if (!opponentId.equals(card.getOwner())) continue;

                // Must be at battle location
                PhysicalCard cardLocation = card.getAtLocation();
                if (cardLocation == null || !cardLocation.equals(battleLocation)) continue;

                // Must be a valid weapon target (character, starship, vehicle)
                SwccgCardBlueprint bp = card.getBlueprint();
                if (bp == null) continue;
                CardCategory cat = bp.getCardCategory();
                if (cat != CardCategory.CHARACTER && cat != CardCategory.STARSHIP && cat != CardCategory.VEHICLE) {
                    continue;
                }

                // Check if this card is NOT hit
                if (!card.isHit()) {
                    foundUnhitEnemy = true;
                    logger.debug("Found unhit enemy target: {}", card.getTitle());
                    break;  // Found at least one valid target
                }
            }

            if (!foundUnhitEnemy) {
                logger.info("🎯 All enemy targets at battle location are HIT - no valid weapon targets");
            }

            return foundUnhitEnemy;

        } catch (Exception e) {
            logger.debug("Error checking weapon targets: {}", e.getMessage());
            return true;  // Default to allowing fire on error
        }
    }
}
