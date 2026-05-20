package com.gempukku.swccgo.ai.models.chosenone.evaluators;

import com.gempukku.swccgo.ai.models.chosenone.ChosenOneConfig;
import com.gempukku.swccgo.ai.models.chosenone.strategy.DeployPhasePlanner;
import com.gempukku.swccgo.ai.models.chosenone.strategy.DeployStrategy;
import com.gempukku.swccgo.ai.models.chosenone.strategy.DeploymentPlan;
import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Draw Evaluator
 *
 * Handles card draw decisions.
 * Ported from Python draw_evaluator.py (~620 lines)
 *
 * Decision factors:
 * - Hand size (target ~7-8 cards, soft cap 12, hard cap 16)
 * - Reserve deck status (don't deck out)
 * - Force pile available
 * - Strategy (deploy vs hold)
 * - Force generation deficit (draw to find locations if low gen)
 * - Future turn planning (save force for expensive cards)
 * - Late-game life force preservation
 * - Hold-back draw logic (draw aggressively when couldn't deploy)
 * - Force-starved strategy (save force when activation is low)
 * - Contested location force reservation
 */
public class DrawEvaluator extends ActionEvaluator {

    // Rank deltas
    private static final float VERY_GOOD_DELTA = 150.0f;
    private static final float GOOD_DELTA = 10.0f;
    private static final float BAD_DELTA = -10.0f;
    private static final float VERY_BAD_DELTA = -150.0f;

    // Draw thresholds
    private static final int TARGET_HAND_SIZE = 7;
    private static final int LOW_RESERVE_THRESHOLD = 6;
    private static final int SMALL_HAND_THRESHOLD = 5;
    private static final int AGGRESSIVE_FORCE_THRESHOLD = 10;
    private static final int LATE_GAME_LIFE_FORCE = 12;
    private static final int CRITICAL_LIFE_FORCE = 6;
    private static final int EXPENSIVE_CARD_THRESHOLD = 8;

    // Hold-back draw thresholds (from Python)
    private static final int HOLD_BACK_DRAW_FORCE_THRESHOLD = 6;
    private static final int HOLD_BACK_DRAW_LIFE_THRESHOLD = 10;
    private static final int HOLD_BACK_DRAW_FORCE_FLOOR = 6;

    // Force-starved thresholds (from Python)
    private static final int FORCE_STARVED_ACTIVATION = 8;
    private static final int FORCE_STARVED_POWER_THRESHOLD = 6;
    private static final int FORCE_STARVED_MAX_HAND = 8;

    public DrawEvaluator() {
        super("Draw");
    }

    @Override
    public boolean canEvaluate(DecisionContext context) {
        String decisionType = context.getDecisionType();

        // Only handle CARD_ACTION_CHOICE or ACTION_CHOICE
        if (!"CARD_ACTION_CHOICE".equals(decisionType) && !"ACTION_CHOICE".equals(decisionType)) {
            return false;
        }

        // CRITICAL: Only evaluate during OUR turn
        if (!context.isMyTurn()) {
            logger.trace("DrawEvaluator skipping - not our turn");
            return false;
        }

        // CRITICAL: Only evaluate during Draw phase
        // "Draw destiny" is NOT drawing cards - it's a random number mechanic
        Phase phase = context.getPhase();
        if (phase != Phase.DRAW) {
            logger.trace("DrawEvaluator skipping - not draw phase (phase={})", phase);
            return false;
        }

        // Check for draw actions in the decision
        String decisionLower = (context.getDecisionText() != null ? context.getDecisionText() : "").toLowerCase();
        if (decisionLower.contains("draw") && decisionLower.contains("action")) {
            logger.debug("DrawEvaluator triggered (our turn, draw phase): '{}'", context.getDecisionText());
            return true;
        }

        // Check if any action is a draw action (but not destiny draw)
        for (String actionText : context.getActionTexts()) {
            String actionLower = actionText.toLowerCase();
            if (actionLower.contains("draw") && !actionLower.contains("destiny")) {
                logger.debug("DrawEvaluator triggered by action: '{}'", actionText);
                return true;
            }
        }

        return false;
    }

    @Override
    public List<EvaluatedAction> evaluate(DecisionContext context) {
        List<EvaluatedAction> actions = new ArrayList<>();
        List<String> actionIds = context.getActionIds();
        List<String> actionTexts = context.getActionTexts();
        Set<String> blocked = context.getBlockedResponses();

        for (int i = 0; i < actionIds.size(); i++) {
            String actionId = actionIds.get(i);
            String actionText = i < actionTexts.size() ? actionTexts.get(i) : "";
            String actionLower = actionText.toLowerCase();

            // Only evaluate draw actions (not destiny draws)
            if (!actionLower.contains("draw")) {
                continue;
            }
            if (actionLower.contains("destiny")) {
                logger.trace("Skipping destiny draw action: '{}'", actionText);
                continue;
            }

            logger.debug("Evaluating draw action: '{}' (id={})", actionText, actionId);

            EvaluatedAction action = new EvaluatedAction(actionId, ActionType.DRAW, 0.0f, actionText);

            // Check if this response is blocked (loop prevention)
            if (blocked.contains(actionId) || blocked.contains(actionText)) {
                action.addReasoning("BLOCKED (loop prevention)", -200.0f);
            }

            // Rank the draw action
            rankDrawAction(action, context);

            actions.add(action);
        }

        return actions;
    }

    /**
     * Rank the draw action based on game state.
     *
     * CRITICAL: When life force is low, reduce max hand size proportionally.
     * Having 12 cards in hand but only 2 force to spend is TERRIBLE.
     *
     * Ported from Python draw_evaluator.py _rank_draw_action
     * Enhanced with hold-back draw, force-starved, and expensive card logic.
     */
    private void rankDrawAction(EvaluatedAction action, DecisionContext context) {
        GameState gameState = context.getGameState();
        if (gameState == null) {
            action.addReasoning("No board state - neutral draw", 0.0f);
            return;
        }

        String playerId = context.getPlayerId();
        int handSize = context.getHandSize();
        int reserveDeck = context.getReserveDeckSize();
        int usedPile = context.getUsedPileSize();
        int forcePile = context.getForcePileSize();
        int turnNumber = context.getTurnNumber();

        // Total remaining life force
        int remainingLifeForce = reserveDeck + usedPile + forcePile;

        // Get force generation for forward planning
        int forceGeneration = calculateForceGeneration(context);

        // === V42: EMERGENCY DRAW — empty hand is a death spiral ===
        // If hand is 0-2, we MUST draw regardless of other considerations.
        // An empty hand means no deploys, no interrupts, no responses — game over.
        if (handSize <= 2 && forcePile >= 1 && reserveDeck >= 2) {
            float emergencyBonus = (3 - handSize) * 200.0f; // 0 cards = +600, 1 = +400, 2 = +200
            action.addReasoning(String.format(
                "V42 EMERGENCY DRAW: Hand has only %d cards — MUST draw to stay in the game!", handSize),
                emergencyBonus);
            logger.warn("V42 EMERGENCY DRAW: hand={}, force={}, reserve={} — bonus +{}",
                handSize, forcePile, reserveDeck, emergencyBonus);
        }

        // === CRITICAL: LIFE FORCE BASED HAND LIMIT ===
        if (remainingLifeForce < CRITICAL_LIFE_FORCE) {
            float penalty = VERY_BAD_DELTA * 0.8f;
            action.addReasoning("CRITICAL life force (" + remainingLifeForce + ") - minimize draws", penalty);
            // Still allow draws if hand is truly empty
            if (handSize >= 2) {
                return;
            }
        }

        // Late game - be more strategic
        if (remainingLifeForce < LATE_GAME_LIFE_FORCE) {
            float penaltyScale = (LATE_GAME_LIFE_FORCE - remainingLifeForce) / (float) LATE_GAME_LIFE_FORCE;
            action.addReasoning("Late game (" + remainingLifeForce + " life force) - draw carefully",
                               BAD_DELTA * 2 * penaltyScale);
        }

        // Effective max hand based on life force
        int effectiveMaxHand = ChosenOneConfig.MAX_HAND_SIZE;
        if (remainingLifeForce < ChosenOneConfig.MAX_HAND_SIZE) {
            effectiveMaxHand = Math.max(2, remainingLifeForce);
            logger.trace("Life force {} < {}: effective max hand = {}",
                        remainingLifeForce, ChosenOneConfig.MAX_HAND_SIZE, effectiveMaxHand);
        }

        // If hand already exceeds effective max, STRONGLY penalize drawing
        if (handSize >= effectiveMaxHand) {
            action.addReasoning("CRITICAL: Hand " + handSize + " >= life force limit " + effectiveMaxHand, VERY_BAD_DELTA);
            return;
        }

        // === NON-STRATEGIC HOLD-BACK: DRAW TO FIND OPTIONS ===
        // If we held back this turn because we COULDN'T deploy (not strategic save),
        // draw aggressively to find new options. This prevents sitting with
        // undeployable cards while having plenty of force.
        applyHoldBackDrawLogic(action, context, forcePile, remainingLifeForce, handSize);

        // === FUTURE TURN PLANNING: EXPENSIVE CARDS ===
        // Save force for expensive cards in hand
        applyExpensiveCardLogic(action, context, forcePile, remainingLifeForce, forceGeneration);

        // === FORCE-STARVED STRATEGY ===
        // When activation is low (<8/turn), hoarding cards is counterproductive.
        // If we have enough power in hand, stop drawing and SAVE force for deployment.
        if (applyForceStarvedLogic(action, context, forcePile, forceGeneration, handSize)) {
            // If force-starved logic strongly suggests not drawing, exit early
            return;
        }

        // === V24.10: DIG FOR PIETT — KEY ENGINE CARD ===
        // Piett is THE critical pilot for TDIGWATT's Executor engine.
        // If Piett isn't in hand or reserve, he's stuck in the force pile — draw to find him.
        // The earlier we find Piett, the earlier Executor gets on the table via AMSD.
        com.gempukku.swccgo.ai.models.chosenone.strategy.DeckOracle drawOracle = context.getDeckOracle();
        if (drawOracle != null && drawOracle.isAnalyzed()) {
            boolean piettInHand = drawOracle.isCardInHand("Admiral Piett") || drawOracle.isCardInHand("Piett");
            boolean piettInReserve = drawOracle.isCardInReserve("Admiral Piett") || drawOracle.isCardInReserve("Piett");
            boolean piettInPlay = drawOracle.isCardInPlay("Admiral Piett") || drawOracle.isCardInPlay("Piett");
            boolean piettLost = drawOracle.isCardLost("Admiral Piett") || drawOracle.isCardLost("Piett");

            if (!piettInHand && !piettInReserve && !piettInPlay && !piettLost) {
                // Piett is in the force pile (or used pile cycling back) — DRAW to find him!
                float piettBonus = 0.0f;
                if (turnNumber <= 2) {
                    piettBonus = 200.0f;  // Turns 1-2: maximum urgency — Executor MUST come out
                } else if (turnNumber <= 4) {
                    piettBonus = 150.0f;  // Turns 3-4: still very important
                } else {
                    piettBonus = 80.0f;   // Later turns: still worth finding
                }
                action.addReasoning("V24.10 DIG FOR PIETT: Not in hand/reserve — must be in force pile! Draw to find him!", piettBonus);
                logger.warn("V24.10 PIETT DIG: Piett not in hand/reserve/play/lost — drawing aggressively to find him! (+{})", piettBonus);
            }
        }

        // === BASELINE: DRAW TOWARDS DYNAMIC SOFT CAP ===
        String phaseNote = turnNumber <= 3 ? "early" : (turnNumber <= 6 ? "mid" : "late");

        // Dynamic soft cap based on turn
        int effectiveSoftCap;
        if (turnNumber <= 3) {
            effectiveSoftCap = ChosenOneConfig.HAND_SOFT_CAP + 4;  // 16
        } else if (turnNumber <= 6) {
            effectiveSoftCap = ChosenOneConfig.HAND_SOFT_CAP;  // 12
        } else {
            effectiveSoftCap = ChosenOneConfig.HAND_SOFT_CAP - 4;  // 8
        }

        if (handSize < effectiveSoftCap && remainingLifeForce >= LATE_GAME_LIFE_FORCE) {
            int cardsBelowCap = effectiveSoftCap - handSize;
            // Strong baseline bonus for drawing toward cap
            float baselineBonus = Math.max(30.0f, 8.0f * cardsBelowCap);
            action.addReasoning("Hand " + handSize + " below " + phaseNote + "-game cap " + effectiveSoftCap + " - draw!",
                               baselineBonus);
            logger.debug("Draw baseline: hand {} < {} cap {}, +{}", handSize, phaseNote, effectiveSoftCap, baselineBonus);
        }

        // === FORCE RESERVATION FOR OPPONENT'S TURN ===
        // V58: Reserve accurate to Steve's rules (DTF, First Strike,
        // maintenance, contested). Aggressively draw the surplus.
        int forceToReserve = calculateForceToReserve(context, handSize);
        int drawableSurplus = Math.max(0, forcePile - forceToReserve);
        if (drawableSurplus > 0 && handSize < effectiveMaxHand && reserveDeck > 2) {
            float surplusBonus = Math.min(400.0f, 80.0f * drawableSurplus);
            action.addReasoning(String.format(
                "V58 DRAW-DOWN: force pile %d > reserve %d — draw %d surplus into hand!",
                forcePile, forceToReserve, drawableSurplus), surplusBonus);
            logger.warn("V58 DRAW-DOWN: pile={}, reserve={}, surplus={} → +{}",
                forcePile, forceToReserve, drawableSurplus, (int)surplusBonus);
        }
        if (forcePile <= forceToReserve && turnNumber >= 4) {
            action.addReasoning("V58 HOLD RESERVE: force pile " + forcePile +
                " at/below reserve target " + forceToReserve + " — keep it",
                BAD_DELTA * 1.5f);
        }

        // Don't draw if low reserve (avoid decking)
        if (reserveDeck <= LOW_RESERVE_THRESHOLD) {
            float penalty = BAD_DELTA * (LOW_RESERVE_THRESHOLD - reserveDeck);
            action.addReasoning("Low reserve (" + reserveDeck + ") - avoid drawing", penalty);
        }

        // Draw if hand is smaller than target and enough reserve
        if (handSize < TARGET_HAND_SIZE && reserveDeck > 10 && forcePile > 1) {
            if (remainingLifeForce >= LATE_GAME_LIFE_FORCE) {
                action.addReasoning("Hand size " + handSize + " < " + TARGET_HAND_SIZE + " - draw to fill", GOOD_DELTA);
            }
        }

        // Draw if hand is very small
        if (handSize <= SMALL_HAND_THRESHOLD && reserveDeck > 4 && forcePile > 1) {
            action.addReasoning("Small hand (" + handSize + ") - draw cards", GOOD_DELTA);
        }

        // Aggressive draw if high force and good life force
        if (forcePile > AGGRESSIVE_FORCE_THRESHOLD && remainingLifeForce >= LATE_GAME_LIFE_FORCE) {
            action.addReasoning("High force pile (" + forcePile + ") - YOLO draw", GOOD_DELTA);
        }

        // Weak hand - draw even on hold
        if (forcePile > 5 && handSize <= 4) {
            action.addReasoning("Weak hand - draw even on hold", GOOD_DELTA);
        }

        // Hand size penalty for exceeding soft cap
        if (handSize >= effectiveSoftCap) {
            int overflow = handSize - effectiveSoftCap;
            action.addReasoning("Hand above " + phaseNote + "-game cap (" + handSize + "/" + effectiveSoftCap + ")",
                               BAD_DELTA * overflow * 0.5f);
        }

        // Save last force
        if (forcePile == 1) {
            action.addReasoning("Last force - save it", BAD_DELTA);
        }
    }

    /**
     * Calculate current force generation (icons at our locations).
     */
    private int calculateForceGeneration(DecisionContext context) {
        SwccgGame game = context.getGame();
        if (game == null) {
            return 1;  // Default to 1 (we generate 1 ourselves)
        }

        GameState gameState = context.getGameState();
        String playerId = context.getPlayerId();

        // Sum force icons at locations where we have presence
        // This is an approximation - actual calculation is complex
        int totalIcons = 1;  // Base generation

        try {
            ModifiersQuerying modifiers = game.getModifiersQuerying();
            if (modifiers != null) {
                // Use the modifiers system to calculate total force generation
                // For now, estimate based on locations in play
                Collection<PhysicalCard> locations = gameState.getLocationsInOrder();
                for (PhysicalCard loc : locations) {
                    if (loc == null) continue;

                    // Check if we have presence at this location
                    // For simplicity, count force icons on our side
                    SwccgCardBlueprint bp = loc.getBlueprint();
                    if (bp != null) {
                        if (context.getSide() == com.gempukku.swccgo.common.Side.DARK) {
                            Integer darkIcons = bp.getIconCount(com.gempukku.swccgo.common.Icon.DARK_FORCE);
                            if (darkIcons != null) totalIcons += darkIcons;
                        } else {
                            Integer lightIcons = bp.getIconCount(com.gempukku.swccgo.common.Icon.LIGHT_FORCE);
                            if (lightIcons != null) totalIcons += lightIcons;
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.trace("Error calculating force generation: {}", e.getMessage());
        }

        return Math.max(1, totalIcons);
    }

    /**
     * Calculate force to reserve for opponent's turn.
     *
     * V58 FIX 20 (2026-04-16): Match Steve's actual reservation rules.
     *   +1 if opponent has Draw Their Fire on table
     *   +1 if opponent has First Strike on table
     *   +1 mid/late game for battle interrupts
     *   +N for total maintenance cost of our on-table characters
     *   +1 per contested location (at least one)
     *   Hard cap at 4.
     */
    private int calculateForceToReserve(DecisionContext context, int handSize) {
        int forceToReserve = 0;
        SwccgGame game = context.getGame();
        if (game == null) return 1;

        GameState gameState = context.getGameState();
        String playerId = context.getPlayerId();
        int turnNumber = context.getTurnNumber();

        try {
            int contestedCount = 0;
            int maintenanceCost = 0;
            boolean opponentHasDTF = false;
            boolean opponentHasFirstStrike = false;
            boolean opponentHasIAO = false;  // V78: Imperial Arrest Order
            boolean ourVergeNeedsDeathStarMove = false;  // V79

            Collection<PhysicalCard> locations = gameState.getLocationsInOrder();
            for (PhysicalCard loc : locations) {
                if (loc == null) continue;
                Collection<PhysicalCard> cardsAtLoc = gameState.getCardsAtLocation(loc);
                boolean weHavePresence = false;
                boolean theyHavePresence = false;
                for (PhysicalCard card : cardsAtLoc) {
                    if (card.getOwner() != null && card.getOwner().equals(playerId)) {
                        weHavePresence = true;
                    } else {
                        theyHavePresence = true;
                    }
                }
                if (weHavePresence && theyHavePresence) contestedCount++;
            }

            for (PhysicalCard pc : gameState.getAllPermanentCards()) {
                if (pc == null) continue;
                String title = pc.getTitle();
                if (title == null) continue;
                String titleLower = title.toLowerCase(java.util.Locale.ROOT);
                boolean isOurs = pc.getOwner() != null && pc.getOwner().equals(playerId);
                if (!isOurs) {
                    if (titleLower.contains("draw their fire"))   opponentHasDTF = true;
                    if (titleLower.contains("first strike"))      opponentHasFirstStrike = true;
                    // V78 (Steve, 2026-05-15): Imperial Arrest Order awareness.
                    if (titleLower.contains("imperial arrest")
                            || titleLower.contains("secret plans")) opponentHasIAO = true;
                } else {
                    // V67w (Steve, 2026-05-03): Use the engine's Icon.MAINTENANCE
                    // instead of hand-rolled title matching. SWCCG marks every
                    // maintenance character with this icon. OLD code only matched
                    // "lando calrissian, scoundrel" — missed everything else.
                    try {
                        if (pc.getBlueprint() != null
                                && pc.getBlueprint().hasIcon(com.gempukku.swccgo.common.Icon.MAINTENANCE)) {
                            maintenanceCost += 1;
                        }
                    } catch (Exception e) { /* ignore */ }
                    // V79: detect Verge of Greatness on Rando's table + Death Star not at Scarif
                    if (titleLower.contains("on the verge of greatness")
                            || titleLower.contains("taking control of the weapon")) {
                        boolean dsAtScarif = false;
                        PhysicalCard rDeathStar = null;
                        for (PhysicalCard pc2 : gameState.getAllPermanentCards()) {
                            if (pc2 == null || !playerId.equals(pc2.getOwner())) continue;
                            String t2 = pc2.getTitle() != null ? pc2.getTitle().toLowerCase(java.util.Locale.ROOT) : "";
                            if (t2.contains("death star")
                                    && pc2.getBlueprint() != null
                                    && pc2.getBlueprint().getCardCategory() == CardCategory.LOCATION) {
                                rDeathStar = pc2;
                                PhysicalCard loc = pc2.getAtLocation();
                                if (loc != null && loc.getTitle() != null
                                        && loc.getTitle().toLowerCase(java.util.Locale.ROOT).contains("scarif")) {
                                    dsAtScarif = true;
                                }
                                break;
                            }
                        }
                        if (rDeathStar != null && !dsAtScarif) {
                            ourVergeNeedsDeathStarMove = true;
                        }
                    }
                }
            }

            if (opponentHasDTF)         forceToReserve += 1;
            if (opponentHasFirstStrike) forceToReserve += 1;
            if (contestedCount > 0)     forceToReserve += 1;
            if (turnNumber >= 4)        forceToReserve += 1;
            if (opponentHasIAO)         forceToReserve += 2;  // V78
            if (ourVergeNeedsDeathStarMove) forceToReserve += 1;  // V79
            forceToReserve += maintenanceCost;
            // V78: Bumped cap 8 → 10 to accommodate IAO tax.
            forceToReserve = Math.min(10, forceToReserve);

            logger.debug("V58 RESERVE: DTF={}, FirstStrike={}, IAO={}, contested={}, maint={}, turn={}, total={}",
                opponentHasDTF, opponentHasFirstStrike, opponentHasIAO, contestedCount, maintenanceCost, turnNumber, forceToReserve);
        } catch (Exception e) {
            logger.trace("V58 RESERVE error: {}", e.getMessage());
            return 1;
        }

        return forceToReserve;
    }

    /**
     * Apply hold-back draw logic.
     * If we held back due to inability (not strategic), draw aggressively.
     */
    private void applyHoldBackDrawLogic(EvaluatedAction action, DecisionContext context,
                                        int forcePile, int remainingLifeForce, int handSize) {
        DeployPhasePlanner planner = context.getDeployPhasePlanner();
        if (planner == null) {
            return;
        }

        DeploymentPlan plan = planner.getCurrentPlan();
        if (plan == null || plan.getStrategy() != DeployStrategy.HOLD_BACK) {
            return;
        }

        String holdReason = plan.getReason() != null ? plan.getReason().toLowerCase() : "";

        // Check if this is a NON-STRATEGIC hold (couldn't deploy vs choosing not to)
        // Strategic holds mention: "crush", "bleed", "early game", "saving"
        boolean isStrategicHold = holdReason.contains("crush") ||
                                  holdReason.contains("bleed") ||
                                  holdReason.contains("early game") ||
                                  holdReason.contains("saving") ||
                                  holdReason.contains("next-turn");

        if (!isStrategicHold) {
            // This is a "couldn't deploy" hold - check if we should draw
            if (forcePile > HOLD_BACK_DRAW_FORCE_THRESHOLD &&
                remainingLifeForce > HOLD_BACK_DRAW_LIFE_THRESHOLD &&
                handSize < ChosenOneConfig.MAX_HAND_SIZE) {

                // Calculate how many draws we can afford while keeping force floor
                int drawsAffordable = forcePile - HOLD_BACK_DRAW_FORCE_FLOOR;

                if (drawsAffordable > 0) {
                    // Boost drawing significantly - we need new options!
                    float drawBoost = 50.0f + (drawsAffordable * 5);
                    String reasonSummary = holdReason.length() > 50 ? holdReason.substring(0, 50) + "..." : holdReason;
                    action.addReasoning(
                        "HOLD-BACK DRAW: Couldn't deploy (" + reasonSummary + "), " +
                        "force " + forcePile + " > " + HOLD_BACK_DRAW_FORCE_THRESHOLD + ", " +
                        "drawing to find options",
                        drawBoost
                    );
                    logger.info("🎴 HOLD-BACK DRAW boost: +{} (reason: {})", drawBoost, reasonSummary);
                }
            }
        }
    }

    /**
     * Apply expensive card planning logic.
     * Save force for expensive cards (cost >= 8) in hand.
     */
    private void applyExpensiveCardLogic(EvaluatedAction action, DecisionContext context,
                                         int forcePile, int remainingLifeForce, int forceGeneration) {
        List<PhysicalCard> hand = context.getHand();
        if (hand == null || hand.isEmpty()) {
            return;
        }

        int maxDeployableCost = 0;
        int affordableCardsCount = 0;
        boolean expensiveCardInHand = false;

        for (PhysicalCard card : hand) {
            if (card == null || card.getBlueprint() == null) continue;

            SwccgCardBlueprint bp = card.getBlueprint();
            CardCategory cat = bp.getCardCategory();

            // Skip interrupts - they don't have deploy costs
            if (cat == CardCategory.INTERRUPT) continue;

            try {
                Float deployCost = bp.getDeployCost();
                if (deployCost != null) {
                    int cost = deployCost.intValue();
                    maxDeployableCost = Math.max(maxDeployableCost, cost);

                    if (cost >= EXPENSIVE_CARD_THRESHOLD) {
                        expensiveCardInHand = true;
                    }
                    if (forcePile >= cost) {
                        affordableCardsCount++;
                    }
                }
            } catch (UnsupportedOperationException e) {
                // Card type doesn't support deployCost
            }
        }

        // If we have expensive cards (Executor costs 15+), save force across turns
        if (expensiveCardInHand && maxDeployableCost > forcePile) {
            int forceDeficit = maxDeployableCost - forcePile;
            int turnsToSave = Math.max(1, (forceDeficit + forceGeneration - 1) / Math.max(1, forceGeneration));

            // Only save if it's achievable (within ~3 turns)
            if (turnsToSave <= 3 && remainingLifeForce >= maxDeployableCost) {
                action.addReasoning(
                    "Saving for expensive card (cost " + maxDeployableCost +
                    ", need " + forceDeficit + " more, ~" + turnsToSave + " turns)",
                    BAD_DELTA * 2
                );
            }
        }

        // If we have stuff to deploy but couldn't afford it, save force
        if (affordableCardsCount == 0 && hand.size() > 3 && forcePile < 6) {
            action.addReasoning(
                "No affordable cards (hand " + hand.size() + ", force " + forcePile + ") - save force for next turn",
                BAD_DELTA * 1.5f
            );
        }
    }

    /**
     * Apply force-starved strategy.
     * When activation is low (<8/turn), save force instead of drawing.
     *
     * @return true if we should definitely NOT draw (exit early)
     */
    private boolean applyForceStarvedLogic(EvaluatedAction action, DecisionContext context,
                                           int forcePile, int forceGeneration, int handSize) {
        if (forceGeneration >= FORCE_STARVED_ACTIVATION) {
            return false;  // Not force-starved
        }

        List<PhysicalCard> hand = context.getHand();
        if (hand == null || hand.isEmpty()) {
            return false;
        }

        // Calculate deployable power and minimum cost to reach threshold
        int deployablePower = 0;
        int minCostForThresholdPower = 999;
        List<int[]> powerCostPairs = new ArrayList<>();  // [power, cost]

        for (PhysicalCard card : hand) {
            if (card == null || card.getBlueprint() == null) continue;

            SwccgCardBlueprint bp = card.getBlueprint();

            // Only count characters/starships with power
            if (!bp.hasPowerAttribute()) continue;

            Float power = bp.getPower();
            Float cost = null;
            try {
                cost = bp.getDeployCost();
            } catch (UnsupportedOperationException e) {
                continue;
            }

            if (power != null && cost != null && power > 0 && cost > 0) {
                int p = power.intValue();
                int c = cost.intValue();
                deployablePower += p;
                powerCostPairs.add(new int[] { p, c });
            }
        }

        // Find minimum cost to reach 6 power threshold
        // Sort by efficiency (power/cost ratio descending)
        powerCostPairs.sort((a, b) -> {
            float ratioA = a[1] > 0 ? (float) a[0] / a[1] : 0;
            float ratioB = b[1] > 0 ? (float) b[0] / b[1] : 0;
            return Float.compare(ratioB, ratioA);  // Descending
        });

        int cumulativePower = 0;
        int cumulativeCost = 0;
        for (int[] pair : powerCostPairs) {
            cumulativePower += pair[0];
            cumulativeCost += pair[1];
            if (cumulativePower >= FORCE_STARVED_POWER_THRESHOLD) {
                minCostForThresholdPower = cumulativeCost;
                break;
            }
        }

        // If we have 6+ deployable power, apply force-starved logic
        if (deployablePower >= FORCE_STARVED_POWER_THRESHOLD) {
            // Forward-looking: can we afford to deploy next turn?
            int nextTurnForce = forcePile + forceGeneration;
            int forceNeeded = minCostForThresholdPower + 2;  // +2 buffer for reactions

            logger.info("🎴 FORCE-STARVED check: activation={}, deployable_power={}, min_cost={}, " +
                       "next_turn_force={}, need={}",
                       forceGeneration, deployablePower, minCostForThresholdPower,
                       nextTurnForce, forceNeeded);

            if (nextTurnForce < forceNeeded) {
                // We WON'T have enough force next turn - stop drawing!
                int shortfall = forceNeeded - nextTurnForce;
                action.addReasoning(
                    "FORCE-STARVED: Save force! (" + deployablePower + "p ready, need " +
                    forceNeeded + " force, will have " + nextTurnForce + " → short " + shortfall + ")",
                    VERY_BAD_DELTA * 0.6f
                );
                logger.warn("🎴 FORCE-STARVED: Stopping draw to save force for deployment");

                // If hand already has 6+ cards, make the penalty even stronger
                if (handSize >= 6) {
                    action.addReasoning(
                        "Already have " + handSize + " cards - more won't help without force",
                        BAD_DELTA * 2
                    );
                    return true;  // Exit early - definitely don't draw
                }
            }

            // Even if we CAN afford next turn, don't over-draw when force-starved
            if (handSize >= FORCE_STARVED_MAX_HAND) {
                action.addReasoning(
                    "Force-starved (" + forceGeneration + "/turn): hand " + handSize + " is enough",
                    BAD_DELTA * 3
                );
            }
        }

        return false;
    }
}
