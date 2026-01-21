package com.gempukku.swccgo.ai.common;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Calculates destiny draw probabilities and expectations.
 *
 * Uses GEMP's game state to analyze deck composition and estimate
 * destiny draw outcomes. This helps AI make informed decisions about
 * when to initiate battles or use destiny-manipulating cards.
 */
public class AiDestinyCalculator {

    /**
     * Result of destiny analysis.
     */
    public static class DestinyAnalysis {
        public final float expectedValue;           // Expected destiny value
        public final float probabilityHighDestiny;  // P(destiny >= 4)
        public final float probabilityVeryHigh;     // P(destiny >= 6)
        public final int cardsInReserve;            // Number of cards in reserve
        public final Map<Integer, Float> distribution; // destiny value -> probability

        public DestinyAnalysis(float expectedValue, float probabilityHighDestiny,
                              float probabilityVeryHigh, int cardsInReserve,
                              Map<Integer, Float> distribution) {
            this.expectedValue = expectedValue;
            this.probabilityHighDestiny = probabilityHighDestiny;
            this.probabilityVeryHigh = probabilityVeryHigh;
            this.cardsInReserve = cardsInReserve;
            this.distribution = distribution;
        }
    }

    /**
     * Analyze the reserve deck to estimate destiny draw outcomes.
     *
     * @param game the current game
     * @param playerId the player whose deck to analyze
     * @return DestinyAnalysis with expectations and probabilities
     */
    public static DestinyAnalysis analyzeReserveDeck(SwccgGame game, String playerId) {
        if (game == null || playerId == null) {
            return createEmptyAnalysis();
        }

        GameState gameState = game.getGameState();
        if (gameState == null) {
            return createEmptyAnalysis();
        }

        // Get cards in reserve deck
        Collection<PhysicalCard> reserveDeck = gameState.getReserveDeck(playerId);
        if (reserveDeck == null || reserveDeck.isEmpty()) {
            return createEmptyAnalysis();
        }

        int totalCards = reserveDeck.size();
        float destinySum = 0;
        int highDestinyCount = 0;    // destiny >= 4
        int veryHighCount = 0;       // destiny >= 6
        Map<Integer, Integer> destinyDistribution = new HashMap<>();

        for (PhysicalCard card : reserveDeck) {
            float destiny = AiCardHelper.getDestiny(card);
            int destinyInt = Math.round(destiny);

            destinySum += destiny;

            if (destiny >= 4) {
                highDestinyCount++;
            }
            if (destiny >= 6) {
                veryHighCount++;
            }

            destinyDistribution.merge(destinyInt, 1, Integer::sum);
        }

        // Calculate probabilities
        float expectedValue = destinySum / totalCards;
        float probHigh = (float) highDestinyCount / totalCards;
        float probVeryHigh = (float) veryHighCount / totalCards;

        // Convert distribution to probabilities
        Map<Integer, Float> distribution = new HashMap<>();
        for (Map.Entry<Integer, Integer> entry : destinyDistribution.entrySet()) {
            distribution.put(entry.getKey(), (float) entry.getValue() / totalCards);
        }

        return new DestinyAnalysis(expectedValue, probHigh, probVeryHigh, totalCards, distribution);
    }

    /**
     * Get expected destiny value for a player's next draw.
     *
     * @param game the current game
     * @param playerId the player
     * @return expected destiny value
     */
    public static float getExpectedDestiny(SwccgGame game, String playerId) {
        DestinyAnalysis analysis = analyzeReserveDeck(game, playerId);
        return analysis.expectedValue;
    }

    /**
     * Get probability that next destiny draw is >= threshold.
     *
     * @param game the current game
     * @param playerId the player
     * @param threshold minimum destiny value
     * @return probability 0.0 to 1.0
     */
    public static float getProbabilityDestinyAtLeast(SwccgGame game, String playerId, int threshold) {
        if (game == null || playerId == null) {
            return 0.0f;
        }

        GameState gameState = game.getGameState();
        if (gameState == null) {
            return 0.0f;
        }

        Collection<PhysicalCard> reserveDeck = gameState.getReserveDeck(playerId);
        if (reserveDeck == null || reserveDeck.isEmpty()) {
            return 0.0f;
        }

        int totalCards = reserveDeck.size();
        int aboveThreshold = 0;

        for (PhysicalCard card : reserveDeck) {
            float destiny = AiCardHelper.getDestiny(card);
            if (destiny >= threshold) {
                aboveThreshold++;
            }
        }

        return (float) aboveThreshold / totalCards;
    }

    /**
     * Compare expected destiny outcomes between two players.
     *
     * @param game the current game
     * @param player1 first player
     * @param player2 second player
     * @return positive if player1 has better expected destiny, negative if player2
     */
    public static float compareExpectedDestiny(SwccgGame game, String player1, String player2) {
        float expected1 = getExpectedDestiny(game, player1);
        float expected2 = getExpectedDestiny(game, player2);
        return expected1 - expected2;
    }

    /**
     * Estimate battle destiny advantage.
     *
     * For battle, we typically draw 2 destiny. This estimates the expected
     * total of 2 draws minus opponent's 2 draws.
     *
     * @param game the current game
     * @param playerId our player ID
     * @param opponentId opponent's player ID
     * @param ourDraws number of destiny draws we'll get
     * @param theirDraws number of destiny draws opponent will get
     * @return expected destiny difference (positive = advantage)
     */
    public static float estimateBattleDestinyAdvantage(SwccgGame game, String playerId,
            String opponentId, int ourDraws, int theirDraws) {
        float ourExpected = getExpectedDestiny(game, playerId);
        float theirExpected = getExpectedDestiny(game, opponentId);

        return (ourExpected * ourDraws) - (theirExpected * theirDraws);
    }

    /**
     * Check if deck has good destiny composition for battle.
     *
     * A deck is considered "battle-ready" if it has reasonable chance
     * of drawing high destiny.
     *
     * @param game the current game
     * @param playerId the player
     * @return true if deck has good battle destiny potential
     */
    public static boolean hasGoodBattleDestiny(SwccgGame game, String playerId) {
        DestinyAnalysis analysis = analyzeReserveDeck(game, playerId);

        // Consider good if either:
        // - Expected destiny >= 3
        // - At least 40% chance of drawing destiny >= 4
        return analysis.expectedValue >= 3.0f || analysis.probabilityHighDestiny >= 0.40f;
    }

    /**
     * Get the number of cards remaining in reserve deck.
     *
     * @param game the current game
     * @param playerId the player
     * @return number of cards in reserve deck
     */
    public static int getReserveDeckSize(SwccgGame game, String playerId) {
        if (game == null || playerId == null) {
            return 0;
        }
        GameState gameState = game.getGameState();
        if (gameState == null) {
            return 0;
        }
        Collection<PhysicalCard> reserveDeck = gameState.getReserveDeck(playerId);
        return reserveDeck != null ? reserveDeck.size() : 0;
    }

    private static DestinyAnalysis createEmptyAnalysis() {
        return new DestinyAnalysis(0, 0, 0, 0, new HashMap<>());
    }
}
