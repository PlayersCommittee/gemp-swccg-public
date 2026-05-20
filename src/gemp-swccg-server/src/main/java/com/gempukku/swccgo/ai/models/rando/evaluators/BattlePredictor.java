package com.gempukku.swccgo.ai.models.rando.evaluators;

import java.util.Random;

/**
 * Predicts battle outcomes using Monte Carlo simulation.
 * 
 * Week 4 Improvement: Instead of simple power comparison,
 * simulates battles 50 times to predict win probability.
 * 
 * Accounts for:
 * - Destiny draws (random 0-6 per draw)
 * - Power totals
 * - Multiple simulation runs for accuracy
 */
public class BattlePredictor {
    
    private static final Random random = new Random();
    private static final int SIMULATIONS = 50;  // Run 50 battle simulations
    private static final int DESTINY_MAX = 6;   // Destiny range: 0-6
    
    /**
     * Predict battle outcome.
     * 
     * @param myPower Our total power at location
     * @param myDestinyDraws Number of destiny draws we'll get
     * @param oppPower Opponent's total power
     * @param oppDestinyDraws Opponent's destiny draws
     * @return Battle outcome with win probability and expected damage
     */
    public static BattleOutcome predictBattle(
        int myPower, int myDestinyDraws,
        int oppPower, int oppDestinyDraws) {
        
        int wins = 0;
        int totalDamageDealt = 0;
        int totalDamageTaken = 0;
        
        // Run simulations
        for (int i = 0; i < SIMULATIONS; i++) {
            int myTotal = myPower + simulateDestiny(myDestinyDraws);
            int oppTotal = oppPower + simulateDestiny(oppDestinyDraws);
            
            if (myTotal > oppTotal) {
                wins++;
                totalDamageDealt += (myTotal - oppTotal);
            } else if (oppTotal > myTotal) {
                totalDamageTaken += (oppTotal - myTotal);
            }
            // Ties = no damage either way
        }
        
        float winRate = (float) wins / SIMULATIONS;
        float avgDamageDealt = (float) totalDamageDealt / SIMULATIONS;
        float avgDamageTaken = (float) totalDamageTaken / SIMULATIONS;
        
        return new BattleOutcome(winRate, avgDamageDealt, avgDamageTaken);
    }
    
    /**
     * V24.7: Predict battle using KNOWN opponent destiny average from deck peek.
     * Uses the real average instead of random 0-6 for opponent draws.
     * Our draws still use random simulation (we don't know our own draw order).
     */
    public static BattleOutcome predictBattleWithIntel(
        int myPower, int myDestinyDraws,
        int oppPower, int oppDestinyDraws,
        float knownOppDestinyAvg) {

        int wins = 0;
        int totalDamageDealt = 0;
        int totalDamageTaken = 0;

        for (int i = 0; i < SIMULATIONS; i++) {
            int myTotal = myPower + simulateDestiny(myDestinyDraws);
            // Use known average for opponent instead of random
            int oppTotal = oppPower + Math.round(knownOppDestinyAvg * oppDestinyDraws);

            if (myTotal > oppTotal) {
                wins++;
                totalDamageDealt += (myTotal - oppTotal);
            } else if (oppTotal > myTotal) {
                totalDamageTaken += (oppTotal - myTotal);
            }
        }

        float winRate = (float) wins / SIMULATIONS;
        float avgDamageDealt = (float) totalDamageDealt / SIMULATIONS;
        float avgDamageTaken = (float) totalDamageTaken / SIMULATIONS;

        return new BattleOutcome(winRate, avgDamageDealt, avgDamageTaken);
    }

    /**
     * V24.7: Predict battle using BOTH sides' known destiny averages.
     * Rando knows his own reserve deck contents (DeckOracle), and may know
     * opponent's average from verification peeks (OpponentDeckTracker).
     * Uses deterministic calculation when averages are known — no randomness needed.
     */
    public static BattleOutcome predictBattleFullIntel(
        int myPower, int myDestinyDraws, float myDestinyAvg,
        int oppPower, int oppDestinyDraws, float oppDestinyAvg) {

        // With both averages known, use deterministic prediction
        int myTotal = myPower + Math.round(myDestinyAvg * myDestinyDraws);
        int oppTotal = oppPower + Math.round(oppDestinyAvg * oppDestinyDraws);

        float winRate;
        float damageDealt;
        float damageTaken;

        if (myTotal > oppTotal) {
            winRate = 1.0f;
            damageDealt = myTotal - oppTotal;
            damageTaken = 0;
        } else if (oppTotal > myTotal) {
            winRate = 0.0f;
            damageDealt = 0;
            damageTaken = oppTotal - myTotal;
        } else {
            winRate = 0.5f;
            damageDealt = 0;
            damageTaken = 0;
        }

        return new BattleOutcome(winRate, damageDealt, damageTaken);
    }

    /**
     * V24.7: Predict battle using all available intel.
     * - Uses DeckOracle's average destiny for Rando's draws
     * - Uses OpponentDeckTracker's average for opponent draws (if available)
     * - Falls back to random simulation only where intel is missing
     */
    public static BattleOutcome predictBattle(
        int myPower, int myDestinyDraws,
        int oppPower, int oppDestinyDraws,
        com.gempukku.swccgo.ai.models.rando.strategy.DeckOracle deckOracle,
        com.gempukku.swccgo.ai.models.rando.strategy.OpponentDeckTracker tracker) {

        float myAvg = -1;
        float oppAvg = -1;

        // Get Rando's own destiny average from DeckOracle
        if (deckOracle != null && deckOracle.isAnalyzed()) {
            double avg = deckOracle.getAverageDestinyInReserve();
            if (avg > 0) myAvg = (float) avg;
        }

        // Get opponent's destiny average from peek intel
        if (tracker != null && tracker.hasIntel()) {
            oppAvg = tracker.getOpponentDestinyAverage();
        }

        // Use full intel if both known
        if (myAvg > 0 && oppAvg > 0) {
            return predictBattleFullIntel(myPower, myDestinyDraws, myAvg,
                oppPower, oppDestinyDraws, oppAvg);
        }
        // Use opponent intel only
        if (oppAvg > 0) {
            return predictBattleWithIntel(myPower, myDestinyDraws,
                oppPower, oppDestinyDraws, oppAvg);
        }
        // No intel — fall back to random simulation
        return predictBattle(myPower, myDestinyDraws, oppPower, oppDestinyDraws);
    }

    /**
     * V24.7: Predict battle using OpponentDeckTracker intel if available.
     * Falls back to random simulation if no intel gathered yet.
     */
    public static BattleOutcome predictBattle(
        int myPower, int myDestinyDraws,
        int oppPower, int oppDestinyDraws,
        com.gempukku.swccgo.ai.models.rando.strategy.OpponentDeckTracker tracker) {

        if (tracker != null && tracker.hasIntel()) {
            return predictBattleWithIntel(myPower, myDestinyDraws,
                oppPower, oppDestinyDraws, tracker.getOpponentDestinyAverage());
        }
        return predictBattle(myPower, myDestinyDraws, oppPower, oppDestinyDraws);
    }

    /**
     * Simulate destiny draws for one side.
     * Each draw is a random number 0-6.
     */
    private static int simulateDestiny(int draws) {
        int total = 0;
        for (int i = 0; i < draws; i++) {
            total += random.nextInt(DESTINY_MAX + 1);  // 0-6 inclusive
        }
        return total;
    }

    /**
     * Quick check: Should we initiate this battle?
     */
    public static boolean shouldInitiateBattle(
        int myPower, int myDestinyDraws,
        int oppPower, int oppDestinyDraws,
        float conservativeThreshold) {

        BattleOutcome outcome = predictBattle(myPower, myDestinyDraws, oppPower, oppDestinyDraws);
        return outcome.winProbability >= conservativeThreshold &&
               outcome.expectedDamageDealt >= outcome.expectedDamageTaken;
    }

    /**
     * V24.7: Should we initiate battle — using opponent deck intel if available.
     */
    public static boolean shouldInitiateBattle(
        int myPower, int myDestinyDraws,
        int oppPower, int oppDestinyDraws,
        float conservativeThreshold,
        com.gempukku.swccgo.ai.models.rando.strategy.OpponentDeckTracker tracker) {

        BattleOutcome outcome = predictBattle(myPower, myDestinyDraws, oppPower, oppDestinyDraws, tracker);
        return outcome.winProbability >= conservativeThreshold &&
               outcome.expectedDamageDealt >= outcome.expectedDamageTaken;
    }

    /**
     * V24.7: Should we initiate battle — using FULL intel (both sides' destiny averages).
     */
    public static boolean shouldInitiateBattle(
        int myPower, int myDestinyDraws,
        int oppPower, int oppDestinyDraws,
        float conservativeThreshold,
        com.gempukku.swccgo.ai.models.rando.strategy.DeckOracle deckOracle,
        com.gempukku.swccgo.ai.models.rando.strategy.OpponentDeckTracker tracker) {

        BattleOutcome outcome = predictBattle(myPower, myDestinyDraws, oppPower, oppDestinyDraws, deckOracle, tracker);
        return outcome.winProbability >= conservativeThreshold &&
               outcome.expectedDamageDealt >= outcome.expectedDamageTaken;
    }

    /**
     * Quick favorable check with default 60% threshold.
     */
    public static boolean shouldInitiateBattle(
        int myPower, int myDestinyDraws,
        int oppPower, int oppDestinyDraws) {
        return shouldInitiateBattle(myPower, myDestinyDraws, oppPower, oppDestinyDraws, 0.6f);
    }
    
    /**
     * Outcome of battle prediction.
     */
    public static class BattleOutcome {
        /** Probability of winning (0.0 to 1.0) */
        public final float winProbability;
        
        /** Average damage we'll deal if we win */
        public final float expectedDamageDealt;
        
        /** Average damage we'll take if we lose */
        public final float expectedDamageTaken;
        
        public BattleOutcome(float winProb, float damageDealt, float damageTaken) {
            this.winProbability = winProb;
            this.expectedDamageDealt = damageDealt;
            this.expectedDamageTaken = damageTaken;
        }
        
        /**
         * Is this battle favorable?
         * Default threshold: 60%+ win rate.
         */
        public boolean isFavorable() {
            return winProbability >= 0.6f;
        }
        
        /**
         * Is this battle favorable with custom threshold?
         */
        public boolean isFavorable(float threshold) {
            return winProbability >= threshold;
        }
        
        /**
         * Is this a risky battle?
         * Risky = win probability between 40-60% (coin flip)
         */
        public boolean isRisky() {
            return winProbability >= 0.4f && winProbability <= 0.6f;
        }
        
        /**
         * Is this battle dangerous?
         * Dangerous = less than 40% win probability
         */
        public boolean isDangerous() {
            return winProbability < 0.4f;
        }
        
        /**
         * Get expected net damage (positive = we deal more, negative = we take more)
         */
        public float getExpectedNetDamage() {
            return expectedDamageDealt - expectedDamageTaken;
        }
        
        @Override
        public String toString() {
            return String.format("BattleOutcome[winRate=%.1f%%, dmgDealt=%.1f, dmgTaken=%.1f]",
                winProbability * 100, expectedDamageDealt, expectedDamageTaken);
        }
    }
}
