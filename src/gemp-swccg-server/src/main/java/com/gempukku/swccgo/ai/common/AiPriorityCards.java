package com.gempukku.swccgo.ai.common;

import com.gempukku.swccgo.common.Side;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Defines high-priority cards that AI should:
 * 1. Protect from being lost/discarded
 * 2. Use strategically (not randomly)
 *
 * Based on analysis of 67 production bot decks - these cards appear in 15%+ of decks
 * and have significant strategic value.
 *
 * Categories:
 * - DEFENSIVE: Barrier cards, damage cancellation (Houjix/Ghhhk)
 * - DESTINY: Destiny manipulation (Jedi Levitation, Sith Fury)
 * - PROTECTION: Character protection (Blaster Deflection, Odin Nesloor)
 * - UTILITY: Cancel/retrieve cards
 */
public class AiPriorityCards {

    // =========================================================================
    // Card Categories
    // =========================================================================

    public enum CardCategory {
        DEFENSIVE,       // Barrier cards - prevent opponent actions
        DAMAGE_CANCEL,   // Houjix/Ghhhk - cancel battle damage
        DESTINY,         // Destiny manipulation
        DESTINY_BONUS,   // +X to destiny (battle, weapon)
        PROTECTION,      // Character protection
        UTILITY,         // General utility/cancel
        RETRIEVAL,       // Card retrieval from piles
        STARTING         // Starting effects (usually shouldn't lose)
    }

    // =========================================================================
    // Priority Card Definition
    // =========================================================================

    public static class PriorityCard {
        public final String blueprintId;
        public final String title;
        public final CardCategory category;
        public final Side side;
        public final int protectionScore;  // 0-100, higher = more protected
        public final String usageNotes;

        public PriorityCard(String blueprintId, String title, CardCategory category,
                           Side side, int protectionScore, String usageNotes) {
            this.blueprintId = blueprintId;
            this.title = title;
            this.category = category;
            this.side = side;
            this.protectionScore = protectionScore;
            this.usageNotes = usageNotes;
        }
    }

    // =========================================================================
    // Priority Card Registry
    // =========================================================================

    private static final Map<String, PriorityCard> PRIORITY_CARDS = new HashMap<>();

    static {
        // ----- BARRIER CARDS (34% of decks) -----
        registerInterrupt("1_249", "Imperial Barrier", CardCategory.DEFENSIVE, Side.DARK, 80,
            "Use when opponent deploys to contested location");
        registerInterrupt("1_105", "Rebel Barrier", CardCategory.DEFENSIVE, Side.LIGHT, 80,
            "Use when opponent deploys to contested location");

        // ----- BATTLE DESTINY MODIFIERS (31% / 18% of decks) -----
        registerInterrupt("9_51", "Heading For The Medical Frigate", CardCategory.DESTINY_BONUS, Side.LIGHT, 65,
            "USED: +1 to battle destiny just drawn - almost always use");
        registerInterrupt("9_139", "Prepared Defenses", CardCategory.DESTINY_BONUS, Side.DARK, 65,
            "USED: +1 to battle destiny just drawn - almost always use");

        // ----- DESTINY MANIPULATION (34% / 22% of decks) -----
        registerInterrupt("200_54", "Jedi Levitation (V)", CardCategory.DESTINY, Side.LIGHT, 90,
            "Use to take good character destiny into hand or redraw bad destiny");
        registerInterrupt("200_123", "Sith Fury (V)", CardCategory.DESTINY, Side.DARK, 90,
            "Use to take good character destiny into hand or redraw bad destiny");

        // ----- BATTLE DAMAGE CANCELLATION (28% / 24% of decks) -----
        registerInterrupt("2_50", "Houjix", CardCategory.DAMAGE_CANCEL, Side.LIGHT, 100,
            "CRITICAL: Save for when losing battle with no forfeit options");
        registerInterrupt("2_132", "Ghhhk", CardCategory.DAMAGE_CANCEL, Side.DARK, 100,
            "CRITICAL: Save for when losing battle with no forfeit options");

        // ----- CHARACTER PROTECTION (25% of decks) -----
        registerInterrupt("6_61", "Blaster Deflection", CardCategory.PROTECTION, Side.LIGHT, 70,
            "Use when opponent targets ability > 4 character with weapon");
        registerInterrupt("209_21", "Odin Nesloor & First Aid", CardCategory.PROTECTION, Side.LIGHT, 75,
            "Use when valuable character about to be hit");

        // ----- WEAPON ENHANCEMENT (30% of decks) -----
        registerInterrupt("10_23", "Sorry About The Mess & Blaster Proficiency", CardCategory.UTILITY, Side.LIGHT, 60,
            "Use when firing blaster to boost weapon destiny");

        // ----- COMMAND/RETRIEVAL (19% / 16% of decks) -----
        registerInterrupt("9_137", "Imperial Command", CardCategory.RETRIEVAL, Side.DARK, 65,
            "Use to retrieve admiral/general or add battle destiny");
        registerInterrupt("203_17", "Rebel Leadership (V)", CardCategory.RETRIEVAL, Side.LIGHT, 65,
            "Use to retrieve admiral/general or add battle destiny");

        // ----- UTILITY/CANCEL (24-25% of decks) -----
        registerInterrupt("210_24", "Quite A Mercenary (V)", CardCategory.UTILITY, Side.LIGHT, 55,
            "Use to cancel opponent effects or retrieve smuggler");
        registerInterrupt("204_18", "Escape Pod & We're Doomed", CardCategory.UTILITY, Side.LIGHT, 55,
            "Use to prevent Force loss from occupations");
        registerInterrupt("12_152", "Masterful Move & Endor Occupation", CardCategory.UTILITY, Side.DARK, 55,
            "Use to cancel opponent celebrations");
        registerInterrupt("201_13", "Hear Me Baby, Hold Together (V)", CardCategory.UTILITY, Side.LIGHT, 60,
            "Use to play Defensive Shield or cancel opponent cards");

        // ----- COMBO INTERRUPTS (16% of decks) -----
        registerInterrupt("11_29", "A Jedi's Resilience", CardCategory.PROTECTION, Side.LIGHT, 70,
            "Use when about to lose a duel");
        registerInterrupt("209_48", "Lana Dobreed & Sacrifice", CardCategory.UTILITY, Side.DARK, 55,
            "Utility interrupt");
        registerInterrupt("10_39", "Ghhhk & Those Rebels Won't Escape Us", CardCategory.DAMAGE_CANCEL, Side.DARK, 90,
            "Combo version of Ghhhk - save for critical moments");

        // ----- BATTLE DESTINY PROTECTION -----
        registerInterrupt("218_7", "A Dark Time For The Rebellion & Tarkin's Orders", CardCategory.DESTINY, Side.DARK, 75,
            "ONLY use when planning to initiate battle this turn - protects battle destiny draws");

        // ----- SENSE/CONTROL/ALTER -----
        registerInterrupt("1_267", "Sense", CardCategory.UTILITY, Side.DARK, 85,
            "Use to cancel opponent's Used or Lost Interrupts");
        registerInterrupt("1_108", "Sense", CardCategory.UTILITY, Side.LIGHT, 85,
            "Use to cancel opponent's Used or Lost Interrupts");
        registerInterrupt("4_139", "Control", CardCategory.UTILITY, Side.DARK, 80,
            "Cancel Sense/Alter, effects, or force drains");
        registerInterrupt("4_62", "Control", CardCategory.UTILITY, Side.LIGHT, 80,
            "Cancel Sense/Alter, effects, or force drains");
        registerInterrupt("1_217", "Alter", CardCategory.UTILITY, Side.DARK, 70,
            "Cancel opponent's Utinni Effects or Force drain modifiers");
        registerInterrupt("1_69", "Alter", CardCategory.UTILITY, Side.LIGHT, 70,
            "Cancel opponent's Utinni Effects or Force drain modifiers");

        // ----- STARTING EFFECTS (48% / 43% of decks) -----
        registerEffect("200_35", "Anger, Fear, Aggression (V)", CardCategory.STARTING, Side.LIGHT, 100,
            "Starting effect - deploy Defensive Shields");
        registerEffect("200_110", "Knowledge And Defense (V)", CardCategory.STARTING, Side.DARK, 100,
            "Starting effect - deploy Defensive Shields");

        // ----- FORCE GENERATION (36% of decks) -----
        registerEffect("200_47", "Wokling (V)", CardCategory.UTILITY, Side.LIGHT, 75,
            "Personal Force generation - keep deployed");
    }

    private static void registerInterrupt(String blueprintId, String title, CardCategory category,
                                         Side side, int protectionScore, String usageNotes) {
        PRIORITY_CARDS.put(blueprintId, new PriorityCard(blueprintId, title, category, side, protectionScore, usageNotes));
    }

    private static void registerEffect(String blueprintId, String title, CardCategory category,
                                       Side side, int protectionScore, String usageNotes) {
        PRIORITY_CARDS.put(blueprintId, new PriorityCard(blueprintId, title, category, side, protectionScore, usageNotes));
    }

    // =========================================================================
    // Lookup Methods
    // =========================================================================

    /**
     * Check if a card is in the priority list by blueprint ID.
     */
    public static boolean isPriorityCard(String blueprintId) {
        return blueprintId != null && PRIORITY_CARDS.containsKey(blueprintId);
    }

    /**
     * Get the priority card info by blueprint ID.
     * @return PriorityCard or null if not a priority card
     */
    public static PriorityCard getPriorityCard(String blueprintId) {
        return blueprintId != null ? PRIORITY_CARDS.get(blueprintId) : null;
    }

    /**
     * Get protection score for a card (how much to penalize losing it).
     * @return 0 for non-priority cards, 50-100 for priority cards
     */
    public static int getProtectionScore(String blueprintId) {
        PriorityCard card = getPriorityCard(blueprintId);
        return card != null ? card.protectionScore : 0;
    }

    // =========================================================================
    // Title-Based Lookups (fallback when blueprint ID not available)
    // =========================================================================

    /**
     * Check if a card is high-priority based on its title.
     * Used when we don't have the blueprint ID but have the title.
     */
    public static boolean isPriorityCardByTitle(String cardTitle) {
        if (cardTitle == null || cardTitle.isEmpty()) {
            return false;
        }
        String title = cardTitle.toLowerCase(Locale.ROOT);

        // Damage cancel cards
        if (title.contains("houjix") || title.contains("ghhhk")) {
            return true;
        }
        // Sense/Alter
        if (title.equals("sense") || title.equals("alter") || title.equals("control")) {
            return true;
        }
        // Barrier cards
        if (title.contains("barrier") && (title.contains("imperial") || title.contains("rebel"))) {
            return true;
        }
        // Destiny manipulation
        if (title.contains("jedi levitation") || title.contains("sith fury")) {
            return true;
        }
        // Battle destiny modifiers
        if (title.contains("heading for the medical frigate") || title.contains("prepared defenses")) {
            return true;
        }
        // Character protection
        if (title.contains("blaster deflection") || title.contains("odin nesloor")) {
            return true;
        }
        // Weapon enhancement
        if (title.contains("sorry about the mess")) {
            return true;
        }
        // Command cards
        if (title.contains("imperial command") || title.contains("rebel leadership")) {
            return true;
        }
        return false;
    }

    /**
     * Get protection score based on card title (fallback when no blueprint).
     */
    public static int getProtectionScoreByTitle(String cardTitle) {
        if (cardTitle == null || cardTitle.isEmpty()) {
            return 0;
        }
        String title = cardTitle.toLowerCase(Locale.ROOT);

        // Critical cards (highest priority - survival)
        if (title.contains("houjix") || title.contains("ghhhk")) {
            return 100;
        }
        // High priority - key interrupts
        if (title.equals("sense") || title.equals("alter")) {
            return 85;
        }
        if (title.contains("jedi levitation") || title.contains("sith fury")) {
            return 90;
        }
        if (title.contains("barrier")) {
            return 80;
        }
        if (title.equals("control")) {
            return 80;
        }
        // Medium-high priority - common useful interrupts
        if (title.contains("blaster deflection")) {
            return 70;
        }
        if (title.contains("odin nesloor")) {
            return 75;
        }
        if (title.contains("heading for the medical frigate") || title.contains("prepared defenses")) {
            return 65;
        }
        if (title.contains("imperial command") || title.contains("rebel leadership")) {
            return 65;
        }
        if (title.contains("sorry about the mess")) {
            return 60;
        }
        return 0;
    }

    // =========================================================================
    // Sense Target Evaluation - Cards worth canceling with Sense/Control
    // =========================================================================

    private static final Map<String, Integer> SENSE_TARGET_PATTERNS = new HashMap<>();

    static {
        // Critical - 100 protection score
        SENSE_TARGET_PATTERNS.put("houjix", 100);
        SENSE_TARGET_PATTERNS.put("ghhhk", 100);

        // Very High - 90 protection score
        SENSE_TARGET_PATTERNS.put("jedi levitation", 90);
        SENSE_TARGET_PATTERNS.put("sith fury", 90);

        // High - 80-85 protection score
        SENSE_TARGET_PATTERNS.put("sense", 85);
        SENSE_TARGET_PATTERNS.put("barrier", 80);

        // Medium-High - 70 protection score
        SENSE_TARGET_PATTERNS.put("blaster deflection", 70);
        SENSE_TARGET_PATTERNS.put("odin nesloor", 70);
        SENSE_TARGET_PATTERNS.put("jedi's resilience", 70);

        // Medium - 60-65 protection score
        SENSE_TARGET_PATTERNS.put("escape pod", 65);
        SENSE_TARGET_PATTERNS.put("medical frigate", 65);
        SENSE_TARGET_PATTERNS.put("prepared defenses", 65);
        SENSE_TARGET_PATTERNS.put("rebel leadership", 65);
        SENSE_TARGET_PATTERNS.put("imperial command", 65);

        // Also valuable but not in our priority list
        SENSE_TARGET_PATTERNS.put("nabrun leids", 60);
        SENSE_TARGET_PATTERNS.put("elis helrot", 60);
        SENSE_TARGET_PATTERNS.put("hyper escape", 60);
        SENSE_TARGET_PATTERNS.put("alter", 55);
        SENSE_TARGET_PATTERNS.put("control", 55);
    }

    /**
     * Result of checking if text contains a Sense target.
     */
    public static class SenseTargetResult {
        public final boolean isHighValue;
        public final int score;
        public final String matchedPattern;

        public SenseTargetResult(boolean isHighValue, int score, String matchedPattern) {
            this.isHighValue = isHighValue;
            this.score = score;
            this.matchedPattern = matchedPattern;
        }
    }

    /**
     * Check if text contains a high-value Sense target.
     *
     * Used when deciding whether to play Sense on an opponent's interrupt.
     * Parses action text like "Cancel [card name]" to identify valuable targets.
     *
     * @param text Action text or decision text to check
     * @return SenseTargetResult with (isHighValue, score, matchedPattern)
     */
    public static SenseTargetResult getSenseTargetValue(String text) {
        if (text == null || text.isEmpty()) {
            return new SenseTargetResult(false, 0, "");
        }
        String textLower = text.toLowerCase(Locale.ROOT);

        int bestScore = 0;
        String bestPattern = "";

        for (Map.Entry<String, Integer> entry : SENSE_TARGET_PATTERNS.entrySet()) {
            if (textLower.contains(entry.getKey())) {
                if (entry.getValue() > bestScore) {
                    bestScore = entry.getValue();
                    bestPattern = entry.getKey();
                }
            }
        }

        return new SenseTargetResult(bestScore > 0, bestScore, bestPattern);
    }

}
