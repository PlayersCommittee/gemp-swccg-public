package com.gempukku.swccgo.ai.models.rando.strategy;

import com.gempukku.swccgo.ai.common.AiPriorityCards;
import com.gempukku.swccgo.ai.models.rando.RandoConfig;
import com.gempukku.swccgo.ai.models.rando.RandoLogger;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;

import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * Strategy Controller
 *
 * Manages game strategy state including:
 * - Battle Order Rules tracking (force drain costs)
 * - Game phase (EARLY/MID/LATE)
 * - Force economy tracking and targets
 * - Location priority scoring
 * - Cross-turn strategy focus (GROUND/SPACE/BALANCED)
 *
 * Combines functionality from Python strategy_controller.py and game_strategy.py
 */
public class StrategyController {
    private static final Logger LOG = RandoLogger.getStrategyLogger();

    // =========================================================================
    // Constants
    // =========================================================================

    // Battle Order / Battle Plan card IDs (from Python board_state.py)
    // When either is in play, force drains cost +3 unless draining player
    // occupies both a battleground site AND a battleground system.

    // Dark Side Battle Order cards
    private static final Set<String> BATTLE_ORDER_DARK = new HashSet<>(Arrays.asList(
        "8_118",   // Battle Order (Effect - Endor)
        "13_54",   // Battle Order (Defensive Shield - Reflections 3)
        "12_129"   // Battle Order & First Strike (Effect - Coruscant)
    ));

    // Light Side Battle Plan cards (same effect as Dark's Battle Order)
    private static final Set<String> BATTLE_PLAN_LIGHT = new HashSet<>(Arrays.asList(
        "8_35",    // Battle Plan (Effect - Endor)
        "13_8",    // Battle Plan (Defensive Shield - Reflections 3)
        "12_41"    // Battle Plan & Draw Their Fire (Effect - Coruscant)
    ));

    // All cards that trigger Battle Order rules
    private static final Set<String> ALL_BATTLE_ORDER_CARDS;
    static {
        ALL_BATTLE_ORDER_CARDS = new HashSet<>();
        ALL_BATTLE_ORDER_CARDS.addAll(BATTLE_ORDER_DARK);
        ALL_BATTLE_ORDER_CARDS.addAll(BATTLE_PLAN_LIGHT);
    }

    private static final int EARLY_GAME_TURNS = 3;
    private static final int MID_GAME_TURNS = 8;

    private static final int FORCE_GEN_TARGET_EARLY = 8;
    private static final int FORCE_GEN_TARGET_MID = 6;
    private static final int FORCE_GEN_TARGET_LATE = 5;

    private static final int HAND_SOFT_CAP = 12;
    private static final int HAND_HARD_CAP = 16;
    private static final int MIN_RESERVE_TO_KEEP = 3;
    private static final int MAX_RESERVE_CHECKS_PER_TURN = 2;

    // =========================================================================
    // Enums
    // =========================================================================

    public enum GamePhase {
        EARLY("early"),   // Turns 1-3: Establishing force generation
        MID("mid"),       // Turns 4-8: Building board presence
        LATE("late");     // Turns 9+: Consolidating and finishing

        private final String value;

        GamePhase(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public enum StrategyFocus {
        GROUND("ground"),      // Prioritize characters, vehicles, sites
        SPACE("space"),        // Prioritize starships, pilots, systems
        BALANCED("balanced");  // No preference

        private final String value;

        StrategyFocus(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public enum ThreatLevel {
        SAFE("safe"),              // We control, no enemies
        CRUSH("crush"),            // Overwhelming advantage (6+)
        FAVORABLE("favorable"),    // Good odds (2-5)
        RISKY("risky"),            // Could go either way (-2 to +2)
        DANGEROUS("dangerous"),    // Bad odds (-6 to -2)
        RETREAT("retreat");        // Should retreat (<-6)

        private final String value;

        ThreatLevel(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    // =========================================================================
    // Instance Fields
    // =========================================================================

    private Side mySide;  // Set when game starts

    // Game state
    private boolean underBattleOrderRules = false;
    private boolean hasShieldsToPlay = true;
    private boolean offeredConcedeThisGame = false;

    // Game phase
    private GamePhase phase = GamePhase.EARLY;
    private int turnNumber = 0;

    // Force economy
    private int myForceGeneration = 0;
    private int forceGenerationTarget = FORCE_GEN_TARGET_EARLY;
    private int forceDeficit = FORCE_GEN_TARGET_EARLY;

    // Strategy focus
    private StrategyFocus currentFocus = StrategyFocus.BALANCED;
    private float focusConfidence = 0.5f;
    private int turnsWithFocus = 0;
    private int focusDeployments = 0;

    // Location tracking
    private final List<Integer> contestedLocations = new ArrayList<>();
    private final List<Integer> dangerousLocations = new ArrayList<>();

    // Reserve deck limiting
    private int reserveChecksThisTurn = 0;
    private final Set<String> cardsSeenInReserve = new HashSet<>();
    private int lastReserveCheckTurn = 0;

    // Battle tracking
    private int battlesWon = 0;
    private int battlesLost = 0;

    // Strategy tracking
    private String lastDecisionReason = "No decisions made yet.";

    /**
     * Default constructor - side will be set when game starts.
     */
    public StrategyController() {
        LOG.info("StrategyController initialized (side will be set when game starts)");
    }

    public StrategyController(Side side) {
        this.mySide = side;
        LOG.info("StrategyController initialized for {} side", side);
    }

    /**
     * Set the side after initialization (called when game starts).
     */
    public void setSide(Side side) {
        this.mySide = side;
        LOG.debug("StrategyController side set to {}", side);
    }

    /**
     * Get the current side.
     */
    public Side getSide() {
        return mySide;
    }

    /**
     * Reset strategy state for new game.
     */
    public void reset() {
        underBattleOrderRules = false;
        hasShieldsToPlay = true;
        offeredConcedeThisGame = false;

        phase = GamePhase.EARLY;
        turnNumber = 0;

        myForceGeneration = 0;
        forceGenerationTarget = FORCE_GEN_TARGET_EARLY;
        forceDeficit = FORCE_GEN_TARGET_EARLY;

        currentFocus = StrategyFocus.BALANCED;
        focusConfidence = 0.5f;
        turnsWithFocus = 0;
        focusDeployments = 0;

        contestedLocations.clear();
        dangerousLocations.clear();

        reserveChecksThisTurn = 0;
        cardsSeenInReserve.clear();
        lastReserveCheckTurn = 0;

        battlesWon = 0;
        battlesLost = 0;

        LOG.info("StrategyController reset for new game");
    }

    /**
     * Called at start of each turn to reset per-turn tracking.
     */
    public void startNewTurn(int turnNumber) {
        this.turnNumber = turnNumber;
        reserveChecksThisTurn = 0;

        // Update game phase
        if (turnNumber <= EARLY_GAME_TURNS) {
            phase = GamePhase.EARLY;
            forceGenerationTarget = FORCE_GEN_TARGET_EARLY;
        } else if (turnNumber <= MID_GAME_TURNS) {
            phase = GamePhase.MID;
            forceGenerationTarget = FORCE_GEN_TARGET_MID;
        } else {
            phase = GamePhase.LATE;
            forceGenerationTarget = FORCE_GEN_TARGET_LATE;
        }

        // Clear old reserve card memory after cooldown
        if (turnNumber - lastReserveCheckTurn > 2) {
            cardsSeenInReserve.clear();
        }

        LOG.debug("Turn {}: Phase={}, Gen target={}", turnNumber, phase.getValue(), forceGenerationTarget);
    }

    // =========================================================================
    // Battle Order Management
    // =========================================================================

    public void setUnderBattleOrderRules(boolean underBattleOrder) {
        boolean wasUnder = this.underBattleOrderRules;
        this.underBattleOrderRules = underBattleOrder;

        if (underBattleOrder && !wasUnder) {
            LOG.info("Now under Battle Order rules - force drains cost +3!");
        } else if (wasUnder && !underBattleOrder) {
            LOG.info("No longer under Battle Order rules");
        }
    }

    public boolean isUnderBattleOrderRules() {
        return underBattleOrderRules;
    }

    /**
     * Scan the game state for Battle Order or Battle Plan cards in play.
     * Updates the underBattleOrderRules flag accordingly.
     *
     * This mirrors Python's board_state.is_under_battle_order() which checks
     * for specific blueprint IDs in the SIDE_OF_TABLE zone.
     *
     * @param gameState the current game state
     */
    public void updateBattleOrderFromGameState(GameState gameState) {
        if (gameState == null) {
            return;
        }

        boolean foundBattleOrder = false;
        String foundCard = null;
        String foundOwner = null;

        try {
            for (PhysicalCard card : gameState.getAllPermanentCards()) {
                if (card == null) continue;

                Zone zone = card.getZone();
                // Battle Order/Plan cards live in SIDE_OF_TABLE zone
                // (matches Python board_state.is_under_battle_order())
                if (zone != Zone.SIDE_OF_TABLE) {
                    continue;
                }

                // Get blueprint ID
                String blueprintId = card.getBlueprintId(true);
                if (blueprintId == null) continue;

                // Check if this is a Battle Order/Plan card
                if (ALL_BATTLE_ORDER_CARDS.contains(blueprintId)) {
                    foundBattleOrder = true;
                    foundCard = card.getTitle();
                    foundOwner = card.getOwner();
                    break;  // Found one, that's enough
                }
            }
        } catch (Exception e) {
            LOG.warn("Error scanning for Battle Order cards: {}", e.getMessage());
        }

        // Update flag
        boolean wasUnder = underBattleOrderRules;
        if (foundBattleOrder && !wasUnder) {
            LOG.info("⚔️ Battle Order detected: {} (owner: {}) - force drains now cost +3!",
                foundCard, foundOwner);
        }
        setUnderBattleOrderRules(foundBattleOrder);
    }

    /**
     * Check if a specific blueprint ID is a Battle Order/Plan card.
     * Useful for evaluators that need to know before the card is in play.
     */
    public static boolean isBattleOrderCard(String blueprintId) {
        return blueprintId != null && ALL_BATTLE_ORDER_CARDS.contains(blueprintId);
    }

    // =========================================================================
    // Card Evaluation
    // =========================================================================

    /**
     * Check if a card should be avoided (bad cards to play).
     */
    public boolean isAvoidUsingCard(String cardTitle) {
        if (cardTitle == null) return false;

        if (cardTitle.contains("Wokling")) {
            return true;
        }
        if (cardTitle.contains("Anger, Fear, Aggression") ||
            cardTitle.contains("Knowledge And Defense")) {
            return true;
        }
        return false;
    }

    /**
     * Check if a card is high value (should be protected).
     */
    public boolean isHighValueCard(String cardType, String cardTitle, String blueprintId) {
        // Check priority cards system first
        if (blueprintId != null && AiPriorityCards.isPriorityCard(blueprintId)) {
            return true;
        }

        // Fall back to title-based check
        if (cardTitle != null && AiPriorityCards.isPriorityCardByTitle(cardTitle)) {
            return true;
        }

        // Legacy checks
        if (cardTitle != null) {
            if (cardTitle.contains("Ghhhk") || cardTitle.contains("Sense") || cardTitle.contains("Alter")) {
                return true;
            }
        }

        // Non-interrupts/effects/weapons are generally high value
        if (cardType != null &&
            !cardType.contains("Interrupt") &&
            !cardType.contains("Effect") &&
            !cardType.contains("Weapon")) {
            return true;
        }

        return false;
    }

    /**
     * Get the protection score for a card.
     */
    public int getCardProtectionScore(String blueprintId, String cardTitle) {
        if (blueprintId != null) {
            int score = AiPriorityCards.getProtectionScore(blueprintId);
            if (score > 0) {
                return score;
            }
        }

        if (cardTitle != null) {
            return AiPriorityCards.getProtectionScoreByTitle(cardTitle);
        }

        return 0;
    }

    // =========================================================================
    // Force Economy
    // =========================================================================

    public void updateForceGeneration(int generation) {
        this.myForceGeneration = generation;
        this.forceDeficit = forceGenerationTarget - generation;
    }

    public int getForceDeficit() {
        return forceDeficit;
    }

    public float getLocationDeployBonus() {
        if (forceDeficit <= 0) {
            return 0.0f;
        } else if (forceDeficit <= 2) {
            return 15.0f;
        } else if (forceDeficit <= 4) {
            return 30.0f;
        } else {
            return 50.0f;
        }
    }

    // =========================================================================
    // Strategy Focus
    // =========================================================================

    public StrategyFocus getCurrentFocus() {
        return currentFocus;
    }

    public void setFocus(StrategyFocus focus) {
        if (focus != currentFocus) {
            LOG.info("Strategy focus changed: {} -> {}", currentFocus.getValue(), focus.getValue());
            this.currentFocus = focus;
            this.turnsWithFocus = 0;
            this.focusDeployments = 0;
            this.focusConfidence = 0.5f;
        }
    }

    public float getFocusDeployBonus(String cardType) {
        if (cardMatchesFocus(cardType)) {
            return 15.0f * focusConfidence;
        }
        return 0.0f;
    }

    private boolean cardMatchesFocus(String cardType) {
        if (currentFocus == StrategyFocus.BALANCED || cardType == null) {
            return false;
        }

        String typeLower = cardType.toLowerCase(Locale.ROOT);

        if (currentFocus == StrategyFocus.GROUND) {
            return typeLower.contains("character") || typeLower.contains("vehicle") || typeLower.contains("site");
        } else if (currentFocus == StrategyFocus.SPACE) {
            return typeLower.contains("starship") || typeLower.contains("system");
        }

        return false;
    }

    public void onSuccessfulDeploy(String cardType) {
        if (cardMatchesFocus(cardType)) {
            focusDeployments++;
            if (focusDeployments >= 2) {
                focusConfidence = Math.min(1.0f, focusConfidence + 0.2f);
                LOG.debug("Focus confidence increased to {}", focusConfidence);
            }
        }
    }

    public void onBattleResult(boolean won) {
        if (won) {
            battlesWon++;
        } else {
            battlesLost++;
            focusConfidence = Math.max(0.0f, focusConfidence - 0.3f);
            LOG.debug("Battle lost, focus confidence reduced to {}", focusConfidence);

            if (focusConfidence < 0.3f) {
                currentFocus = StrategyFocus.BALANCED;
                LOG.info("Focus reset to BALANCED due to low confidence");
            }
        }
    }

    // =========================================================================
    // Hand Size Management
    // =========================================================================

    public int getEffectiveSoftCap(boolean hasDeployableCards) {
        int effectiveCap;

        if (turnNumber <= 3) {
            effectiveCap = HAND_SOFT_CAP + 4;  // 16
        } else if (turnNumber <= 6) {
            effectiveCap = HAND_SOFT_CAP;      // 12
        } else {
            effectiveCap = HAND_SOFT_CAP - 4;  // 8
        }

        if (!hasDeployableCards) {
            effectiveCap += 2;
            LOG.debug("No deployable cards - allowing +2 extra draw (cap {})", effectiveCap);
        }

        return Math.max(4, Math.min(effectiveCap, HAND_HARD_CAP));
    }

    public float getHandSizePenalty(int handSize, boolean hasDeployableCards) {
        int softCap = getEffectiveSoftCap(hasDeployableCards);

        if (handSize >= HAND_HARD_CAP) {
            return -100.0f;
        } else if (handSize >= softCap) {
            int overflow = handSize - softCap;
            return -20.0f * overflow;
        }
        return 0.0f;
    }

    // =========================================================================
    // Reserve Deck Management
    // =========================================================================

    public boolean shouldCheckReserve() {
        return reserveChecksThisTurn < MAX_RESERVE_CHECKS_PER_TURN;
    }

    public void recordReserveCheck(List<String> cardsSeen) {
        reserveChecksThisTurn++;
        lastReserveCheckTurn = turnNumber;

        if (cardsSeen != null) {
            cardsSeenInReserve.addAll(cardsSeen);
        }

        LOG.debug("Reserve check #{} this turn", reserveChecksThisTurn);
    }

    public boolean isCardRecentlySeenInReserve(String blueprintId) {
        return cardsSeenInReserve.contains(blueprintId);
    }

    // =========================================================================
    // Force Activation Recommendations
    // =========================================================================

    public int getForceActivationAmount(int maxAvailable, int currentForce, int reserveSize) {
        int target;

        // Early game: Activate aggressively
        if (phase == GamePhase.EARLY) {
            target = Math.min(maxAvailable, myForceGeneration + 2);
        }
        // Mid game: Balance activation with reserve
        else if (phase == GamePhase.MID) {
            target = Math.min(maxAvailable, 8);
            if (reserveSize < 10) {
                target = Math.min(target, maxAvailable - MIN_RESERVE_TO_KEEP);
            }
        }
        // Late game: Conservative
        else {
            target = Math.min(maxAvailable, Math.min(4, myForceGeneration));
        }

        // Don't over-activate if we already have a lot
        if (currentForce > 12) {
            target = Math.min(target, 2);
        }

        // Ensure we leave some in reserve
        if (reserveSize <= maxAvailable && reserveSize <= 5) {
            target = Math.min(target, Math.max(0, reserveSize - MIN_RESERVE_TO_KEEP));
        }

        return Math.max(0, target);
    }

    // =========================================================================
    // Threat Assessment
    // =========================================================================

    public ThreatLevel assessThreatLevel(float myPower, float theirPower) {
        if (theirPower == 0) {
            return ThreatLevel.SAFE;
        }

        float powerDiff = myPower - theirPower;
        int favorable = RandoConfig.BATTLE_FAVORABLE_THRESHOLD;
        int danger = RandoConfig.BATTLE_DANGER_THRESHOLD;
        int crush = favorable + 4;

        if (powerDiff >= crush) {
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

    // =========================================================================
    // Status
    // =========================================================================

    public GamePhase getPhase() {
        return phase;
    }

    public int getTurnNumber() {
        return turnNumber;
    }

    public Side getMySide() {
        return mySide;
    }

    public Map<String, Object> getStatus() {
        Map<String, Object> status = new LinkedHashMap<>();
        status.put("phase", phase.getValue());
        status.put("turn", turnNumber);
        status.put("force_generation", myForceGeneration);
        status.put("force_deficit", forceDeficit);
        status.put("focus", currentFocus.getValue());
        status.put("focus_confidence", focusConfidence);
        status.put("under_battle_order_rules", underBattleOrderRules);
        status.put("battles_won", battlesWon);
        status.put("battles_lost", battlesLost);
        return status;
    }
}
