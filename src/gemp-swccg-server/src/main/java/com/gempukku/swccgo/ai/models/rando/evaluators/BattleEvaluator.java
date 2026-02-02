package com.gempukku.swccgo.ai.models.rando.evaluators;

import com.gempukku.swccgo.ai.models.rando.RandoConfig;
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
                50.0f,  // Base score
                actionText
            );

            // === INITIATE BATTLE SCORING ===
            if (actionLower.contains("initiate battle")) {
                // When deciding to initiate battle, we don't know the location yet
                // (battle location is only set after initiation)
                // So we need to be conservative and check overall board position

                SwccgGame game = context.getGame();
                boolean foundFavorableBattle = false;
                boolean foundAnyContestedLocation = false;

                if (game != null && gameState != null) {
                    String playerId = context.getPlayerId();
                    String opponentId = gameState.getOpponent(playerId);

                    if (opponentId != null) {
                        // Check all locations to see if we have any favorable battles
                        try {
                            for (PhysicalCard location : gameState.getTopLocations()) {
                                float ourPower = game.getModifiersQuerying().getTotalPowerAtLocation(
                                    gameState, location, playerId, false, false);
                                float theirPower = game.getModifiersQuerying().getTotalPowerAtLocation(
                                    gameState, location, opponentId, false, false);

                                // Only consider locations where both sides have presence
                                if (ourPower > 0 && theirPower > 0) {
                                    foundAnyContestedLocation = true;
                                    float powerDiff = ourPower - theirPower;

                                    // Also check ability differential (affects destiny draws)
                                    float ourAbility = game.getModifiersQuerying().getTotalAbilityAtLocation(
                                        gameState, playerId, location);
                                    float theirAbility = game.getModifiersQuerying().getTotalAbilityAtLocation(
                                        gameState, opponentId, location);
                                    float abilityDiff = ourAbility - theirAbility;

                                    logger.info("[BattleEvaluator] Checking {}: power={}/{} (diff={}), ability={}/{} (diff={})",
                                        location.getTitle(), ourPower, theirPower, powerDiff,
                                        ourAbility, theirAbility, abilityDiff);

                                    // Adjust effective power difference based on ability
                                    // Each point of ability disadvantage roughly equals 2-3 power disadvantage
                                    // (enemy draws more destiny which averages ~2-3)
                                    float effectiveDiff = powerDiff + (abilityDiff * 2.5f);

                                    if (effectiveDiff >= MARGINAL_THRESHOLD) {
                                        foundFavorableBattle = true;
                                        action.addReasoning(String.format("Favorable battle at %s (power %.0f vs %.0f, ability %.0f vs %.0f)",
                                            location.getTitle(), ourPower, theirPower, ourAbility, theirAbility), 40.0f);
                                        break; // Found at least one good option
                                    } else if (abilityDiff < -1) {
                                        // They have significantly more ability - they'll draw more destiny
                                        action.addReasoning(String.format("Ability disadvantage at %s (%.0f vs %.0f) - enemy draws more destiny",
                                            location.getTitle(), ourAbility, theirAbility), -25.0f);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            logger.warn("[BattleEvaluator] Could not check locations: {}", e.getMessage());
                        }
                    }
                }

                if (!foundFavorableBattle && foundAnyContestedLocation) {
                    // We have contested locations but no favorable power - discourage initiation
                    action.addReasoning("No favorable battles available - don't initiate", -60.0f);
                } else if (!foundAnyContestedLocation) {
                    // No contested locations - can't really battle
                    action.addReasoning("No contested locations", -20.0f);
                }

                // Check if we have enough reserve for destiny draws
                if (reserveDeck < MIN_RESERVE_FOR_BATTLE) {
                    action.addReasoning(
                        String.format("Low reserve deck (%d) - risky destiny draws", reserveDeck),
                        -50.0f
                    );
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
                if (lifeForce <= RandoConfig.CRITICAL_LIFE_FORCE) {
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
