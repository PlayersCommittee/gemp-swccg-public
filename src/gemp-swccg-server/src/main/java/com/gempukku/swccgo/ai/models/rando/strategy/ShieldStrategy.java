package com.gempukku.swccgo.ai.models.rando.strategy;

import com.gempukku.swccgo.ai.models.rando.RandoLogger;
import com.gempukku.swccgo.common.Side;

import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * Defensive Shield Strategy for SWCCG AI.
 *
 * Based on NARP strategy guides, implements intelligent shield selection:
 * - Categorizes shields by priority and use case
 * - Considers game state, turn number, and opponent's deck
 * - Tracks shields played (4 max per game by default)
 * - Monitors opponent shields to avoid redundant plays
 *
 * Ported from Python shield_strategy.py
 */
public class ShieldStrategy {
    private static final Logger LOG = RandoLogger.getStrategyLogger();

    // =========================================================================
    // Shield Category Enum
    // =========================================================================

    public enum ShieldCategory {
        AUTO_PLAY_IMMEDIATE("auto_immediate"),   // Play immediately (turn 1-2)
        AUTO_PLAY_EARLY("auto_early"),           // Play early before opponent drains
        SITUATIONAL_HIGH("situational_high"),    // Play based on opponent deck/actions
        SITUATIONAL_MEDIUM("situational_medium"), // Context-dependent
        LOW_PRIORITY("low_priority"),            // Rarely needed
        NEVER("never");                          // Obsolete or virtual version exists

        private final String value;

        ShieldCategory(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    // =========================================================================
    // Shield Info Class
    // =========================================================================

    public static class ShieldInfo {
        public final String name;
        public final List<String> blueprintIds;
        public final ShieldCategory category;
        public final String description;
        public final List<String> playIfOpponentHas;
        public final List<String> playIfWeHave;
        public final List<String> playIfOpponentObjective;
        public final int maxTurnToPlay;
        public final int minTurnToPlay;

        public ShieldInfo(String name, List<String> blueprintIds, ShieldCategory category,
                         String description, List<String> playIfOpponentHas,
                         List<String> playIfWeHave, List<String> playIfOpponentObjective,
                         int maxTurnToPlay, int minTurnToPlay) {
            this.name = name;
            this.blueprintIds = blueprintIds;
            this.category = category;
            this.description = description;
            this.playIfOpponentHas = playIfOpponentHas != null ? playIfOpponentHas : Collections.emptyList();
            this.playIfWeHave = playIfWeHave != null ? playIfWeHave : Collections.emptyList();
            this.playIfOpponentObjective = playIfOpponentObjective != null ? playIfOpponentObjective : Collections.emptyList();
            this.maxTurnToPlay = maxTurnToPlay;
            this.minTurnToPlay = minTurnToPlay;
        }

        // Convenience constructor for simpler shields
        public ShieldInfo(String name, String blueprintId, ShieldCategory category,
                         String description, int maxTurnToPlay) {
            this(name, Collections.singletonList(blueprintId), category, description,
                null, null, null, maxTurnToPlay, 0);
        }
    }

    // =========================================================================
    // Shield Databases
    // =========================================================================

    private static final Map<String, ShieldInfo> DARK_SHIELDS = new LinkedHashMap<>();
    private static final Map<String, ShieldInfo> LIGHT_SHIELDS = new LinkedHashMap<>();

    static {
        initializeDarkShields();
        initializeLightShields();
    }

    private static void initializeDarkShields() {
        // === AUTO PLAY IMMEDIATELY ===
        DARK_SHIELDS.put("Allegations Of Corruption", new ShieldInfo(
            "Allegations Of Corruption", "13_52", ShieldCategory.AUTO_PLAY_IMMEDIATE,
            "Grabber - grab opponent's key Used Interrupt", 2));

        DARK_SHIELDS.put("Secret Plans", new ShieldInfo(
            "Secret Plans", "13_86", ShieldCategory.AUTO_PLAY_IMMEDIATE,
            "Makes retrieval cost 1 force per card", 3));

        // === AUTO PLAY EARLY ===
        DARK_SHIELDS.put("Battle Order", new ShieldInfo(
            "Battle Order", "13_54", ShieldCategory.AUTO_PLAY_EARLY,
            "Opponent pays 3 to drain without both theaters", 4));

        DARK_SHIELDS.put("Come Here You Big Coward", new ShieldInfo(
            "Come Here You Big Coward", Arrays.asList("13_61", "225_3"),
            ShieldCategory.AUTO_PLAY_EARLY,
            "Punishes stacking at one battleground, stops retrieval",
            null, null, null, 5, 0));

        // === SITUATIONAL HIGH ===
        DARK_SHIELDS.put("A Useless Gesture (V)", new ShieldInfo(
            "A Useless Gesture (V)", Arrays.asList("223_7"),
            ShieldCategory.SITUATIONAL_HIGH,
            "Limits Watch Your Step lost pile plays",
            null, null, Arrays.asList("watch your step"), 99, 0));

        DARK_SHIELDS.put("Do They Have A Code Clearance?", new ShieldInfo(
            "Do They Have A Code Clearance?", Arrays.asList("13_66"),
            ShieldCategory.SITUATIONAL_HIGH,
            "Grabs retrieval interrupts, reduces all retrieval by 1",
            Arrays.asList("kessel run", "death star plans", "on the edge", "harvest", "jedi levitation"),
            null, null, 99, 0));

        DARK_SHIELDS.put("Firepower (V)", new ShieldInfo(
            "Firepower (V)", Arrays.asList("200_95"),
            ShieldCategory.SITUATIONAL_HIGH,
            "Damage when opponent moves away, retrieval when in both theaters",
            Arrays.asList("dodge", "path of least resistance", "run luke run", "hyper escape"),
            null, null, 99, 0));

        DARK_SHIELDS.put("We'll Let Fate-a Decide, Huh?", new ShieldInfo(
            "We'll Let Fate-a Decide, Huh?", Arrays.asList("13_96", "223_26"),
            ShieldCategory.SITUATIONAL_HIGH,
            "Cancels Sabacc, Beggar, Frozen Assets",
            Arrays.asList("sabacc", "beggar", "frozen assets", "draw their fire"),
            null, null, 99, 0));

        DARK_SHIELDS.put("You Cannot Hide Forever (V)", new ShieldInfo(
            "You Cannot Hide Forever (V)", "200_100", ShieldCategory.SITUATIONAL_HIGH,
            "Stops Podracing damage, cancels inserts", 99));

        // === SITUATIONAL MEDIUM ===
        DARK_SHIELDS.put("Resistance", new ShieldInfo(
            "Resistance", "13_84", ShieldCategory.SITUATIONAL_MEDIUM,
            "Limits force drains to 2 if we occupy 3 battlegrounds", 99));

        DARK_SHIELDS.put("There Is No Try", new ShieldInfo(
            "There Is No Try", Arrays.asList("13_90"),
            ShieldCategory.SITUATIONAL_MEDIUM,
            "Anti-Sense/Alter (punishes both players)",
            Arrays.asList("sense", "alter"), null, null, 99, 0));

        DARK_SHIELDS.put("Oppressive Enforcement", new ShieldInfo(
            "Oppressive Enforcement", Arrays.asList("13_81"),
            ShieldCategory.SITUATIONAL_MEDIUM,
            "Anti-Sense/Alter (only helps us)",
            Arrays.asList("sense", "alter"), Arrays.asList("sense", "alter"), null, 99, 0));

        // === LOW PRIORITY ===
        DARK_SHIELDS.put("Death Star Sentry (V)", new ShieldInfo(
            "Death Star Sentry (V)", "223_7", ShieldCategory.LOW_PRIORITY,
            "Stops non-unique swarms, cancels Colo Claw Fish", 99));

        // === NEVER PLAY ===
        DARK_SHIELDS.put("A Useless Gesture", new ShieldInfo(
            "A Useless Gesture", "13_51", ShieldCategory.NEVER,
            "Virtual version is better", 99));

        DARK_SHIELDS.put("Crossfire", new ShieldInfo(
            "Crossfire", "13_63", ShieldCategory.NEVER,
            "S-foils rarely played, V version exists", 99));
    }

    private static void initializeLightShields() {
        // === AUTO PLAY IMMEDIATELY ===
        LIGHT_SHIELDS.put("A Tragedy Has Occurred", new ShieldInfo(
            "A Tragedy Has Occurred", "13_3", ShieldCategory.AUTO_PLAY_IMMEDIATE,
            "Grabber - grab opponent's key Used Interrupt", 2));

        LIGHT_SHIELDS.put("Aim High", new ShieldInfo(
            "Aim High", "13_4", ShieldCategory.AUTO_PLAY_IMMEDIATE,
            "Makes retrieval cost 1 force per card", 3));

        // === AUTO PLAY EARLY ===
        LIGHT_SHIELDS.put("Battle Plan", new ShieldInfo(
            "Battle Plan", "13_8", ShieldCategory.AUTO_PLAY_EARLY,
            "Opponent pays 3 to drain without both theaters", 4));

        LIGHT_SHIELDS.put("Simple Tricks And Nonsense", new ShieldInfo(
            "Simple Tricks And Nonsense", "200_28", ShieldCategory.AUTO_PLAY_EARLY,
            "Punishes stacking, stops drains at non-BGs if < 2 BGs", 5));

        LIGHT_SHIELDS.put("Goldenrod", new ShieldInfo(
            "Goldenrod", Arrays.asList("223_49"),
            ShieldCategory.AUTO_PLAY_EARLY,
            "Makes Blizzard 4 deploys cost 2, Executor cost 2",
            Arrays.asList("blizzard 4", "they must never again leave this city"),
            null, null, 3, 0));

        // === SITUATIONAL HIGH ===
        LIGHT_SHIELDS.put("Weapons Display (V)", new ShieldInfo(
            "Weapons Display (V)", Arrays.asList("200_30"),
            ShieldCategory.SITUATIONAL_HIGH,
            "Damage when opponent excludes from battle, retrieval in both theaters",
            Arrays.asList("imperial barrier", "stunning leader", "you are beaten", "force push"),
            null, null, 99, 0));

        LIGHT_SHIELDS.put("Your Insight Serves You Well (V)", new ShieldInfo(
            "Your Insight Serves You Well (V)", "200_32", ShieldCategory.SITUATIONAL_HIGH,
            "Stops Podracing damage, Scanning Crew, inserts", 99));

        // === SITUATIONAL MEDIUM ===
        LIGHT_SHIELDS.put("Ultimatum", new ShieldInfo(
            "Ultimatum", "13_44", ShieldCategory.SITUATIONAL_MEDIUM,
            "Limits force drains to 2 if we occupy 3 battlegrounds", 99));

        LIGHT_SHIELDS.put("Do, Or Do Not", new ShieldInfo(
            "Do, Or Do Not", Arrays.asList("13_15"),
            ShieldCategory.SITUATIONAL_MEDIUM,
            "Anti-Sense/Alter (punishes both players)",
            Arrays.asList("sense", "alter"), null, null, 99, 0));

        LIGHT_SHIELDS.put("Wise Advice", new ShieldInfo(
            "Wise Advice", Arrays.asList("13_47"),
            ShieldCategory.SITUATIONAL_MEDIUM,
            "Anti-Sense/Alter (only helps us)",
            Arrays.asList("sense", "alter"), null, null, 99, 0));

        // === LOW PRIORITY ===
        LIGHT_SHIELDS.put("He Can Go About His Business", new ShieldInfo(
            "He Can Go About His Business", "13_22", ShieldCategory.LOW_PRIORITY,
            "Stops Brangus Glee shenanigans", 99));

        // === NEVER PLAY ===
        LIGHT_SHIELDS.put("A Close Race", new ShieldInfo(
            "A Close Race", "13_1", ShieldCategory.NEVER,
            "Your Insight Serves You Well (V) is strictly better", 99));

        LIGHT_SHIELDS.put("Another Pathetic Lifeform", new ShieldInfo(
            "Another Pathetic Lifeform", "13_6", ShieldCategory.NEVER,
            "Not very effective", 99));
    }

    // =========================================================================
    // Shield Tracker
    // =========================================================================

    private Side mySide;  // Set when game starts
    private final Set<String> shieldsPlayed = new HashSet<>();
    private int maxShields = 4;
    private final Set<String> opponentShields = new HashSet<>();
    private final Set<String> opponentCardsSeen = new HashSet<>();
    private String opponentObjective = null;

    // Shield pacing - don't play all shields immediately
    private static final Map<Integer, Integer> SHIELD_PACING = new LinkedHashMap<>();
    static {
        SHIELD_PACING.put(1, 2);  // Play at most 2 shields on turn 1
        SHIELD_PACING.put(2, 3);  // Play at most 3 shields by turn 2
        SHIELD_PACING.put(3, 4);  // Play all 4 shields by turn 3
    }

    /**
     * Default constructor - side will be set when game starts.
     */
    public ShieldStrategy() {
    }

    public ShieldStrategy(Side side) {
        this.mySide = side;
    }

    /**
     * Set the side after initialization (called when game starts).
     */
    public void setSide(Side side) {
        this.mySide = side;
    }

    /**
     * Get the current side.
     */
    public Side getSide() {
        return mySide;
    }

    /**
     * Reset tracker for new game.
     */
    public void reset() {
        shieldsPlayed.clear();
        opponentShields.clear();
        opponentCardsSeen.clear();
        opponentObjective = null;
        maxShields = 4;
    }

    /**
     * How many shields can we still play?
     */
    public int shieldsRemaining() {
        return Math.max(0, maxShields - shieldsPlayed.size());
    }

    /**
     * How many shields should we have played by this turn (pacing limit).
     */
    public int shieldsAllowedThisTurn(int turnNumber) {
        for (int turn : new int[]{3, 2, 1}) {
            if (turnNumber >= turn && SHIELD_PACING.containsKey(turn)) {
                return SHIELD_PACING.get(turn);
            }
        }
        return 0;
    }

    /**
     * Check if we've reached our shield pacing cap for this turn.
     */
    public boolean atPacingCap(int turnNumber) {
        int shieldsPlayedCount = shieldsPlayed.size();
        int maxForTurn = shieldsAllowedThisTurn(turnNumber);
        boolean atCap = shieldsPlayedCount >= maxForTurn;
        if (atCap) {
            LOG.debug("At shield pacing cap: {}/{} for turn {}", shieldsPlayedCount, maxForTurn, turnNumber);
        }
        return atCap;
    }

    /**
     * Record that we played a shield.
     */
    public void recordShieldPlayed(String blueprintId, String cardTitle) {
        shieldsPlayed.add(blueprintId);
        int played = shieldsPlayed.size();
        int remaining = shieldsRemaining();
        LOG.info("Shield #{} played: {} ({} remaining of {})", played, cardTitle, remaining, maxShields);
    }

    /**
     * Record an opponent shield we've seen.
     */
    public void recordOpponentShield(String blueprintId, String cardTitle) {
        opponentShields.add(blueprintId);
        LOG.debug("Opponent shield: {}", cardTitle);
    }

    /**
     * Record a card title opponent has played (for situational shields).
     */
    public void recordOpponentCard(String cardTitle) {
        opponentCardsSeen.add(cardTitle.toLowerCase(Locale.ROOT));
    }

    /**
     * Record opponent's objective.
     */
    public void setOpponentObjective(String objectiveTitle) {
        this.opponentObjective = objectiveTitle.toLowerCase(Locale.ROOT);
        LOG.info("Opponent objective: {}", objectiveTitle);
    }

    /**
     * Check if shield conditions are met.
     */
    private ConditionResult checkConditions(ShieldInfo shield, int turnNumber) {
        List<String> reasons = new ArrayList<>();

        // Check turn timing
        if (turnNumber > shield.maxTurnToPlay) {
            reasons.add("Past optimal turn (" + shield.maxTurnToPlay + ")");
        }

        // Check opponent objective conditions
        if (!shield.playIfOpponentObjective.isEmpty() && opponentObjective != null) {
            for (String obj : shield.playIfOpponentObjective) {
                if (opponentObjective.contains(obj.toLowerCase(Locale.ROOT))) {
                    reasons.add("Opponent plays " + obj);
                    return new ConditionResult(true, reasons);
                }
            }
        }

        // Check opponent card conditions
        if (!shield.playIfOpponentHas.isEmpty()) {
            for (String card : shield.playIfOpponentHas) {
                if (opponentCardsSeen.contains(card.toLowerCase(Locale.ROOT))) {
                    reasons.add("Opponent has " + card);
                    return new ConditionResult(true, reasons);
                }
            }
        }

        // For auto-play shields, always play early
        if (shield.category == ShieldCategory.AUTO_PLAY_IMMEDIATE ||
            shield.category == ShieldCategory.AUTO_PLAY_EARLY) {
            if (turnNumber <= shield.maxTurnToPlay) {
                reasons.add("Auto-play shield (early)");
                return new ConditionResult(true, reasons);
            }
        }

        return new ConditionResult(!reasons.isEmpty(), reasons);
    }

    /**
     * Score a defensive shield for deployment priority.
     */
    public float scoreShield(String blueprintId, String cardTitle, int turnNumber) {
        // Check if already played
        if (shieldsPlayed.contains(blueprintId)) {
            return -100.0f;
        }

        // Check pacing cap
        if (atPacingCap(turnNumber)) {
            LOG.info("{}: Holding back (pacing cap for turn {})", cardTitle, turnNumber);
            return -50.0f;
        }

        // Find the shield info
        Map<String, ShieldInfo> shieldDb = (mySide == Side.DARK) ? DARK_SHIELDS : LIGHT_SHIELDS;
        ShieldInfo shieldInfo = null;

        for (Map.Entry<String, ShieldInfo> entry : shieldDb.entrySet()) {
            if (entry.getValue().blueprintIds.contains(blueprintId)) {
                shieldInfo = entry.getValue();
                break;
            }
            // Also check by title match
            if (entry.getKey().toLowerCase(Locale.ROOT).contains(cardTitle.toLowerCase(Locale.ROOT))) {
                shieldInfo = entry.getValue();
                break;
            }
        }

        if (shieldInfo == null) {
            LOG.debug("Unknown shield: {} ({})", cardTitle, blueprintId);
            return 50.0f;
        }

        // Base score by category
        float score;
        switch (shieldInfo.category) {
            case AUTO_PLAY_IMMEDIATE:
                score = 200.0f;
                break;
            case AUTO_PLAY_EARLY:
                score = 150.0f;
                break;
            case SITUATIONAL_HIGH:
                score = 100.0f;
                break;
            case SITUATIONAL_MEDIUM:
                score = 75.0f;
                break;
            case LOW_PRIORITY:
                score = 25.0f;
                break;
            case NEVER:
            default:
                score = -100.0f;
        }

        // Check conditions
        ConditionResult result = checkConditions(shieldInfo, turnNumber);
        if (result.shouldPlay && !result.reasons.isEmpty()) {
            score += 50.0f;
            for (String reason : result.reasons) {
                LOG.debug("{}: +50 ({})", cardTitle, reason);
            }
        }

        // Timing adjustments
        if (turnNumber > shieldInfo.maxTurnToPlay) {
            float latePenalty = Math.min(50.0f, (turnNumber - shieldInfo.maxTurnToPlay) * 10);
            score -= latePenalty;
            LOG.debug("{}: -{} (turn {} > max {})", cardTitle, latePenalty, turnNumber, shieldInfo.maxTurnToPlay);
        }

        // Early game bonus for auto-play shields
        if (shieldInfo.category == ShieldCategory.AUTO_PLAY_IMMEDIATE && turnNumber <= 2) {
            score += 25.0f;
        }

        // Shields remaining affects urgency
        if (shieldsRemaining() <= 1) {
            if (shieldInfo.category == ShieldCategory.LOW_PRIORITY ||
                shieldInfo.category == ShieldCategory.SITUATIONAL_MEDIUM) {
                score -= 30.0f;
            }
        }

        return score;
    }

    /**
     * Get the description of a shield for logging.
     */
    public String getShieldDescription(String blueprintId, String cardTitle) {
        Map<String, ShieldInfo> shieldDb = (mySide == Side.DARK) ? DARK_SHIELDS : LIGHT_SHIELDS;

        for (Map.Entry<String, ShieldInfo> entry : shieldDb.entrySet()) {
            ShieldInfo info = entry.getValue();
            if (info.blueprintIds.contains(blueprintId) ||
                entry.getKey().toLowerCase(Locale.ROOT).contains(cardTitle.toLowerCase(Locale.ROOT))) {
                return info.category.getValue() + ": " + info.description;
            }
        }
        return "Unknown shield";
    }

    // =========================================================================
    // Helper Classes
    // =========================================================================

    private static class ConditionResult {
        final boolean shouldPlay;
        final List<String> reasons;

        ConditionResult(boolean shouldPlay, List<String> reasons) {
            this.shouldPlay = shouldPlay;
            this.reasons = reasons;
        }
    }

    /**
     * Score result with reason.
     */
    public static class ShieldScoreResult {
        public final float score;
        public final String reason;

        public ShieldScoreResult(float score, String reason) {
            this.score = score;
            this.reason = reason;
        }
    }

    /**
     * Convenience method to score a shield and get reason.
     */
    public ShieldScoreResult scoreShieldWithReason(String blueprintId, String cardTitle, int turnNumber) {
        float score = scoreShield(blueprintId, cardTitle, turnNumber);
        String reason = getShieldDescription(blueprintId, cardTitle);
        return new ShieldScoreResult(score, reason);
    }
}
