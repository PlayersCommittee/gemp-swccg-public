package com.gempukku.swccgo.bot;

import java.util.Random;

/**
 * Battle damage commentary generator.
 *
 * Provides flavor text messages for battle damage events, ported from
 * the Python bot's astrogator_brain.py. Messages are tiered by damage amount:
 * - High (>20): Dramatic, impressed commentary
 * - Medium (10-20): Moderate concern
 * - Low (1-9): Dismissive, sarcastic commentary
 *
 * Also handles record-breaking damage announcements.
 */
public class BattleCommentary {

    private static final Random RANDOM = new Random();

    // High damage messages (>20)
    private static final String[] HIGH_DAMAGE_MESSAGES = {
        "Now THIS is podracing!",
        "That's no moon... that's YOUR damage total!",
        "Great shot, kid. That was one in a million!",
        "Witness the firepower of this fully armed deck!",
        "I'm not even mad. That was efficient.",
        "I felt a great disturbance in my cards.",
        "That hurt. In a purely mathematical sense.",
        "You just deleted a large portion of my future. Impressive.",
        "If that was your plan, it worked. Unfortunately.",
        "Note to self: do not stand in front of that again."
    };

    // Medium damage messages (10-20)
    private static final String[] MEDIUM_DAMAGE_MESSAGES = {
        "Ow. Moderate, but emotionally rude.",
        "That damage was... adequate. Like your excuses will be later.",
        "Not fatal. Yet. I hate that word.",
        "I've seen worse. I've also seen better. Please do worse.",
        "You may fire when ready. Apparently you did.",
        "I dislike this route. It has teeth.",
        "That's a meaningful number of cards. I'm counting them. Bitterly.",
        "Acceptable hit. Keep that up and I'll have to start respecting you.",
        "Your battle destiny is behaving suspiciously competent.",
        "Attrition is a cruel hobby."
    };

    // Low damage messages (1-9)
    private static final String[] LOW_DAMAGE_MESSAGES = {
        "That was adorable.",
        "Light scratch. My pride is more damaged than my life force.",
        "Is that all? I've had worse from a malfunctioning mouse droid.",
        "Minimal impact. Maximum confidence. Classic organic.",
        "Those blast points... too accurate for Sand People.",
        "Only Imperial Stormtroopers are so imprecise.",
        "Into the garbage chute, flyboy. Preferably your strategy.",
        "Boring conversation anyway.",
        "You may fire when ready. Or not. Apparently not.",
        "If you're aiming for my feelings, you're doing great. For the cards, not so much.",
        "I barely noticed. Which is insulting in its own way."
    };

    /**
     * Get damage commentary for a battle result.
     *
     * @param damage the amount of battle damage dealt
     * @param isNewGlobalRecord true if this sets a new global damage record
     * @param isNewPersonalRecord true if this sets a new personal best
     * @param previousRecordHolder name of the previous global record holder (can be null)
     * @param previousPersonalBest the player's previous personal best damage (0 if none)
     * @param currentPlayer the current player's name (for record comparisons)
     * @return a commentary message, or null if damage <= 0
     */
    public static String getDamageCommentary(int damage, boolean isNewGlobalRecord,
                                             boolean isNewPersonalRecord,
                                             String previousRecordHolder,
                                             int previousPersonalBest,
                                             String currentPlayer) {
        if (damage <= 0) {
            return null;
        }

        // New global record takes priority
        if (isNewGlobalRecord) {
            if (previousRecordHolder != null && !previousRecordHolder.equals(currentPlayer)) {
                return "New damage record: " + damage + "! " + previousRecordHolder + " dethroned!";
            } else {
                return "New damage record: " + damage + "! Impressive!";
            }
        }

        // New personal record
        if (isNewPersonalRecord) {
            if (previousPersonalBest > 0) {
                return "Personal best: " + damage + "! (was " + previousPersonalBest + ")";
            } else {
                return "Personal best: " + damage + "!";
            }
        }

        // Regular damage commentary with tier-based formatting
        String prefix;
        String[] messagePool;

        if (damage > 20) {
            prefix = "Battle damage: " + damage + "!";
            messagePool = HIGH_DAMAGE_MESSAGES;
        } else if (damage > 10) {
            prefix = "Battle damage: " + damage + ".";
            messagePool = MEDIUM_DAMAGE_MESSAGES;
        } else {
            prefix = "Battle damage: " + damage + "...";
            messagePool = LOW_DAMAGE_MESSAGES;
        }

        String flavor = messagePool[RANDOM.nextInt(messagePool.length)];
        return prefix + " " + flavor;
    }

    /**
     * Simplified version without record tracking.
     *
     * @param damage the amount of battle damage dealt
     * @return a commentary message, or null if damage <= 0
     */
    public static String getDamageCommentary(int damage) {
        return getDamageCommentary(damage, false, false, null, 0, null);
    }
}
