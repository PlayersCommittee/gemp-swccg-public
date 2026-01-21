package com.gempukku.swccgo.ai.models.rando.evaluators;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;

import java.util.ArrayList;
import java.util.List;

/**
 * Evaluates force activation decisions (INTEGER type).
 *
 * Determines the optimal amount of force to activate based on:
 * - Current force pile
 * - Reserve deck size
 * - Turn number and strategy
 * - Future turn planning (save for expensive cards)
 * - Late-game life force preservation
 *
 * Ported from Python ForceActivationEvaluator
 */
public class ForceActivationEvaluator extends ActionEvaluator {

    // Config constants
    private static final int MAX_FORCE_PILE = 25;
    private static final int RESERVE_FOR_DESTINY_CONTESTED = 4;
    private static final int RESERVE_FOR_DESTINY_SAFE = 1;
    private static final int LATE_GAME_LIFE_FORCE = 12;
    private static final int CRITICAL_LIFE_FORCE = 6;

    public ForceActivationEvaluator() {
        super("ForceActivation");
    }

    @Override
    public boolean canEvaluate(DecisionContext context) {
        // Handle all INTEGER decisions - force activation is the most common
        return "INTEGER".equals(context.getDecisionType());
    }

    @Override
    public List<EvaluatedAction> evaluate(DecisionContext context) {
        List<EvaluatedAction> actions = new ArrayList<>();
        GameState gameState = context.getGameState();
        String textLower = context.getDecisionText().toLowerCase();

        // Parse min/max from context
        int minVal = context.getMin();
        int maxVal = context.getMax();

        // Ensure we have valid bounds
        if (maxVal == 0) {
            maxVal = 1;
            logger.warn("No max value found, using fallback: {}", maxVal);
        }

        // Special case: "allow opponent to activate" - just let them activate max
        if (textLower.contains("allow opponent to activate") || textLower.contains("opponent to activate")) {
            EvaluatedAction action = new EvaluatedAction(
                String.valueOf(maxVal),
                ActionType.ACTIVATE_FORCE,
                50.0f,
                String.format("Allow opponent to activate %d force", maxVal)
            );
            action.addReasoning("Allowing opponent max activation (they'll waste force)");
            actions.add(action);
            return actions;
        }

        if (gameState == null) {
            // No game state - use max value
            EvaluatedAction action = new EvaluatedAction(
                String.valueOf(maxVal),
                ActionType.ACTIVATE_FORCE,
                50.0f,
                String.format("INTEGER response: %d (no game state)", maxVal)
            );
            action.addReasoning("No game state available, defaulting to max");
            actions.add(action);
            return actions;
        }

        int amount;

        // Standard force activation logic
        if (textLower.contains("force to activate") || textLower.contains("activate force")) {
            // EARLY GAME AGGRESSION: Turns 1-3, activate maximum to build resources
            if (context.getTurnNumber() <= 3) {
                amount = maxVal;
                logger.info("Early game (turn {}) - activating max force: {}", context.getTurnNumber(), amount);
            } else {
                // Calculate optimal amount using game state logic
                amount = calculateActivationAmount(context, maxVal);
            }
        } else {
            // Unknown INTEGER decision - use max value
            amount = maxVal;
            logger.info("Unknown INTEGER decision, using max: {}", amount);
        }

        // Ensure amount is within bounds
        amount = Math.max(minVal, Math.min(amount, maxVal));

        // Build action with reasoning
        EvaluatedAction action = new EvaluatedAction(
            String.valueOf(amount),
            ActionType.ACTIVATE_FORCE,
            50.0f,
            String.format("Activate %d of %d force", amount, maxVal)
        );

        // Add reasoning based on decision factors
        int forcePile = context.getForcePileSize();
        if (forcePile > 12) {
            action.addReasoning(String.format("Force pile high (%d) - conserving", forcePile));
        }

        int reserveTotal = context.getLifeForce();
        if (reserveTotal <= 20) {
            action.addReasoning(String.format("Reserve low (%d) - saving for destiny", reserveTotal));
        }

        if (amount == maxVal) {
            action.addReasoning("Activating full amount available", 10.0f);
        } else if (amount == 0) {
            action.addReasoning("Skipping activation this turn", -10.0f);
        } else {
            action.addReasoning(String.format("Activating partial (%d/%d)", amount, maxVal));
        }

        actions.add(action);
        return actions;
    }

    /**
     * Calculate optimal force activation amount.
     *
     * Rules (in priority order):
     * 1. Reserve cards for destiny draws (more if contested locations)
     * 2. Cap force pile at MAX_FORCE_PILE
     * 3. Late-game preservation when life force is critical
     */
    private int calculateActivationAmount(DecisionContext context, int maxAvailable) {
        int amount = maxAvailable;
        int currentForce = context.getForcePileSize();
        int reserveDeck = context.getReserveDeckSize();
        int lifeForce = context.getLifeForce();
        int handSize = context.getHandSize();

        // === RULE 1: RESERVE CARDS FOR DESTINY DRAWS ===
        // Conservative approach: always reserve as if we might have contested locations
        // This ensures we have cards for destiny draws when battles occur
        int reserveNeeded = RESERVE_FOR_DESTINY_CONTESTED;
        logger.debug("Reserving {} cards for potential destiny draws", reserveNeeded);

        // Calculate max we can activate while keeping reserve
        int maxFromReserve = Math.max(0, reserveDeck - reserveNeeded);
        if (maxFromReserve < amount) {
            logger.info("Reserving {} cards for destiny. Reserve deck: {}, limiting activation from {} to {}",
                       reserveNeeded, reserveDeck, amount, maxFromReserve);
            amount = maxFromReserve;
        }

        // === RULE 2: CAP FORCE PILE AT MAX ===
        int forceRoom = MAX_FORCE_PILE - currentForce;
        if (forceRoom < amount) {
            logger.info("Capping force pile at {}. Current: {}, limiting activation from {} to {}",
                       MAX_FORCE_PILE, currentForce, amount, Math.max(0, forceRoom));
            amount = Math.max(0, forceRoom);
        }

        // === RULE 3: LATE GAME PRESERVATION ===
        if (lifeForce < CRITICAL_LIFE_FORCE) {
            // Only activate enough to do ONE action, preserve rest for destiny
            int emergencyAmount = Math.min(amount, Math.max(1, 6 - currentForce));
            if (emergencyAmount < amount) {
                logger.info("CRITICAL life force ({}), limiting activation to {}", lifeForce, emergencyAmount);
                amount = emergencyAmount;
            }
            return amount;
        }

        // === CONSIDER HAND CONTENTS ===
        // Check if we have expensive cards that need saving for
        int maxDeployCost = 0;
        for (PhysicalCard card : context.getHand()) {
            // Not all cards have deployCost (e.g., Interrupts)
            try {
                Float deployCost = card.getBlueprint().getDeployCost();
                if (deployCost != null && deployCost > maxDeployCost) {
                    maxDeployCost = deployCost.intValue();
                }
            } catch (UnsupportedOperationException e) {
                // Card type doesn't support deployCost - skip
            }
        }

        // If we have expensive cards and need more force, note it
        if (maxDeployCost > currentForce && maxDeployCost <= lifeForce) {
            int forceNeeded = maxDeployCost - currentForce;
            if (forceNeeded > amount) {
                logger.debug("Expensive card (cost {}), need {} but limited to {}", maxDeployCost, forceNeeded, amount);
            }
        }

        // If we already have plenty of force, only activate a little more
        if (currentForce > 12) {
            int conservativeAmount = Math.max(0, 2);
            if (conservativeAmount < amount) {
                logger.debug("Force > 12 ({}), limiting to {} more", currentForce, conservativeAmount);
                amount = conservativeAmount;
            }
        }

        // === HAND SIZE CONSIDERATION ===
        // If hand is small and we have plenty of force, don't over-activate
        if (handSize <= 4 && currentForce >= 8) {
            if (amount > 2) {
                logger.debug("Small hand ({}), enough force ({}), limiting to 2", handSize, currentForce);
                amount = 2;
            }
        }

        return amount;
    }
}
