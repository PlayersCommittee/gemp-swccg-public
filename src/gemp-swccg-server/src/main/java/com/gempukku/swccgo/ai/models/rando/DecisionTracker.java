package com.gempukku.swccgo.ai.models.rando;

import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * Tracks decisions to detect loops and problems.
 *
 * CRITICAL FIX: Detects MULTI-DECISION loops (e.g., A→B→A→B cycles).
 *
 * The key insight is that a loop can involve 2-4 decisions in sequence:
 * - Decision A: "Choose action" → Response: "Take Vader"
 * - Decision B: "Choose card" → Response: "Pass"
 * - Back to Decision A (loop!)
 *
 * Strategy:
 * 1. Track rolling sequence of (decision_key, response) pairs
 * 2. Detect when a sequence repeats (length 2, 3, or 4)
 * 3. When looping, track which responses to BLOCK/penalize
 * 4. Escalating behavior: randomize → force different → concede
 *
 * Ported from Python decision_safety.py DecisionTracker
 */
public class DecisionTracker {
    private static final Logger LOG = RandoLogger.getSafetyLogger();

    // Thresholds for escalating loop response
    public static final int LOOP_RANDOMIZE_THRESHOLD = 2;   // After 2 sequence repeats: add randomness
    public static final int LOOP_FORCE_DIFFERENT = 6;       // After 6 repeats: force different choice
    public static final int LOOP_CRITICAL = 12;             // After 12 repeats: consider conceding

    private static final int MAX_HISTORY = 100;
    private static final int MAX_SEQUENCE = 20;

    // Decision history
    private final List<DecisionEntry> history = new ArrayList<>();

    // Sequence tracking for multi-decision loops
    // Each entry: [decisionKey, response, stateHash]
    private final List<String[]> sequence = new ArrayList<>();
    private int sequenceRepeatCount = 0;
    private int detectedLoopLength = 0;

    // Blocked choices: decision_key -> set of responses to avoid
    private final Map<String, Set<String>> blockedResponses = new HashMap<>();

    // Track current game phase for reset
    private String lastPhase = "";

    // Track game state for distinguishing state-changing actions from loops
    private String lastStateHash = "";

    // Track the last CARD_ACTION_CHOICE so we can block it when cancelled
    private String lastActionChoiceKey = "";
    private String lastActionChoiceResponse = "";

    // Track current turn for clearing blocked responses
    private int lastTurn = 0;

    // Persistent blocked responses that last across phases within a turn
    private final Map<String, Set<String>> turnBlockedActions = new HashMap<>();

    /**
     * Entry for tracking decision history.
     */
    public static class DecisionEntry {
        public final String type;
        public final String text;
        public final String id;
        public final String response;
        public final String key;

        public DecisionEntry(String type, String text, String id, String response, String key) {
            this.type = type;
            this.text = text;
            this.id = id;
            this.response = response;
            this.key = key;
        }
    }

    /**
     * Create a unique key for a decision.
     */
    private String decisionKey(String decisionType, String decisionText) {
        // Use first 60 chars of text to identify the decision
        String truncated = decisionText != null && decisionText.length() > 60
            ? decisionText.substring(0, 60)
            : (decisionText != null ? decisionText : "");
        return decisionType + ":" + truncated;
    }

    /**
     * Update tracked game state. Call this before recording decisions.
     *
     * If state changes (e.g., hand size increases after drawing), the sequence
     * tracking resets because state-changing actions are not loops.
     */
    public void updateState(int handSize, int forcePile, int reserveDeck, int turn, int cardsInPlay) {
        // Check for turn change - clear turn-specific blocked actions
        if (turn != lastTurn) {
            if (!turnBlockedActions.isEmpty()) {
                LOG.info("Turn changed ({} -> {}) - clearing {} blocked action entries",
                    lastTurn, turn, turnBlockedActions.size());
            }
            turnBlockedActions.clear();
            lastTurn = turn;
        }

        String newHash = handSize + ":" + forcePile + ":" + reserveDeck + ":" + turn + ":" + cardsInPlay;

        if (!newHash.equals(lastStateHash)) {
            if (!lastStateHash.isEmpty() && sequenceRepeatCount > 0) {
                // State changed during a potential loop - it's not a real loop
                LOG.debug("State changed ({} -> {}) - resetting loop detection", lastStateHash, newHash);
                sequenceRepeatCount = 0;
                detectedLoopLength = 0;
            }
            lastStateHash = newHash;
        }
    }

    /**
     * Record a decision and response.
     */
    public void recordDecision(String decisionType, String decisionText,
                               String decisionId, String response) {
        String key = decisionKey(decisionType, decisionText);

        DecisionEntry entry = new DecisionEntry(
            decisionType,
            decisionText != null && decisionText.length() > 100
                ? decisionText.substring(0, 100) : decisionText,
            decisionId,
            response,
            key
        );
        history.add(entry);

        // Trim history
        while (history.size() > MAX_HISTORY) {
            history.remove(0);
        }

        // Track the last CARD_ACTION_CHOICE so we can block it if cancelled
        if ("CARD_ACTION_CHOICE".equals(decisionType) && response != null && !response.isEmpty()) {
            lastActionChoiceKey = key;
            lastActionChoiceResponse = response;
        }

        // CRITICAL: Only track NON-PASS responses for loop detection.
        // Passing (empty response) can't cause an infinite loop.
        if (response != null && !response.isEmpty()) {
            sequence.add(new String[]{key, response, lastStateHash});

            // Keep sequence reasonable length
            while (sequence.size() > MAX_SEQUENCE) {
                sequence.remove(0);
            }

            // Check for sequence repeat
            checkSequenceLoop();
        } else {
            // Pass response - clear any detected loop since we're not looping
            if (sequenceRepeatCount > 0) {
                LOG.debug("Pass response - not counting for loop detection");
            }
        }
    }

    /**
     * Check if we're in a multi-decision loop.
     */
    private void checkSequenceLoop() {
        // Need at least 4 entries to detect a 2-decision loop repeating
        if (sequence.size() < 4) {
            sequenceRepeatCount = 0;
            detectedLoopLength = 0;
            return;
        }

        // Check for loops of length 2, 3, and 4
        for (int loopLen : new int[]{2, 3, 4}) {
            if (sequence.size() < loopLen * 2) {
                continue;
            }

            // Get the last `loopLen` entries
            List<String[]> recent = new ArrayList<>();
            for (int i = sequence.size() - loopLen; i < sequence.size(); i++) {
                recent.add(sequence.get(i));
            }

            // Check how many times this exact sequence appears at the end
            int repeatCount = 1;
            int pos = sequence.size() - loopLen * 2;

            while (pos >= 0) {
                // Check if seq[pos:pos+loopLen] matches recent
                boolean matches = true;
                for (int i = 0; i < loopLen && matches; i++) {
                    String[] seg = sequence.get(pos + i);
                    String[] rec = recent.get(i);
                    if (!Arrays.equals(seg, rec)) {
                        matches = false;
                    }
                }

                if (matches) {
                    repeatCount++;
                    pos -= loopLen;
                } else {
                    break;
                }
            }

            // If we found repeats, record it
            if (repeatCount >= 2) {
                if (repeatCount > sequenceRepeatCount || loopLen < detectedLoopLength) {
                    sequenceRepeatCount = repeatCount;
                    detectedLoopLength = loopLen;

                    // Log the detected loop
                    if (repeatCount >= LOOP_RANDOMIZE_THRESHOLD) {
                        RandoLogger.loopDetected(
                            "{}-decision sequence repeated {}x", loopLen, repeatCount);

                        for (int i = 0; i < recent.size(); i++) {
                            String[] e = recent.get(i);
                            String k = e[0].length() > 50 ? e[0].substring(0, 50) : e[0];
                            LOG.warn("   Step {}: {} -> '{}'", i + 1, k, e[1]);
                        }

                        // Block the responses that are causing the loop
                        for (String[] e : recent) {
                            String k = e[0];
                            String r = e[1];
                            blockedResponses.computeIfAbsent(k, x -> new HashSet<>()).add(r);
                        }
                    }
                }
                return;  // Found a loop, done checking
            }
        }

        // No loop found
        if (sequenceRepeatCount > 0) {
            LOG.info("Loop broken after {} repeats", sequenceRepeatCount);
            sequenceRepeatCount = 0;
            detectedLoopLength = 0;
        }
    }

    /**
     * Check if we're in a potential infinite loop.
     *
     * @return array of [isLoop, repeatCount]
     */
    public int[] checkForLoop(String decisionType, String decisionText, int threshold) {
        boolean isLoop = sequenceRepeatCount >= threshold;
        return new int[]{isLoop ? 1 : 0, sequenceRepeatCount};
    }

    /**
     * Get responses that should be blocked/penalized for this decision.
     *
     * Called by evaluators to avoid choices that caused loops.
     * Note: Empty string (pass) is never blocked since passing can't cause loops.
     */
    public Set<String> getBlockedResponses(String decisionType, String decisionText) {
        String key = decisionKey(decisionType, decisionText);
        Set<String> blocked = new HashSet<>();

        // Combine both immediate blocked_responses and turn-persistent blocks
        if (blockedResponses.containsKey(key)) {
            blocked.addAll(blockedResponses.get(key));
        }
        if (turnBlockedActions.containsKey(key)) {
            blocked.addAll(turnBlockedActions.get(key));
        }

        // Never block empty response (pass) - passing can't cause loops
        blocked.remove("");
        blocked.remove(null);

        return blocked;
    }

    /**
     * Get the severity level of the current loop.
     *
     * @return "none", "mild", "moderate", "severe", or "critical"
     */
    public String getLoopSeverity() {
        if (sequenceRepeatCount < LOOP_RANDOMIZE_THRESHOLD) {
            return "none";
        } else if (sequenceRepeatCount < LOOP_FORCE_DIFFERENT) {
            return "mild";  // Add randomness
        } else if (sequenceRepeatCount < LOOP_CRITICAL) {
            return "severe";  // Force different choice
        } else {
            return "critical";  // Consider conceding
        }
    }

    /**
     * Check if we should force a different choice to break loop.
     */
    public boolean shouldForceDifferentChoice() {
        return sequenceRepeatCount >= LOOP_FORCE_DIFFERENT;
    }

    /**
     * Check if loop is so severe we should consider conceding.
     */
    public boolean shouldConsiderConcede() {
        return sequenceRepeatCount >= LOOP_CRITICAL;
    }

    /**
     * Called when game phase changes.
     * Resets loop tracking since phase change likely breaks loops.
     */
    public void onPhaseChange(String newPhase) {
        if (!newPhase.equals(lastPhase)) {
            lastPhase = newPhase;
            sequenceRepeatCount = 0;
            detectedLoopLength = 0;
            blockedResponses.clear();
            sequence.clear();
            LOG.debug("Loop tracker reset on phase change to: {}", newPhase);
        }
    }

    /**
     * Reset the repeat count (e.g., after successful progress).
     */
    public void resetRepeatCount(String decisionType, String decisionText) {
        String key = decisionKey(decisionType, decisionText);
        blockedResponses.remove(key);
    }

    /**
     * Get the most recent decisions.
     */
    public List<DecisionEntry> getRecentDecisions(int count) {
        int start = Math.max(0, history.size() - count);
        return new ArrayList<>(history.subList(start, history.size()));
    }

    /**
     * Block the previous CARD_ACTION_CHOICE when we cancel a target selection.
     *
     * This breaks the loop pattern:
     * 1. Select action (Force Lightning) → recorded as last_action_choice
     * 2. Cancel target selection (no valid targets)
     * 3. Back to action choice → action now blocked!
     *
     * @return true if we blocked an action, false otherwise
     */
    public boolean blockLastActionOnCancel(String decisionType, String decisionText) {
        // Only act when cancelling target selections
        if (!"CARD_SELECTION".equals(decisionType) && !"ARBITRARY_CARDS".equals(decisionType)) {
            return false;
        }

        // Check if decision text indicates a cancel option
        if (decisionText != null) {
            String textLower = decisionText.toLowerCase(Locale.ROOT);
            if (!textLower.contains("cancel") && !textLower.contains("done")) {
                return false;
            }
        }

        // Block the last action choice if we have one
        if (lastActionChoiceKey != null && !lastActionChoiceKey.isEmpty() &&
            lastActionChoiceResponse != null && !lastActionChoiceResponse.isEmpty()) {

            blockedResponses.computeIfAbsent(lastActionChoiceKey, k -> new HashSet<>())
                .add(lastActionChoiceResponse);

            turnBlockedActions.computeIfAbsent(lastActionChoiceKey, k -> new HashSet<>())
                .add(lastActionChoiceResponse);

            String truncatedKey = lastActionChoiceKey.length() > 50
                ? lastActionChoiceKey.substring(0, 50) : lastActionChoiceKey;
            LOG.warn("Blocking action '{}' for '{}' - target selection was cancelled",
                lastActionChoiceResponse, truncatedKey);

            // Clear so we don't block it again
            lastActionChoiceKey = "";
            lastActionChoiceResponse = "";
            return true;
        }

        return false;
    }

    /**
     * Clear all tracking data (e.g., at game start).
     */
    public void clear() {
        history.clear();
        sequence.clear();
        sequenceRepeatCount = 0;
        detectedLoopLength = 0;
        blockedResponses.clear();
        turnBlockedActions.clear();
        lastPhase = "";
        lastTurn = 0;
        lastStateHash = "";
        lastActionChoiceKey = "";
        lastActionChoiceResponse = "";
    }

    /**
     * Get the current loop repeat count.
     */
    public int getSequenceRepeatCount() {
        return sequenceRepeatCount;
    }
}
