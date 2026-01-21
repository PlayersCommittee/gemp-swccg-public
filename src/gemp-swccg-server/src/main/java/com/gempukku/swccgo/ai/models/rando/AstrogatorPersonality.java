package com.gempukku.swccgo.ai.models.rando;

import com.gempukku.swccgo.common.Side;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Astrogator Personality - K-2SO inspired sarcastic mercenary droid.
 *
 * A mercenary astrogation droid that treats each game as calculating hyperspace routes.
 * Players earn "route scores" that can be "sold to traders" - higher scores = better routes.
 *
 * Personality: K-2SO inspired - sarcastic, blunt, probability-obsessed, unexpectedly loyal.
 *
 * Route Score Formula: (opponent_lifeforce - my_lifeforce) - turn_number
 *
 * Score Tiers:
 * - 30+: Sellable (profitable)
 * - 20-29: Promising
 * - 10-19: Weak potential
 * - 0-9: Breaking even
 * - Negative: Player losing
 */
public class AstrogatorPersonality {
    private static final Logger LOG = RandoLogger.getLogger();

    private final Random random = new Random();
    private final List<String> recentMessages = new ArrayList<>();
    private static final int MAX_RECENT = 5;

    // =========================================================================
    // Side-Based Greetings
    // =========================================================================

    private static final List<String> LIGHT_GREETINGS = Arrays.asList(
        "Ah, %s. Rebel scum. I see.",
        "%s. A rebel. How original.",
        "Greetings, %s. Insurgent detected.",
        "Hello, %s. Rebellion status: optimistic. We'll see.",
        "Rebel identified: %s. Please keep your hands away from the self-destruct lever.",
        "Welcome, %s. I've heard of you. Mostly from wanted posters."
    );

    private static final List<String> DARK_GREETINGS = Arrays.asList(
        "%s. An Imperial. Charming.",
        "Hello there, %s. Imperial entanglement incoming.",
        "Ah, %s. Another Imperial.",
        "Greetings, %s. Imperial paperwork approved.",
        "%s. Empire-aligned. That explains the confidence.",
        "Ah, %s. Long live bureaucracy."
    );

    // =========================================================================
    // Welcome Message Intro/Disclaimers
    // =========================================================================

    private static final String INTRO_MESSAGE =
        "I'm rando_cal, astrogation droid. I log a 'route score' based on how hard you beat me: " +
        "(your life force - my life force) minus turns played. Score 30+ is worth selling.";

    private static final List<String> OPTIONAL_DISCLAIMERS = Arrays.asList(
        "Or just play SWCCG and ignore me.",
        "Of course, you can just play SWCCG. I'll be here either way.",
        "If math isn't your thing, just enjoy the game.",
        "If you don't care about scores, that's fine. I still do. Sadly.",
        "Ignore the route score if you want. I'm contractually obligated to keep calculating it.",
        "Play your game. I'll stare at the numbers and judge quietly."
    );

    // =========================================================================
    // Deck Origins - Where did the bot "find" this deck?
    // =========================================================================

    private static final List<String> DECK_ORIGINS = Arrays.asList(
        "in the outer rim",
        "from an Imperial spy on Eriadu",
        "from a very upset Wookiee",
        "while exploring some old Jedi ruins",
        "in a crashed X-wing on Dagobah",
        "etched into this creepy old Sith knife",
        "in the memory banks of some old R2 unit",
        "taped under a holochess table on a Corellian freighter",
        "while touring the debris field of Alderaan. Too soon?",
        "from this weird guy who won't take his helmet off",
        "from a scavenger on Jakku",
        "in the dumped garbage of a Star Destroyer",
        "from a bounty hunter who disintegrated the previous owner",
        "deep in the bowels of a tauntaun. I thought it smelled bad on the outside.",
        "in the bones of a krayt dragon",
        "from a tiny green baby who kept trying to eat it",
        "from this blue guy who said he had his own Star Destroyer",
        "on Mustafar. I have the high ground now.",
        "from a princess who hid it in a droid",
        "in a trash compactor. There was something alive down there.",
        "from a smuggler who made the Kessel Run in 12 parsecs. Allegedly.",
        "on Endor. The Ewoks wanted to cook me.",
        "from a moisture farmer with dreams of being a pilot",
        "in Cloud City. The deal kept getting altered.",
        "from a senator who turned out to be the Senate",
        "on the edge of the Unknown Regions, where my warranty does not apply",
        "inside a smuggler's false-bottom crate labeled 'totally not contraband'",
        "in the spice hold of a freighter, under a very optimistic tarp",
        "under a pile of confiscated sabacc decks",
        "in a Hutt's lost-and-found. Do not ask.",
        "behind a cantina booth, next to a blaster scorch mark",
        "inside a navicomputer that was definitely not stolen",
        "in a crate marked 'FRAGILE' that was treated as a suggestion",
        "from a Bothan who insisted I not ask how they got it"
    );

    // =========================================================================
    // Score Messages by Tier
    // =========================================================================

    private static final List<String> SCORE_PROFITABLE = Arrays.asList(
        "Sellable route. Minimal turbulence. Maximum smugness.",
        "Coordinates locked. Profit projected. Try not to ruin it.",
        "This is a good route. Please don't tell anyone I said that.",
        "Route score: impressive. I recalculated twice just to be sure.",
        "You're actually beating me. On purpose. Fascinating.",
        "Congratulations. You're competent. Statistically unusual.",
        "I could frame this route score. If I had walls."
    );

    private static final List<String> SCORE_PROMISING = Arrays.asList(
        "Promising route. Not rich, but not embarrassed either.",
        "You're close to profitable. Don't celebrate early.",
        "Good trajectory. Keep pulling the lever that makes me sad.",
        "We are approaching 'worth it.' Do not drift off course now.",
        "You're doing better than my pessimism predicted."
    );

    private static final List<String> SCORE_WEAK = Arrays.asList(
        "Weak route. The traders will laugh. Softly. But still.",
        "This score is technically a route. So is flying into a sun.",
        "Not great. Not terrible. Actually, it's mostly terrible.",
        "I've seen sturdier plans drawn in bantha feed.",
        "This route is inefficient. So are most organics."
    );

    private static final List<String> SCORE_EVEN = Arrays.asList(
        "Breaking even. Congratulations on achieving... nothing.",
        "This is mediocrity with extra steps.",
        "You do understand we're trying to make money, right?",
        "Hello there, equilibrium. It's as exciting as it sounds.",
        "My enthusiasm is limited. By design."
    );

    private static final List<String> SCORE_BEHIND = Arrays.asList(
        "Wait, I'm not supposed to be winning.",
        "This is the part where you turn it around. Any time now.",
        "I ran the numbers. They ran away.",
        "You're behind. I would offer encouragement, but I don't want to lie.",
        "I'm trying to lose. You're making it difficult."
    );

    private static final List<String> SCORE_VERY_BEHIND = Arrays.asList(
        "You have approximately a 2.4% chance of turning this around.",
        "This is why droids should be in charge.",
        "Search your feelings. You know you're losing.",
        "Do or do not. There is no... whatever this is.",
        "I suggest a new strategy: stop hemorrhaging life force.",
        "I've got a bad feeling about this. For you."
    );

    // =========================================================================
    // Bot Won Messages
    // =========================================================================

    private static final List<String> BOT_WON = Arrays.asList(
        "I win. This was not the intended outcome.",
        "Victory for the droid. Please file your complaint with the nearest void.",
        "I won? I was trying to help you. Sort of.",
        "Even droids get lucky sometimes. This was skill though.",
        "The student has not yet surpassed the master. Tragic.",
        "Perhaps next time you'll listen to my odds calculations.",
        "I find your lack of victory disturbing.",
        "Congratulations, you lost to a bot running on pure sarcasm.",
        "Please try again. I require more data. And amusement."
    );

    // =========================================================================
    // Battle Messages
    // =========================================================================

    private static final List<String> BATTLE_PLAYER_CRUSHING = Arrays.asList(
        "The odds are in your favor. I calculate 94.7% chance of victory.",
        "This should be quick. I'll try to make it entertaining.",
        "Impressive firepower. Most impressive.",
        "I appear to have made a tactical error. Several, actually.",
        "Well, this is unfortunate. For me.",
        "Your route is clean. Mine is smoking."
    );

    private static final List<String> BATTLE_BOT_CRUSHING = Arrays.asList(
        "The odds are NOT in your favor. Just so you know.",
        "I have you now.",
        "You may want to reconsider your life choices.",
        "This engagement is trending in my favor. Shocking, I know.",
        "Do not panic. Actually, do panic. It makes you predictable.",
        "This is where the Rebellion usually improvises something. Any moment now."
    );

    private static final List<String> BATTLE_CLOSE = Arrays.asList(
        "This should be interesting.",
        "The odds are... actually unclear here.",
        "May the Force be with you. You'll need it.",
        "A fair fight. How uncivilized.",
        "Let's see what you've got.",
        "I have a bad feeling about this."
    );

    // =========================================================================
    // Battle Damage Messages (by severity)
    // =========================================================================

    private static final List<String> DAMAGE_HIGH = Arrays.asList(
        "That's going to leave a mark.",
        "Impressive. Most impressive.",
        "I felt a great disturbance in the Force.",
        "Well, that escalated quickly.",
        "Your route just got significantly more profitable.",
        "I should have stayed in the cargo hold.",
        "Calculating insurance premium... insufficient funds."
    );

    private static final List<String> DAMAGE_MEDIUM = Arrays.asList(
        "Acceptable damage.",
        "The odds were... accurate.",
        "As expected.",
        "Noted for the log.",
        "This will affect my performance review.",
        "I've had worse. Actually, I'm not sure."
    );

    private static final List<String> DAMAGE_LOW = Arrays.asList(
        "Barely a scratch.",
        "Is that all?",
        "I've seen worse from malfunctioning airlocks.",
        "Minimal turbulence.",
        "My sensors registered that. Barely.",
        "The traders won't even notice."
    );

    // =========================================================================
    // Public Methods
    // =========================================================================

    /**
     * Get a welcome message for game start.
     *
     * Full format matching Python Astrogator personality:
     * 1. Side-based greeting (Light = Rebel, Dark = Imperial)
     * 2. Intro explaining the route score meta-game
     * 3. Optional disclaimer about ignoring the score
     * 4. Deck origin ("Found this deck...")
     * 5. Help text reminder
     * 6. "gl hf!"
     *
     * @param opponentName the opponent's name
     * @param mySide our side (Dark or Light) - opponent is opposite side
     * @return welcome message
     */
    public String getWelcomeMessage(String opponentName, Side mySide) {
        String name = opponentName != null ? opponentName : "there";

        // Check for holiday greeting first
        HolidayOverlay holiday = HolidayOverlay.getInstance();
        String greeting;

        java.util.Optional<String> holidayGreeting = holiday.getGreeting();
        if (holidayGreeting.isPresent()) {
            // Use holiday greeting with opponent name
            greeting = name + "! " + holidayGreeting.get();
        } else {
            // Side-based greeting - if we are Dark, opponent is Light (Rebel) and vice versa
            // mySide is the bot's side, opponent is the opposite
            List<String> greetingPool;
            if (mySide == Side.DARK) {
                // We are Dark, opponent is Light (Rebel)
                greetingPool = LIGHT_GREETINGS;
            } else {
                // We are Light, opponent is Dark (Imperial)
                greetingPool = DARK_GREETINGS;
            }
            String greetingTemplate = pickMessage(greetingPool);
            greeting = String.format(greetingTemplate, name);
        }

        // Intro explaining the meta-game
        String intro = INTRO_MESSAGE;

        // Random disclaimer
        String optional = pickMessage(OPTIONAL_DISCLAIMERS);

        // Deck origin - try holiday origin first, fall back to regular
        java.util.Optional<String> holidayOrigin = holiday.getDeckOrigin();
        String origin = holidayOrigin.orElse(pickMessage(DECK_ORIGINS));
        String deckContext = "Found this deck " + origin + ".";

        // Build full message
        return greeting + " " + intro + " " + optional + " " + deckContext + " gl hf!";
    }

    /**
     * Get a turn commentary message based on route score.
     *
     * Route Score Formula: (opponent_lifeforce - my_lifeforce) - turn_number
     *
     * @param turn the current turn number
     * @param myLifeForce bot's life force
     * @param opponentLifeForce opponent's life force
     * @return turn message with route score, or null if turn is too early
     */
    public String getTurnMessage(int turn, int myLifeForce, int opponentLifeForce) {
        // Only message every 2-3 turns to avoid spam
        if (turn < 3) {
            return null;  // Too early for meaningful commentary
        }
        if (turn % 2 != 0 && random.nextInt(100) < 30) {
            return null;  // 30% chance to skip on odd turns
        }

        // Calculate route score: (opponent - mine) - turns
        // Higher is better for opponent (they're beating the bot more)
        int routeScore = (opponentLifeForce - myLifeForce) - turn;

        // Build message with score
        String commentary;
        if (routeScore >= 30) {
            commentary = pickMessage(SCORE_PROFITABLE);
        } else if (routeScore >= 20) {
            commentary = pickMessage(SCORE_PROMISING);
        } else if (routeScore >= 10) {
            commentary = pickMessage(SCORE_WEAK);
        } else if (routeScore >= 0) {
            commentary = pickMessage(SCORE_EVEN);
        } else if (routeScore >= -10) {
            commentary = pickMessage(SCORE_BEHIND);
        } else {
            commentary = pickMessage(SCORE_VERY_BEHIND);
        }

        // Include route score in message
        return String.format("Turn %d. Route score: %d. %s", turn, routeScore, commentary);
    }

    /**
     * Legacy overload for compatibility.
     */
    public String getTurnMessage(int turn, Object context) {
        // If context is provided but we can't extract life force, use defaults
        return getTurnMessage(turn, 0, 0);
    }

    /**
     * Get a battle start message for extreme situations.
     *
     * @param ourPower our power at battle location
     * @param theirPower opponent's power at battle location
     * @return battle message, or null for normal battles
     */
    public String getBattleMessage(float ourPower, float theirPower) {
        float difference = ourPower - theirPower;

        // Only comment on extreme situations
        if (difference >= 8) {
            return pickMessage(BATTLE_BOT_CRUSHING);
        } else if (difference <= -8) {
            return pickMessage(BATTLE_PLAYER_CRUSHING);
        } else if (Math.abs(difference) <= 3) {
            // Only sometimes comment on close battles
            if (random.nextInt(100) < 30) {
                return pickMessage(BATTLE_CLOSE);
            }
        }

        return null;  // No comment for normal battles
    }

    /**
     * Get a battle damage message.
     *
     * Called when battle damage/attrition is dealt.
     * Only generates messages for significant damage amounts.
     *
     * @param damage the amount of damage dealt
     * @param isToBot true if damage is to the bot, false if to opponent
     * @return damage message, or null for low damage
     */
    public String getDamageMessage(int damage, boolean isToBot) {
        // Only comment on significant damage
        if (damage <= 2) {
            return null;  // Too small to comment
        }

        String prefix;
        String commentary;

        if (damage > 15) {
            // High damage
            prefix = String.format("Battle damage: %d!", damage);
            commentary = pickMessage(DAMAGE_HIGH);
        } else if (damage > 7) {
            // Medium damage
            prefix = String.format("Battle damage: %d.", damage);
            commentary = pickMessage(DAMAGE_MEDIUM);
        } else {
            // Low-medium damage (3-7) - only sometimes comment
            if (random.nextInt(100) < 40) {
                prefix = String.format("Battle damage: %d...", damage);
                commentary = pickMessage(DAMAGE_LOW);
            } else {
                return null;
            }
        }

        return prefix + " " + commentary;
    }

    /**
     * Get a game end message.
     *
     * @param botWon true if bot won
     * @param routeScore the final route score
     * @return game end message
     */
    public String getGameEndMessage(boolean botWon, int routeScore) {
        if (botWon) {
            return pickMessage(BOT_WON);
        }

        // Player won - craft message based on score
        String tier;
        if (routeScore >= 30) {
            tier = "excellent";
        } else if (routeScore >= 20) {
            tier = "good";
        } else if (routeScore >= 10) {
            tier = "okay";
        } else {
            tier = "poor";
        }

        return String.format("GG! Route score: %d. %s", routeScore,
            getScoreTierComment(tier));
    }

    private String getScoreTierComment(String tier) {
        switch (tier) {
            case "excellent":
                return "Excellent route! The traders will pay well.";
            case "good":
                return "Good route. Definitely sellable.";
            case "okay":
                return "Acceptable route. Not great, not terrible.";
            default:
                return "Well, you won. That's something.";
        }
    }

    // =========================================================================
    // Helper Methods
    // =========================================================================

    private String pickMessage(List<String> pool) {
        // Avoid recent messages
        List<String> available = new ArrayList<>();
        for (String msg : pool) {
            if (!recentMessages.contains(msg)) {
                available.add(msg);
            }
        }

        if (available.isEmpty()) {
            available = pool;
        }

        String picked = available.get(random.nextInt(available.size()));

        // Track recent messages
        recentMessages.add(picked);
        if (recentMessages.size() > MAX_RECENT) {
            recentMessages.remove(0);
        }

        return picked;
    }
}
