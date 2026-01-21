package com.gempukku.swccgo.ai.models.rando.evaluators;

import com.gempukku.swccgo.ai.models.rando.RandoConfig;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Evaluates movement decisions.
 *
 * FULLY PORTED from Python move_evaluator.py with:
 * - Threat level calculation (CRUSH, FAVORABLE, RISKY, DANGEROUS, RETREAT)
 * - Flee analysis with destination checking
 * - Offensive attack opportunity detection
 * - Spread viability analysis with icon bonuses
 *
 * Decision factors:
 * - Power differential at current location (fleeing from danger)
 * - Power differential at destination (moving to advantageous positions)
 * - Spreading out vs consolidating forces
 * - Strategic retreat from dangerous locations
 * - Offensive attacks from uncontested strongholds
 */
public class MoveEvaluator extends ActionEvaluator {

    // Move keywords to identify move actions
    private static final String[] MOVE_KEYWORDS = {
        "Move using", "Shuttle", "Docking bay transit", "Transport",
        "Take off", "Land", "Move to", "Move from"
    };

    // Thresholds (from Python config)
    private static final int POWER_DIFF_FOR_FLEE = 2;
    private static final int OVERKILL_THRESHOLD = 4;
    private static final int ESTABLISH_THRESHOLD = 6;
    private static final int CONTEST_MARGIN = 4;
    private static final int ATTACK_POWER_ADVANTAGE = 4;
    private static final int ATTACK_MIN_POWER = 6;
    private static final float ICON_BONUS = 15.0f;

    // Score deltas (from Python)
    private static final float VERY_GOOD_DELTA = 150.0f;
    private static final float GOOD_DELTA = 10.0f;
    private static final float BAD_DELTA = -10.0f;
    private static final float VERY_BAD_DELTA = -150.0f;

    // Threat levels (matching Python ThreatLevel enum)
    private enum ThreatLevel {
        CRUSH, FAVORABLE, RISKY, DANGEROUS, RETREAT
    }

    // Track cards we've already tried moving this turn
    private Set<String> pendingMoveCardIds = new HashSet<>();
    private int lastTurnNumber = -1;

    public MoveEvaluator() {
        super("Move");
    }

    public void resetPendingMoves() {
        pendingMoveCardIds.clear();
    }

    @Override
    public boolean canEvaluate(DecisionContext context) {
        String decisionType = context.getDecisionType();
        if (!"CARD_ACTION_CHOICE".equals(decisionType) && !"ACTION_CHOICE".equals(decisionType)) {
            return false;
        }

        // Must be our turn
        if (context.getGameState() != null && !context.isMyTurn()) {
            return false;
        }

        // Check if any action is a move action
        List<String> actionTexts = context.getActionTexts();
        if (actionTexts != null) {
            for (String actionText : actionTexts) {
                if (isMoveAction(actionText)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isMoveAction(String actionText) {
        if (actionText == null) return false;
        for (String keyword : MOVE_KEYWORDS) {
            if (actionText.contains(keyword)) {
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
        String playerId = context.getPlayerId();
        Side mySide = context.getSide();

        logger.info("[MoveEvaluator] Evaluating move decision");

        // Reset pending move tracking at the start of each turn
        if (context.getTurnNumber() != lastTurnNumber) {
            resetPendingMoves();
            lastTurnNumber = context.getTurnNumber();
        }

        List<String> actionIds = context.getActionIds();
        List<String> actionTexts = context.getActionTexts();
        List<String> cardIds = context.getCardIds();

        if (actionIds == null || actionTexts == null) {
            return actions;
        }

        logger.debug("[MoveEvaluator] Phase={}, actions={}", context.getPhase(), actionIds.size());

        for (int i = 0; i < actionIds.size(); i++) {
            String actionId = actionIds.get(i);
            String actionText = i < actionTexts.size() ? actionTexts.get(i) : "";
            String actionLower = actionText.toLowerCase(Locale.ROOT);
            String cardIdStr = (cardIds != null && i < cardIds.size()) ? cardIds.get(i) : null;

            // Only handle move-related actions
            if (!isMoveAction(actionText)) {
                continue;
            }

            // === SPECIAL CASES: Passenger/Pilot capacity slots ===
            if (actionLower.contains("passenger capacity slot")) {
                logger.info("[MoveEvaluator] SKIP passenger slot move - NEVER good");
                continue;  // Let ActionTextEvaluator's -100 apply
            }

            if (actionLower.contains("pilot capacity slot")) {
                EvaluatedAction pilotAction = new EvaluatedAction(
                    actionId, ActionType.MOVE, 100.0f, actionText
                );
                pilotAction.addReasoning("Move to pilot slot - adds power!", 50.0f);
                actions.add(pilotAction);
                logger.info("[MoveEvaluator] Strongly prefer pilot capacity slot move");
                continue;
            }

            EvaluatedAction action = new EvaluatedAction(
                actionId,
                ActionType.MOVE,
                0.0f,  // Start at 0 - let analysis determine score
                actionText
            );

            // === Get the card being moved ===
            PhysicalCard cardToMove = null;
            if (cardIdStr != null && game != null) {
                try {
                    int cardId = Integer.parseInt(cardIdStr);
                    cardToMove = gameState.findCardById(cardId);
                } catch (Exception e) {
                    logger.debug("[MoveEvaluator] Could not find card: {}", e.getMessage());
                }
            }

            // === STRATEGIC ANALYSIS ===
            if (cardToMove != null && gameState != null && game != null) {
                PhysicalCard currentLocation = cardToMove.getAtLocation();

                if (currentLocation != null) {
                    // Analyze if we should move FROM this location
                    rankMoveFromLocation(action, gameState, game, playerId, mySide,
                                        cardToMove, currentLocation);
                } else {
                    action.addReasoning("Card not at a location", BAD_DELTA);
                }
            }

            // === MOVEMENT TYPE BONUSES ===
            if (actionLower.contains("shuttle") || actionLower.contains("transport")) {
                action.addReasoning("Shuttle/Transport movement", 20.0f);
            }
            if (actionLower.contains("docking bay")) {
                action.addReasoning("Docking bay transit", 15.0f);
            }
            if (actionLower.contains("take off")) {
                action.addReasoning("Take off (space deployment)", 10.0f);
            }

            // Land - penalize starfighters
            if (actionLower.contains("land")) {
                handleLandAction(action, actionLower, cardToMove);
            }

            // Move phase bonus
            if (context.getPhase() == Phase.MOVE) {
                action.addReasoning("Move phase - consider repositioning", 5.0f);
            }

            logger.debug("[MoveEvaluator] Scored '{}' -> {:.1f}",
                actionText.length() > 40 ? actionText.substring(0, 40) + "..." : actionText,
                action.getScore());

            actions.add(action);
        }

        logger.info("[MoveEvaluator] Evaluated {} move actions", actions.size());
        return actions;
    }

    /**
     * Rank moving FROM a specific location.
     * Ported from Python move_evaluator.py _rank_move_from_location
     */
    private void rankMoveFromLocation(EvaluatedAction action, GameState gameState,
                                       SwccgGame game, String playerId, Side mySide,
                                       PhysicalCard cardToMove, PhysicalCard location) {
        String opponentId = gameState.getOpponent(playerId);

        // Calculate power at current location
        float myPower = 0;
        float theirPower = 0;
        int myCardCount = 0;
        int theirCardCount = 0;

        List<PhysicalCard> cardsAtLocation = gameState.getCardsAtLocation(location);
        for (PhysicalCard card : cardsAtLocation) {
            if (card == null) continue;
            String owner = card.getOwner();
            SwccgCardBlueprint bp = card.getBlueprint();
            if (bp == null || !bp.hasPowerAttribute()) continue;

            Float power = bp.getPower();
            if (power == null) power = 0f;

            if (playerId.equals(owner)) {
                myPower += power;
                myCardCount++;
            } else if (opponentId != null && opponentId.equals(owner)) {
                theirPower += power;
                theirCardCount++;
            }
        }

        float powerDiff = myPower - theirPower;
        boolean theirHasCards = theirCardCount > 0;

        logger.debug("[MoveEvaluator] At {}: myPower={}, theirPower={}, diff={}",
            location.getTitle(), myPower, theirPower, powerDiff);

        // === THREAT LEVEL ANALYSIS ===
        if (theirPower > 0) {
            ThreatLevel threat = calculateThreatLevel(powerDiff);

            switch (threat) {
                case RETREAT:
                    action.addReasoning("Strategic retreat - badly outmatched (" + (int)powerDiff + ")",
                                       VERY_GOOD_DELTA);
                    logger.info("[MoveEvaluator] RETREAT recommended - outmatched by {}", -powerDiff);
                    return;

                case DANGEROUS:
                    action.addReasoning("Dangerous location - retreat recommended (" + (int)powerDiff + ")",
                                       GOOD_DELTA * 2);
                    return;

                case CRUSH:
                case FAVORABLE:
                    action.addReasoning("Power advantage (" + (int)powerDiff + ") - stay and fight!",
                                       BAD_DELTA * 2);
                    return;

                case RISKY:
                    // Contested - could go either way
                    action.addReasoning("Contested location - risky to leave", BAD_DELTA);
                    break;
            }
        }

        // === FLEE LOGIC ===
        if (theirPower - myPower > POWER_DIFF_FOR_FLEE && theirPower > 0) {
            float disadvantage = theirPower - myPower;
            action.addReasoning("Outmatched by " + (int)disadvantage + " - should flee",
                               GOOD_DELTA * Math.min(disadvantage / 2, 5));
            return;
        }

        // === OFFENSIVE ATTACK OPPORTUNITY ===
        // If we're at an uncontested location with significant power, look for attack targets
        if (!theirHasCards && myPower >= ATTACK_MIN_POWER && myCardCount >= 2) {
            AttackAnalysis attack = analyzeAttackOpportunity(gameState, game, playerId,
                                                             mySide, location, myPower, myCardCount);
            if (attack != null && attack.viable) {
                action.addReasoning(attack.reason, attack.score);
                logger.info("[MoveEvaluator] ⚔️ ATTACK opportunity: {}", attack.reason);
                return;
            }
        }

        // === SPREAD VIABILITY ===
        // Check if we have excess power we can redistribute
        float powerNeededToStay = Math.max(theirPower + OVERKILL_THRESHOLD, ESTABLISH_THRESHOLD);
        float excessPower = myPower - powerNeededToStay;

        if (excessPower >= 2 && myCardCount >= 2) {
            SpreadAnalysis spread = analyzeSpreadViability(gameState, game, playerId, mySide,
                                                           location, myPower, myCardCount, theirPower);
            if (spread != null && spread.viable) {
                action.addReasoning(spread.reason, spread.score);
                return;
            } else if (spread != null) {
                action.addReasoning("Can't spread: " + spread.reason, BAD_DELTA);
                return;
            }
        }

        // Default: not a good time to move
        action.addReasoning("No good reason to move", BAD_DELTA);
    }

    /**
     * Calculate threat level based on power differential.
     * Ported from Python move_evaluator.py threat level logic.
     */
    private ThreatLevel calculateThreatLevel(float powerDiff) {
        int favorable = RandoConfig.BATTLE_FAVORABLE_THRESHOLD;
        int danger = RandoConfig.BATTLE_DANGER_THRESHOLD;

        if (powerDiff >= favorable + 4) {
            return ThreatLevel.CRUSH;
        } else if (powerDiff >= favorable) {
            return ThreatLevel.FAVORABLE;
        } else if (powerDiff >= -favorable) {
            return ThreatLevel.RISKY;
        } else if (powerDiff >= danger) {
            return ThreatLevel.DANGEROUS;
        } else {
            return ThreatLevel.RETREAT;
        }
    }

    /**
     * Analyze attack opportunities at adjacent locations.
     * Ported from Python move_evaluator.py _analyze_attack_opportunity
     */
    private AttackAnalysis analyzeAttackOpportunity(GameState gameState, SwccgGame game,
                                                     String playerId, Side mySide,
                                                     PhysicalCard currentLocation,
                                                     float ourPowerHere, int ourCardCount) {
        String opponentId = gameState.getOpponent(playerId);
        float avgPowerPerCard = ourPowerHere / Math.max(ourCardCount, 1);

        // Get all locations
        List<PhysicalCard> allLocations = gameState.getLocationsInOrder();
        AttackAnalysis bestAttack = null;
        float bestScore = 0;

        for (PhysicalCard adjLocation : allLocations) {
            if (adjLocation == currentLocation) continue;

            // Calculate enemy power at this location
            float theirPower = 0;
            int theirCount = 0;
            float ourPowerThere = 0;

            List<PhysicalCard> cardsAtAdj = gameState.getCardsAtLocation(adjLocation);
            for (PhysicalCard card : cardsAtAdj) {
                if (card == null) continue;
                String owner = card.getOwner();
                SwccgCardBlueprint bp = card.getBlueprint();
                if (bp == null || !bp.hasPowerAttribute()) continue;

                Float power = bp.getPower();
                if (power == null) power = 0f;

                if (opponentId != null && opponentId.equals(owner)) {
                    theirPower += power;
                    theirCount++;
                } else if (playerId.equals(owner)) {
                    ourPowerThere += power;
                }
            }

            // Skip empty locations (use spread logic for those)
            if (theirCount == 0 || theirPower == 0) continue;

            // Get opponent icons at target
            int theirIcons = getOpponentIcons(adjLocation.getBlueprint(), mySide);

            // Calculate attack viability
            float potentialPower = ourPowerThere + ourPowerHere;  // If we move everyone
            float advantage = potentialPower - theirPower;

            if (advantage >= ATTACK_POWER_ADVANTAGE) {
                float score = 50.0f;  // Base attack score

                // Bonus for crushing attacks
                if (potentialPower >= theirPower * 2) {
                    score += 25.0f;
                }

                // Bonus for opponent icons
                score += theirIcons * ICON_BONUS;

                // Bonus for bigger enemy forces
                score += theirPower / 2;

                String reason = String.format("ATTACK %d enemies with %d power (+%d advantage)",
                    (int)theirPower, (int)potentialPower, (int)advantage);
                if (theirIcons > 0) {
                    reason += " - deny " + theirIcons + " icon drain!";
                }

                if (score > bestScore) {
                    bestScore = score;
                    bestAttack = new AttackAnalysis(true, reason, score);
                }
            }
        }

        return bestAttack;
    }

    /**
     * Analyze if spreading out from this location is viable.
     * Ported from Python move_evaluator.py _analyze_spread_viability
     */
    private SpreadAnalysis analyzeSpreadViability(GameState gameState, SwccgGame game,
                                                   String playerId, Side mySide,
                                                   PhysicalCard currentLocation,
                                                   float ourPowerHere, int ourCardCount,
                                                   float theirPowerHere) {
        String opponentId = gameState.getOpponent(playerId);
        int forceAvailable = 0;  // TODO: Get from context if available

        // Calculate power we need to retain at source
        float powerToRetain = Math.max(theirPowerHere + CONTEST_MARGIN, ESTABLISH_THRESHOLD);
        float avgPowerPerCard = ourPowerHere / Math.max(ourCardCount, 1);
        float powerWeCanSpare = ourPowerHere - powerToRetain;

        if (powerWeCanSpare < 2) {
            return new SpreadAnalysis(false,
                String.format("need %d power to retain control, only have %d",
                    (int)powerToRetain, (int)ourPowerHere), 0);
        }

        // Get all locations and find spread opportunities
        List<PhysicalCard> allLocations = gameState.getLocationsInOrder();
        SpreadAnalysis bestOpportunity = null;
        float bestScore = 0;

        for (PhysicalCard adjLocation : allLocations) {
            if (adjLocation == currentLocation) continue;

            // Calculate power at this location
            float theirPower = 0;
            float ourPowerThere = 0;

            List<PhysicalCard> cardsAtAdj = gameState.getCardsAtLocation(adjLocation);
            for (PhysicalCard card : cardsAtAdj) {
                if (card == null) continue;
                String owner = card.getOwner();
                SwccgCardBlueprint bp = card.getBlueprint();
                if (bp == null || !bp.hasPowerAttribute()) continue;

                Float power = bp.getPower();
                if (power == null) power = 0f;

                if (opponentId != null && opponentId.equals(owner)) {
                    theirPower += power;
                } else if (playerId.equals(owner)) {
                    ourPowerThere += power;
                }
            }

            // Skip if we already have good presence
            if (ourPowerThere >= ESTABLISH_THRESHOLD && theirPower == 0) {
                continue;
            }

            // Get icons at destination
            int theirIcons = getOpponentIcons(adjLocation.getBlueprint(), mySide);
            int myIcons = getMyIcons(adjLocation.getBlueprint(), mySide);

            float potentialPower = ourPowerThere + powerWeCanSpare;

            // Empty location - can we establish?
            if (theirPower == 0) {
                if (potentialPower >= ESTABLISH_THRESHOLD) {
                    float score = GOOD_DELTA * 2;
                    score += theirIcons * ICON_BONUS;  // Bonus for opponent icons

                    String reason = "Can establish at empty location";
                    if (theirIcons > 0) {
                        reason += " - " + theirIcons + " opponent icon(s) = force drain!";
                    }

                    if (score > bestScore) {
                        bestScore = score;
                        bestOpportunity = new SpreadAnalysis(true, reason, score);
                    }
                }
            } else {
                // Contested - can we beat them with margin?
                float powerNeeded = theirPower + CONTEST_MARGIN;
                if (potentialPower >= powerNeeded) {
                    float score = GOOD_DELTA * 3 + theirPower / 2;
                    score += theirIcons * ICON_BONUS;

                    String reason = String.format("Can contest location with %d enemies", (int)theirPower);
                    if (theirIcons > 0) {
                        reason += " - " + theirIcons + " opponent icon(s) = force drain!";
                    }

                    if (score > bestScore) {
                        bestScore = score;
                        bestOpportunity = new SpreadAnalysis(true, reason, score);
                    }
                }
            }
        }

        if (bestOpportunity != null) {
            return bestOpportunity;
        }

        return new SpreadAnalysis(false, "no good adjacent locations", 0);
    }

    /**
     * Handle Land action - penalize starfighters.
     */
    private void handleLandAction(EvaluatedAction action, String actionLower, PhysicalCard card) {
        boolean isStarfighter = false;
        String cardName = "unknown";

        if (card != null) {
            cardName = card.getTitle();
            CardSubtype subtype = card.getBlueprint().getCardSubtype();
            if (subtype == CardSubtype.STARFIGHTER) {
                isStarfighter = true;
            }
        }

        // Fallback to name-based detection
        if (!isStarfighter) {
            isStarfighter = actionLower.contains("x-wing") ||
                actionLower.contains("y-wing") ||
                actionLower.contains("a-wing") ||
                actionLower.contains("b-wing") ||
                actionLower.contains("tie") ||
                actionLower.contains("starfighter");
        }

        if (isStarfighter) {
            action.addReasoning("AVOID: Landing starfighter (" + cardName + ") wastes combat power!", -100.0f);
            logger.info("[MoveEvaluator] BLOCKED: Landing starfighter {}", cardName);
        } else {
            action.addReasoning("Land (ground deployment)", 10.0f);
        }
    }

    /**
     * Get opponent icons at a location.
     */
    private int getOpponentIcons(SwccgCardBlueprint bp, Side mySide) {
        if (bp == null) return 0;
        if (mySide == Side.LIGHT) {
            return bp.getIconCount(Icon.DARK_FORCE);
        } else {
            return bp.getIconCount(Icon.LIGHT_FORCE);
        }
    }

    /**
     * Get our icons at a location.
     */
    private int getMyIcons(SwccgCardBlueprint bp, Side mySide) {
        if (bp == null) return 0;
        if (mySide == Side.LIGHT) {
            return bp.getIconCount(Icon.LIGHT_FORCE);
        } else {
            return bp.getIconCount(Icon.DARK_FORCE);
        }
    }

    // Helper classes for analysis results
    private static class AttackAnalysis {
        boolean viable;
        String reason;
        float score;

        AttackAnalysis(boolean viable, String reason, float score) {
            this.viable = viable;
            this.reason = reason;
            this.score = score;
        }
    }

    private static class SpreadAnalysis {
        boolean viable;
        String reason;
        float score;

        SpreadAnalysis(boolean viable, String reason, float score) {
            this.viable = viable;
            this.reason = reason;
            this.score = score;
        }
    }
}
