package com.gempukku.swccgo.ai.models.rando.strategy;

// No external logging dependency - uses System.out for debug output

/**
 * V24.7: Tracks opponent's deck intelligence gathered from verification peeks.
 *
 * When the opponent searches their reserve deck and fails, Rando gets to verify
 * (look through) the deck. This tracker scans all visible cards, records their
 * destiny values, and calculates the average. BattlePredictor can then use the
 * real average instead of random 0-6 guesses for opponent destiny draws.
 */
public class OpponentDeckTracker {

    // Tracked destiny statistics
    private float opponentDestinyAverage = 3.0f;  // Default: midpoint of 0-6
    private int totalDestinySum = 0;
    private int totalDestinyCards = 0;
    private int peekCount = 0;  // How many times we've peeked
    private boolean hasIntel = false;  // Whether we have any real data

    /**
     * Record destiny values from a set of cards seen during deck verification.
     *
     * @param destinyValues array of destiny values from the peeked cards
     * @param cardCount total number of cards seen
     */
    public void recordPeek(float[] destinyValues, int cardCount) {
        if (destinyValues == null || destinyValues.length == 0) return;

        // Reset and recalculate from this peek (most recent peek is most accurate)
        totalDestinySum = 0;
        totalDestinyCards = 0;

        for (float d : destinyValues) {
            if (d >= 0) {  // Valid destiny value
                totalDestinySum += d;
                totalDestinyCards++;
            }
        }

        if (totalDestinyCards > 0) {
            opponentDestinyAverage = (float) totalDestinySum / totalDestinyCards;
            hasIntel = true;
            peekCount++;
            System.out.println("V24.7 OPPONENT INTEL: Peeked at " + cardCount + " cards, " +
                    totalDestinyCards + " with destiny values. Average destiny: " +
                    opponentDestinyAverage + " (peek #" + peekCount + ")");
        } else {
            System.out.println("V24.7 OPPONENT INTEL: Peeked at " + cardCount + " cards but none had destiny values");
        }
    }

    /**
     * Get the opponent's average destiny value.
     * Returns 3.0 (midpoint) if no intel gathered yet.
     */
    public float getOpponentDestinyAverage() {
        return opponentDestinyAverage;
    }

    /**
     * Whether we have real intel from peeking (vs the default estimate).
     */
    public boolean hasIntel() {
        return hasIntel;
    }

    /**
     * How many times we've peeked at the opponent's deck.
     */
    public int getPeekCount() {
        return peekCount;
    }

    /**
     * Total number of cards with destiny values we've seen.
     */
    public int getTotalDestinyCards() {
        return totalDestinyCards;
    }

    /**
     * Reset all intel (for new game).
     */
    public void reset() {
        opponentDestinyAverage = 3.0f;
        totalDestinySum = 0;
        totalDestinyCards = 0;
        peekCount = 0;
        hasIntel = false;
        System.out.println("V24.7 OPPONENT INTEL: Reset for new game");
    }
}
