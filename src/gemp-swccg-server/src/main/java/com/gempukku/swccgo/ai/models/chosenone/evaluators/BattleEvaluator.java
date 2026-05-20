package com.gempukku.swccgo.ai.models.chosenone.evaluators;

import com.gempukku.swccgo.ai.models.chosenone.ChosenOneConfig;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.BattleState;
import com.gempukku.swccgo.game.state.GameState;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Evaluates battle initiation decisions.
 *
 * Decision factors (from Python battle_evaluator.py):
 * - Power differential (my power - their power)
 * - Reserve deck (need cards for destiny draws)
 * - Strategic situation (ahead/behind on board/life force)
 *
 * Threat levels:
 * - CRUSH: Power advantage 8+ -> definitely battle
 * - FAVORABLE: Power advantage 5-7 -> battle recommended
 * - MARGINAL: Power advantage 2-4 -> battle worth considering
 * - RISKY: Power diff 0 to +1 -> cautious
 * - DANGEROUS: Power disadvantage -> avoid/retreat
 *
 * Ported from Python battle_evaluator.py
 */
public class BattleEvaluator extends ActionEvaluator {

    // Battle thresholds (power advantage needed)
    private static final int CRUSH_THRESHOLD = 8;      // Overwhelming advantage
    private static final int FAVORABLE_THRESHOLD = 5;  // Strong advantage
    private static final int MARGINAL_THRESHOLD = 2;   // Worth initiating
    private static final int RISKY_THRESHOLD = 0;      // Even or slight advantage

    // Minimum reserve deck for destiny draws
    private static final int MIN_RESERVE_FOR_BATTLE = 3;

    public BattleEvaluator() {
        super("Battle");
    }

    @Override
    public boolean canEvaluate(DecisionContext context) {
        // Handle CARD_ACTION_CHOICE with battle-related actions
        if (!"CARD_ACTION_CHOICE".equals(context.getDecisionType())) {
            return false;
        }

        // Check for battle phase or battle-related decision
        Phase phase = context.getPhase();
        String decisionText = context.getDecisionText();
        String decisionLower = decisionText != null ? decisionText.toLowerCase(Locale.ROOT) : "";

        // During battle phase
        if (phase == Phase.BATTLE) {
            return true;
        }

        // Or if decision text mentions battle/initiate
        if (decisionLower.contains("battle") || decisionLower.contains("initiate")) {
            return true;
        }

        // Check if any action mentions battle
        List<String> actionTexts = context.getActionTexts();
        if (actionTexts != null) {
            for (String actionText : actionTexts) {
                if (actionText != null) {
                    String actionLower = actionText.toLowerCase(Locale.ROOT);
                    if (actionLower.contains("initiate battle") || actionLower.contains("battle")) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public List<EvaluatedAction> evaluate(DecisionContext context) {
        List<EvaluatedAction> actions = new ArrayList<>();
        GameState gameState = context.getGameState();

        logger.info("[BattleEvaluator] Evaluating battle decision");

        List<String> actionIds = context.getActionIds();
        List<String> actionTexts = context.getActionTexts();

        if (actionIds == null || actionTexts == null) {
            logger.warn("[BattleEvaluator] No action IDs or texts available");
            return actions;
        }

        logger.debug("[BattleEvaluator] Phase={}, actions={}", context.getPhase(), actionIds.size());

        // Get game state info
        int reserveDeck = context.getReserveDeckSize();
        int lifeForce = context.getLifeForce();
        int forcePile = context.getForcePileSize();

        // Calculate board position
        boolean isBehindOnLifeForce = false;
        boolean isAheadOnLifeForce = false;
        if (gameState != null) {
            String playerId = context.getPlayerId();
            String opponentId = gameState.getOpponent(playerId);
            if (opponentId != null) {
                int opponentLifeForce = gameState.getPlayerLifeForce(opponentId);
                isBehindOnLifeForce = lifeForce < opponentLifeForce - 5;
                isAheadOnLifeForce = lifeForce > opponentLifeForce + 5;
            }
        }

        for (int i = 0; i < actionIds.size(); i++) {
            String actionId = actionIds.get(i);
            String actionText = i < actionTexts.size() ? actionTexts.get(i) : "";
            String actionLower = actionText.toLowerCase(Locale.ROOT);

            // Only handle battle-related actions
            if (!actionLower.contains("battle") && !actionLower.contains("fire")) {
                continue;
            }

            EvaluatedAction action = new EvaluatedAction(
                actionId,
                ActionType.BATTLE,
                100.0f,  // V34: Raised base score from 50 — bot needs to actually fight
                actionText
            );

            // === INITIATE BATTLE SCORING ===
            if (actionLower.contains("initiate battle")) {
                // V22.4: LOCATION-SPECIFIC battle evaluation
                // OLD BUG: Checked ALL locations — if ANY was favorable, approved initiation.
                // But the action is for a SPECIFIC location! Rando initiated battle at Dining Room
                // (3 vs 26 power) because another location was favorable.
                // NEW: Try to extract the specific location from the action text first.

                SwccgGame game = context.getGame();
                boolean foundFavorableBattle = false;
                boolean foundAnyContestedLocation = false;
                boolean checkedSpecificLocation = false;

                if (game != null && gameState != null) {
                    String playerId = context.getPlayerId();
                    String opponentId = gameState.getOpponent(playerId);

                    if (opponentId != null) {
                        try {
                            // V22.4: First try to find the SPECIFIC location this action targets
                            PhysicalCard targetLocation = null;
                            for (PhysicalCard location : gameState.getTopLocations()) {
                                String locTitle = location.getTitle();
                                if (locTitle != null && actionLower.contains(locTitle.toLowerCase(Locale.ROOT))) {
                                    targetLocation = location;
                                    break;
                                }
                            }

                            if (targetLocation != null) {
                                // V22.4: Evaluate THIS SPECIFIC location only
                                checkedSpecificLocation = true;
                                float ourPower = game.getModifiersQuerying().getTotalPowerAtLocation(
                                    gameState, targetLocation, playerId, false, false);
                                float theirPower = game.getModifiersQuerying().getTotalPowerAtLocation(
                                    gameState, targetLocation, opponentId, false, false);
                                float ourAbility = game.getModifiersQuerying().getTotalAbilityAtLocation(
                                    gameState, playerId, targetLocation);
                                float theirAbility = game.getModifiersQuerying().getTotalAbilityAtLocation(
                                    gameState, opponentId, targetLocation);
                                float powerDiff = ourPower - theirPower;
                                float abilityDiff = ourAbility - theirAbility;
                                float effectiveDiff = powerDiff + (abilityDiff * 2.5f);

                                logger.info("V22.4 [BattleEvaluator] SPECIFIC location {}: power={}/{}, ability={}/{}, effectiveDiff={}",
                                    targetLocation.getTitle(), ourPower, theirPower, ourAbility, theirAbility, effectiveDiff);

                                if (ourPower > 0 && theirPower > 0) {
                                    foundAnyContestedLocation = true;

                                    // === V76 (Steve, 2026-05-15): BATTLE PREDICTION GATE ===
                                    // Mirror of Rando V76. See Rando BattleEvaluator V76 comment.
                                    try {
                                        int myDraws = 1, oppDraws = 1;
                                        try {
                                            int myCh = 0, oppCh = 0;
                                            java.util.List<PhysicalCard> coCards = gameState.getCardsAtLocation(targetLocation);
                                            if (coCards != null) {
                                                for (PhysicalCard c : coCards) {
                                                    if (c == null || c.getBlueprint() == null) continue;
                                                    if (c.getBlueprint().getCardCategory() != com.gempukku.swccgo.common.CardCategory.CHARACTER) continue;
                                                    if (playerId.equals(c.getOwner())) myCh++;
                                                    else if (opponentId.equals(c.getOwner())) oppCh++;
                                                }
                                            }
                                            myDraws = Math.max(1, Math.min(4, myCh));
                                            oppDraws = Math.max(1, Math.min(4, oppCh));
                                        } catch (Exception e) { /* defaults */ }
                                        com.gempukku.swccgo.ai.models.chosenone.strategy.DeckOracle v76DeckOracle =
                                            context.getDeckOracle();
                                        com.gempukku.swccgo.ai.models.chosenone.strategy.OpponentDeckTracker v76OppTracker =
                                            context.getOpponentDeckTracker();
                                        BattlePredictor.BattleOutcome v76Outcome = BattlePredictor.predictBattle(
                                            (int) ourPower, myDraws,
                                            (int) theirPower, oppDraws,
                                            v76DeckOracle, v76OppTracker);
                                        logger.warn("V76 BATTLE PREDICT at {}: winRate={} avgDamageTaken={}",
                                            targetLocation.getTitle(),
                                            String.format("%.2f", v76Outcome.winProbability),
                                            String.format("%.1f", v76Outcome.expectedDamageTaken));
                                        if (v76Outcome.winProbability < 0.35f) {
                                            action.addReasoning(String.format(
                                                "V76 BATTLE PREDICT: winRate %.0f%% at %s — probable defeat, don't initiate!",
                                                v76Outcome.winProbability * 100, targetLocation.getTitle()), -800.0f);
                                            logger.warn("V76 BATTLE BLOCK at {}: winRate too low",
                                                targetLocation.getTitle());
                                        } else if (v76Outcome.expectedDamageTaken >= 10f) {
                                            action.addReasoning(String.format(
                                                "V76 BATTLE PREDICT: avg damage taken %.1f at %s — pyrrhic, don't initiate!",
                                                v76Outcome.expectedDamageTaken, targetLocation.getTitle()), -500.0f);
                                            logger.warn("V76 BATTLE COSTLY at {}: damage too high",
                                                targetLocation.getTitle());
                                        }
                                    } catch (Exception v76Ex) {
                                        logger.debug("V76 prediction error: {}", v76Ex.getMessage());
                                    }

                                    // V22.4: HARD BLOCK on suicidal battles
                                    // If opponent power is more than 2x ours, this is suicide
                                    if (theirPower > ourPower * 2 && theirPower > 6) {
                                        action.addReasoning(String.format("V22.4 SUICIDE BLOCK: %.0f vs %.0f power - NEVER initiate!",
                                            ourPower, theirPower), -500.0f);
                                        logger.warn("V22.4 SUICIDE BLOCK at {}: our {} vs their {} - BLOCKED",
                                            targetLocation.getTitle(), (int)ourPower, (int)theirPower);
                                    } else if (effectiveDiff >= MARGINAL_THRESHOLD) {
                                        foundFavorableBattle = true;
                                        action.addReasoning(String.format("V34 Favorable battle at %s (power %.0f vs %.0f, ability %.0f vs %.0f)",
                                            targetLocation.getTitle(), ourPower, theirPower, ourAbility, theirAbility), 150.0f); // V34: Raised from 40
                                    } else if (effectiveDiff >= -MARGINAL_THRESHOLD) {
                                        // V34: Close to even — still worth trying if we have presence
                                        action.addReasoning(String.format("V34 Even battle at %s (power %.0f vs %.0f) - marginal",
                                            targetLocation.getTitle(), ourPower, theirPower), 30.0f); // V34: Changed from -30 to +30
                                    } else {
                                        // Unfavorable — strong discourage
                                        float penalty = -60.0f;
                                        if (effectiveDiff < -8) penalty = -120.0f;
                                        if (effectiveDiff < -15) penalty = -250.0f;
                                        action.addReasoning(String.format("V22.4: UNFAVORABLE at %s (power %.0f vs %.0f) - don't initiate!",
                                            targetLocation.getTitle(), ourPower, theirPower), penalty);
                                    }
                                } else if (ourPower > 0 && theirPower == 0) {
                                    // We're alone here - no battle possible
                                    action.addReasoning("No opponent here", -20.0f);
                                }

                                // V35: INQUISITOR BATTLE DESTINY BONUS
                                // Scan for our Inquisitors and opponent Jedi/hatred at this location
                                // Inquisitors get extra battle destiny draws from Hunt Down V objective
                                {
                                    List<PhysicalCard> cardsHere = gameState.getCardsAtLocation(targetLocation);
                                    boolean inquisitorInBattle = false;
                                    boolean hatredAtLocation = false;
                                    boolean jediAtLocation = false;

                                    for (PhysicalCard bCard : cardsHere) {
                                        if (bCard == null || bCard.getBlueprint() == null) continue;
                                        String bTitle = bCard.getTitle() != null ? bCard.getTitle().toLowerCase(Locale.ROOT) : "";

                                        if (playerId.equals(bCard.getOwner())) {
                                            // Our characters — check for Inquisitors
                                            if (isInquisitor(bTitle)) {
                                                inquisitorInBattle = true;
                                            }
                                        } else {
                                            // Opponent characters — check for Jedi/Padawan and stacked hatred
                                            if (isJediOrPadawan(bTitle)) {
                                                jediAtLocation = true;
                                            }
                                            try {
                                                java.util.List<PhysicalCard> stacked = gameState.getStackedCards(bCard);
                                                if (stacked != null && !stacked.isEmpty()) {
                                                    hatredAtLocation = true;
                                                }
                                            } catch (Exception e) { /* ignore */ }
                                        }
                                    }

                                    if (inquisitorInBattle) {
                                        float destinyBonus = 120.0f; // +1 battle destiny from objective
                                        if (hatredAtLocation) destinyBonus = 250.0f; // +2 battle destiny
                                        if (jediAtLocation) destinyBonus += 100.0f; // FMFTD and Fifth Brother bonuses
                                        action.addReasoning(String.format(
                                            "V35 HUNT DESTINY: Inquisitor in battle%s%s — +%d total battle destiny!",
                                            hatredAtLocation ? " + HATRED" : "",
                                            jediAtLocation ? " vs JEDI" : "",
                                            hatredAtLocation ? 2 : 1), destinyBonus);
                                        logger.warn("V35 HUNT DESTINY at {}: Inquisitor={}, hatred={}, jedi={} — bonus +{}",
                                            targetLocation.getTitle(), inquisitorInBattle, hatredAtLocation,
                                            jediAtLocation, (int)destinyBonus);
                                    }
                                }
                            }

                            // V22.4: Fallback — if we couldn't identify the specific location,
                            // check all locations but be MORE conservative
                            if (!checkedSpecificLocation) {
                                for (PhysicalCard location : gameState.getTopLocations()) {
                                    float ourPower = game.getModifiersQuerying().getTotalPowerAtLocation(
                                        gameState, location, playerId, false, false);
                                    float theirPower = game.getModifiersQuerying().getTotalPowerAtLocation(
                                        gameState, location, opponentId, false, false);

                                    if (ourPower > 0 && theirPower > 0) {
                                        foundAnyContestedLocation = true;
                                        float powerDiff = ourPower - theirPower;
                                        float ourAbility = game.getModifiersQuerying().getTotalAbilityAtLocation(
                                            gameState, playerId, location);
                                        float theirAbility = game.getModifiersQuerying().getTotalAbilityAtLocation(
                                            gameState, opponentId, location);
                                        float abilityDiff = ourAbility - theirAbility;
                                        float effectiveDiff = powerDiff + (abilityDiff * 2.5f);

                                        logger.info("[BattleEvaluator] Checking {}: power={}/{} (diff={}), ability={}/{} (diff={})",
                                            location.getTitle(), ourPower, theirPower, powerDiff,
                                            ourAbility, theirAbility, abilityDiff);

                                        // V22.4: Check for any suicidal locations — if ANY location
                                        // has our power < 50% of theirs, add strong warning
                                        if (theirPower > ourPower * 2 && theirPower > 6) {
                                            action.addReasoning(String.format("V22.4 DANGER at %s (%.0f vs %.0f) - might battle here!",
                                                location.getTitle(), ourPower, theirPower), -80.0f);
                                        }

                                        if (effectiveDiff >= MARGINAL_THRESHOLD) {
                                            foundFavorableBattle = true;
                                            action.addReasoning(String.format("Favorable battle at %s (power %.0f vs %.0f, ability %.0f vs %.0f)",
                                                location.getTitle(), ourPower, theirPower, ourAbility, theirAbility), 40.0f);
                                            break;
                                        } else if (abilityDiff < -1) {
                                            action.addReasoning(String.format("Ability disadvantage at %s (%.0f vs %.0f) - enemy draws more destiny",
                                                location.getTitle(), ourAbility, theirAbility), -25.0f);
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            logger.warn("[BattleEvaluator] Could not check locations: {}", e.getMessage());
                        }
                    }
                }

                if (!foundFavorableBattle && foundAnyContestedLocation) {
                    action.addReasoning("No favorable battles available - don't initiate", -60.0f);
                } else if (!foundAnyContestedLocation) {
                    action.addReasoning("No contested locations", -20.0f);
                }

                // V61 RESERVE DECK GUARD — battle destiny needs Reserve Deck cards!
                // Scale penalty: 0 cards = hard block (auto-lose), 1-2 = heavy penalty.
                if (reserveDeck == 0) {
                    action.addReasoning(
                        "V61 RESERVE EMPTY: 0 cards in Reserve — CANNOT draw battle destiny, auto-lose!",
                        -800.0f);
                    logger.warn("V61 RESERVE EMPTY: Blocking battle initiation — Reserve=0!");
                } else if (reserveDeck == 1) {
                    action.addReasoning(
                        "V61 RESERVE CRITICAL: 1 card in Reserve — can draw 1 destiny max, very risky!",
                        -400.0f);
                    logger.warn("V61 RESERVE CRITICAL: 1 card in reserve — heavy penalty -400");
                } else if (reserveDeck == 2) {
                    action.addReasoning(
                        "V61 RESERVE LOW: 2 cards in Reserve — weapon destiny + 1 battle destiny only",
                        -200.0f);
                } else if (reserveDeck < MIN_RESERVE_FOR_BATTLE) {
                    action.addReasoning(
                        String.format("Low reserve deck (%d) - risky destiny draws", reserveDeck),
                        -80.0f);
                }

                // Strategic position adjustments (reduced impact - power diff is more important)
                if (isBehindOnLifeForce) {
                    // When behind, slight encouragement but power still matters most
                    action.addReasoning("Behind on life force - slightly more aggressive", 15.0f);
                } else if (isAheadOnLifeForce) {
                    // When ahead, be more conservative
                    action.addReasoning("Ahead on life force - can afford to wait", -20.0f);
                }

                // Life force critical - more aggressive but still check power
                if (lifeForce <= ChosenOneConfig.CRITICAL_LIFE_FORCE) {
                    action.addReasoning("Low life force - need to act", 30.0f);
                }
            }

            // === WEAPON FIRING ===
            if (actionLower.contains("fire")) {
                action.addReasoning("Fire weapon", 40.0f);

                // Target selection bonuses
                if (actionLower.contains("character")) {
                    action.addReasoning("Target character", 10.0f);
                }
                if (actionLower.contains("unique") || actionLower.contains("•")) {
                    action.addReasoning("Target unique card", 20.0f);
                }
            }

            // === CANCEL BATTLE (It's A Trap, etc.) ===
            if (actionLower.contains("cancel battle") || actionLower.contains("cancel the battle")) {
                SwccgGame game = context.getGame();
                if (game != null && gameState != null) {
                    BattleState battleState = gameState.getBattleState();
                    String playerId = context.getPlayerId();

                    if (battleState != null) {
                        String initiator = battleState.getPlayerInitiatedBattle();
                        boolean weInitiated = playerId != null && playerId.equals(initiator);

                        if (weInitiated) {
                            // NEVER cancel a battle we started - that's wasteful
                            action.addReasoning("DO NOT cancel our own battle! Waste of interrupt.", -150.0f);
                            logger.info("[BattleEvaluator] Penalizing cancel - WE initiated this battle");
                        } else {
                            // Opponent initiated - check if we should cancel
                            String opponentId = gameState.getOpponent(playerId);
                            PhysicalCard battleLocation = battleState.getBattleLocation();

                            if (battleLocation != null && opponentId != null) {
                                try {
                                    float ourPower = game.getModifiersQuerying().getTotalPowerAtLocation(
                                        gameState, battleLocation, playerId, false, false);
                                    float theirPower = game.getModifiersQuerying().getTotalPowerAtLocation(
                                        gameState, battleLocation, opponentId, false, false);
                                    float powerDiff = ourPower - theirPower;

                                    if (powerDiff < -FAVORABLE_THRESHOLD) {
                                        // We're badly losing - cancel is valuable
                                        action.addReasoning(String.format("Cancel losing battle (%.0f vs %.0f)", ourPower, theirPower), 60.0f);
                                    } else if (powerDiff < 0) {
                                        // Slight disadvantage - cancel might be worth it
                                        action.addReasoning(String.format("Cancel unfavorable battle (%.0f vs %.0f)", ourPower, theirPower), 20.0f);
                                    } else {
                                        // We're winning or even - don't waste the interrupt
                                        action.addReasoning(String.format("Don't cancel - we're not losing (%.0f vs %.0f)", ourPower, theirPower), -60.0f);
                                    }
                                } catch (Exception e) {
                                    logger.warn("[BattleEvaluator] Could not get power for cancel decision: {}", e.getMessage());
                                }
                            }
                        }
                    }
                }
            }

            // === BATTLE TACTICS (during battle) ===
            if (context.getPhase() == Phase.BATTLE) {
                // Fire before forfeit
                if (actionLower.contains("fire")) {
                    action.addReasoning("Fire weapons during battle", 50.0f);
                }

                // Draw battle destiny
                if (actionLower.contains("draw") && actionLower.contains("destiny")) {
                    action.addReasoning("Draw battle destiny", 30.0f);
                }
            }

            logger.debug("[BattleEvaluator] Scored '{}' -> {}",
                actionText.length() > 40 ? actionText.substring(0, 40) + "..." : actionText,
                String.format("%.1f", action.getScore()));

            actions.add(action);
        }

        logger.info("[BattleEvaluator] Evaluated {} battle actions", actions.size());
        return actions;
    }
}
