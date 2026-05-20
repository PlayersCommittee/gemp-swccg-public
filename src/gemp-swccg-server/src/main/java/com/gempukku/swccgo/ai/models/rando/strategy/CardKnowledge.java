package com.gempukku.swccgo.ai.models.rando.strategy;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Card-specific strategic knowledge.
 * Enhances Rando's decision-making with card-specific metadata.
 * 
 * Week 2 Improvement: Gives Rando deep knowledge about key cards
 * instead of treating all cards generically.
 */
public class CardKnowledge {
    
    private static final Map<String, CardMetadata> CARDS = new HashMap<>();
    
    static {
        // =====================================================
        // PRIORITY DAMAGE CANCELS - Use when life force critical
        // =====================================================
        addCard("109_10", "Houjix", 100, 8, true)
            .setForfeitPenalty(50)  // Never forfeit!
            .setUsageNotes("Use when LF < 8 to cancel battle damage");
        
        addCard("109_7", "Ghhhk", 100, 8, true)
            .setForfeitPenalty(50)
            .setUsageNotes("Use when LF < 8 to cancel battle damage");
        
        // =====================================================
        // KEY COMBO CARDS - Don't forfeit easily
        // =====================================================
        addCard("7_123", "Obi-Wan Kenobi", 80, 0, false)
            .setForfeitPenalty(30)
            .setCombosWith("7_124")  // Kenobi's Lightsaber
            .setUsageNotes("Best with matching lightsaber");
        
        addCard("7_124", "Kenobi's Lightsaber", 70, 0, false)
            .setForfeitPenalty(20)
            .setUsageNotes("Deploy on Obi-Wan");
        
        addCard("1_2", "Luke Skywalker", 85, 0, false)
            .setForfeitPenalty(30)
            .setUsageNotes("Main character - keep alive");
        
        addCard("1_276", "Darth Vader", 90, 0, false)
            .setForfeitPenalty(35)
            .setUsageNotes("Main character - keep alive");
        
        // =====================================================
        // HIGH-VALUE UNIQUE CHARACTERS
        // =====================================================
        addCard("1_8", "Han Solo", 75, 0, false)
            .setForfeitPenalty(25)
            .setUsageNotes("Pilot + smuggler abilities");
        
        addCard("1_18", "Princess Leia", 75, 0, false)
            .setForfeitPenalty(25)
            .setUsageNotes("High ability leader");
        
        addCard("1_290", "Grand Moff Tarkin", 75, 0, false)
            .setForfeitPenalty(25)
            .setUsageNotes("High ability leader");
        
        addCard("1_280", "Emperor Palpatine", 90, 0, false)
            .setForfeitPenalty(40)
            .setUsageNotes("Most powerful dark side character");
        
        // =====================================================
        // KEY DEFENSIVE CARDS
        // =====================================================
        addCard("8_109", "Sense", 70, 0, false)
            .setForfeitPenalty(20)
            .setUsageNotes("Reveal opponent's hand - critical intel");
        
        addCard("8_110", "Alter", 70, 0, false)
            .setForfeitPenalty(20)
            .setUsageNotes("Cancel opponent's interrupt");
        
        // =====================================================
        // IMPORTANT LOCATIONS
        // =====================================================
        addCard("1_277", "Death Star", 60, 0, false)
            .setDeployPriority(80)
            .setUsageNotes("Deploy early for force generation");
        
        addCard("1_38", "Yavin 4", 60, 0, false)
            .setDeployPriority(80)
            .setUsageNotes("Rebel base - deploy early");
    }
    
    // =====================================================
    // EFFECTS TO NOT DEPLOY ON TURN 1
    // Title-based matching (case-insensitive).
    // These are Effects that provide little or no value
    // when deployed too early and waste a deploy action.
    // Add more titles here as needed.
    // =====================================================
    private static final Set<String> DO_NOT_DEPLOY_TURN_1 = new HashSet<>(Arrays.asList(
        "no escape",
        "coarse and rough and irritating"
    ));

    /**
     * Check if a card (by title) should NOT be deployed on the given turn.
     * Currently blocks certain Effects from being deployed on turn 1.
     *
     * @param cardTitle the card's title
     * @param turnNumber the current turn number
     * @return true if the card should be blocked from deployment this turn
     */
    public static boolean shouldBlockDeployment(String cardTitle, int turnNumber) {
        if (cardTitle == null || turnNumber > 1) {
            return false;  // Only restrict on turn 1
        }
        String titleLower = cardTitle.toLowerCase(Locale.ROOT);
        for (String blocked : DO_NOT_DEPLOY_TURN_1) {
            // Use startsWith so "No Escape (V)" matches "no escape"
            if (titleLower.startsWith(blocked) || titleLower.equals(blocked)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Helper to add card metadata.
     */
    private static CardMetadata addCard(String blueprintId, String name, 
                                         int priority, int useLfThreshold, 
                                         boolean isPriorityCard) {
        CardMetadata meta = new CardMetadata(blueprintId, name, priority);
        meta.setUseLfThreshold(useLfThreshold);
        meta.setIsPriorityCard(isPriorityCard);
        CARDS.put(blueprintId, meta);
        return meta;
    }
    
    /**
     * Get metadata for a specific card.
     */
    public static CardMetadata get(String blueprintId) {
        return CARDS.get(blueprintId);
    }
    
    /**
     * Check if card is a priority card (Houjix, Ghhhk, etc).
     */
    public static boolean isPriorityCard(String blueprintId) {
        CardMetadata meta = CARDS.get(blueprintId);
        return meta != null && meta.isPriorityCard;
    }
    
    /**
     * Get forfeit penalty for this card (higher = keep alive longer).
     */
    public static int getForfeitPenalty(String blueprintId) {
        CardMetadata meta = CARDS.get(blueprintId);
        return meta != null ? meta.getForfeitPenalty() : 0;
    }
    
    /**
     * Get deploy priority for this card.
     */
    public static int getDeployPriority(String blueprintId) {
        CardMetadata meta = CARDS.get(blueprintId);
        return meta != null ? meta.getDeployPriority() : 0;
    }
    
    /**
     * Check if card should be used based on current life force.
     */
    public static boolean shouldUseNow(String blueprintId, int currentLifeForce) {
        CardMetadata meta = CARDS.get(blueprintId);
        if (meta == null || !meta.isPriorityCard) return false;
        return currentLifeForce <= meta.getUseLfThreshold();
    }
    
    /**
     * Metadata for a single card.
     */
    public static class CardMetadata {
        private final String blueprintId;
        private final String name;
        private final int priority;
        private int useLfThreshold;
        private boolean isPriorityCard;
        private int forfeitPenalty = 0;
        private int deployPriority = 0;
        private String combosWith = null;
        private String usageNotes = null;
        
        public CardMetadata(String blueprintId, String name, int priority) {
            this.blueprintId = blueprintId;
            this.name = name;
            this.priority = priority;
        }
        
        public CardMetadata setUseLfThreshold(int threshold) {
            this.useLfThreshold = threshold;
            return this;
        }
        
        public CardMetadata setIsPriorityCard(boolean value) {
            this.isPriorityCard = value;
            return this;
        }
        
        public CardMetadata setForfeitPenalty(int penalty) {
            this.forfeitPenalty = penalty;
            return this;
        }
        
        public CardMetadata setDeployPriority(int priority) {
            this.deployPriority = priority;
            return this;
        }
        
        public CardMetadata setCombosWith(String cardId) {
            this.combosWith = cardId;
            return this;
        }
        
        public CardMetadata setUsageNotes(String notes) {
            this.usageNotes = notes;
            return this;
        }
        
        // Getters
        public String getBlueprintId() { return blueprintId; }
        public String getName() { return name; }
        public int getPriority() { return priority; }
        public int getUseLfThreshold() { return useLfThreshold; }
        public boolean isPriorityCard() { return isPriorityCard; }
        public int getForfeitPenalty() { return forfeitPenalty; }
        public int getDeployPriority() { return deployPriority; }
        public String getCombosWith() { return combosWith; }
        public String getUsageNotes() { return usageNotes; }
    }
}
