package com.gempukku.swccgo.ai.models.rando;

import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Holiday Overlay System
 *
 * Provides seasonal personality overlays for the Astrogator brain.
 * Holidays have special greetings and message pools.
 *
 * Currently supported:
 * - Life Day (Dec 1-31): Star Wars Holiday Special themed
 *
 * The system is extensible - add new holiday configurations.
 * Only one holiday is active at a time (first matching wins).
 *
 * Key pattern: shouldUseHolidayMessage() returns true 50% of time
 * when holiday is active, providing mixed personality.
 */
public class HolidayOverlay {
    private static final Logger LOG = RandoLogger.getLogger();

    private static HolidayOverlay instance;
    private final Random random = new Random();

    // Current active holiday (cached, checked daily)
    private Holiday activeHoliday;
    private LocalDate lastCheckDate;

    /**
     * Get the singleton instance.
     */
    public static synchronized HolidayOverlay getInstance() {
        if (instance == null) {
            instance = new HolidayOverlay();
        }
        return instance;
    }

    private HolidayOverlay() {
        checkActiveHoliday();
        if (activeHoliday != null) {
            LOG.info("[HolidayOverlay] Holiday active: {}", activeHoliday.name);
        }
    }

    // =========================================================================
    // Holiday Detection
    // =========================================================================

    /**
     * Check if any holiday is currently active.
     */
    public boolean isHolidayActive() {
        checkActiveHoliday();
        return activeHoliday != null;
    }

    /**
     * Get the name of the active holiday.
     */
    public String getHolidayName() {
        checkActiveHoliday();
        return activeHoliday != null ? activeHoliday.name : null;
    }

    /**
     * Check if we should use a holiday message (50% chance when active).
     */
    public boolean shouldUseHolidayMessage() {
        if (!isHolidayActive()) {
            return false;
        }
        return random.nextDouble() < 0.5;
    }

    private void checkActiveHoliday() {
        LocalDate today = LocalDate.now();
        if (lastCheckDate != null && lastCheckDate.equals(today)) {
            return;  // Already checked today
        }

        lastCheckDate = today;
        activeHoliday = null;

        // Check each holiday
        for (Holiday holiday : HOLIDAYS) {
            if (holiday.isActive(today)) {
                activeHoliday = holiday;
                break;
            }
        }
    }

    // =========================================================================
    // Message Retrieval
    // =========================================================================

    /**
     * Get a holiday greeting if active.
     *
     * @return Optional containing greeting, or empty if no holiday
     */
    public Optional<String> getGreeting() {
        if (!shouldUseHolidayMessage()) {
            return Optional.empty();
        }
        return Optional.of(pickRandom(activeHoliday.greetings));
    }

    /**
     * Get a holiday deck origin if active.
     *
     * @return Optional containing origin, or empty if no holiday/not selected
     */
    public Optional<String> getDeckOrigin() {
        if (!shouldUseHolidayMessage() || activeHoliday.deckOrigins.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(pickRandom(activeHoliday.deckOrigins));
    }

    /**
     * Get a holiday score message for a tier.
     *
     * @param tier the score tier (profitable, promising, weak, even, behind, very_behind)
     * @return Optional containing message, or empty if no holiday/no message for tier
     */
    public Optional<String> getScoreMessage(String tier) {
        if (!shouldUseHolidayMessage()) {
            return Optional.empty();
        }

        List<String> messages = getMessagesForTier(activeHoliday, tier);
        if (messages.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(pickRandom(messages));
    }

    private List<String> getMessagesForTier(Holiday holiday, String tier) {
        switch (tier.toLowerCase()) {
            case "profitable":
                return holiday.scoreProfitable;
            case "promising":
                return holiday.scorePromising;
            case "weak":
                return holiday.scoreWeak;
            case "even":
                return holiday.scoreEven;
            case "behind":
                return holiday.scoreBehind;
            case "very_behind":
                return holiday.scoreVeryBehind;
            default:
                return Arrays.asList();
        }
    }

    private String pickRandom(List<String> list) {
        if (list.isEmpty()) {
            return "";
        }
        return list.get(random.nextInt(list.size()));
    }

    // =========================================================================
    // Holiday Configuration
    // =========================================================================

    private static class Holiday {
        final String key;
        final String name;
        final Month startMonth;
        final int startDay;
        final Month endMonth;
        final int endDay;

        final List<String> greetings;
        final List<String> deckOrigins;
        final List<String> scoreProfitable;
        final List<String> scorePromising;
        final List<String> scoreWeak;
        final List<String> scoreEven;
        final List<String> scoreBehind;
        final List<String> scoreVeryBehind;

        Holiday(String key, String name, Month startMonth, int startDay,
                Month endMonth, int endDay,
                List<String> greetings, List<String> deckOrigins,
                List<String> scoreProfitable, List<String> scorePromising,
                List<String> scoreWeak, List<String> scoreEven,
                List<String> scoreBehind, List<String> scoreVeryBehind) {
            this.key = key;
            this.name = name;
            this.startMonth = startMonth;
            this.startDay = startDay;
            this.endMonth = endMonth;
            this.endDay = endDay;
            this.greetings = greetings;
            this.deckOrigins = deckOrigins;
            this.scoreProfitable = scoreProfitable;
            this.scorePromising = scorePromising;
            this.scoreWeak = scoreWeak;
            this.scoreEven = scoreEven;
            this.scoreBehind = scoreBehind;
            this.scoreVeryBehind = scoreVeryBehind;
        }

        boolean isActive(LocalDate date) {
            Month month = date.getMonth();
            int day = date.getDayOfMonth();

            // Handle same-month holidays
            if (startMonth == endMonth) {
                return month == startMonth && day >= startDay && day <= endDay;
            }

            // Handle cross-year holidays (e.g., Dec 15 - Jan 5)
            if (startMonth.getValue() > endMonth.getValue()) {
                if (month == startMonth) {
                    return day >= startDay;
                } else if (month == endMonth) {
                    return day <= endDay;
                } else if (month.getValue() > startMonth.getValue()
                        || month.getValue() < endMonth.getValue()) {
                    return true;
                }
                return false;
            }

            // Handle multi-month holidays in same year
            if (month == startMonth) {
                return day >= startDay;
            } else if (month == endMonth) {
                return day <= endDay;
            } else if (month.getValue() > startMonth.getValue()
                    && month.getValue() < endMonth.getValue()) {
                return true;
            }

            return false;
        }
    }

    // =========================================================================
    // Life Day Holiday (Dec 1-31) - Star Wars Holiday Special Themed
    // =========================================================================

    private static final Holiday LIFE_DAY = new Holiday(
        "life_day",
        "Life Day",
        Month.DECEMBER, 1,
        Month.DECEMBER, 31,

        // Greetings
        Arrays.asList(
            "Happy Life Day! May your journey home be swift.",
            "Life Day greetings! The Tree of Life awaits.",
            "Happy Life Day! May you make it to Kashyyyk in time for the celebration.",
            "Life Day is upon us! Time to don our ceremonial robes.",
            "Malla, Itchy, and Lumpy send their Life Day regards.",
            "Somewhere on Kashyyyk, a Wookiee family awaits.",
            "Happy Life Day! Lumpy is watching his favorite hologram.",
            "Life Day greetings from the treetops of Kashyyyk!",
            "Happy Life Day! Yes, the Holiday Special was real. We don't talk about it.",
            "It's Life Day! Try not to think too hard about the Holiday Special."
        ),

        // Deck origins
        Arrays.asList(
            "while Chewie was trying to get home for Life Day",
            "from Lumpy's secret hiding spot on Kashyyyk",
            "during a Life Day celebration in the Wookiee treehouse",
            "from Gormaanda's cooking show. Stir, whip, stir, whip!",
            "while watching Jefferson Starship via hologram",
            "in Ackmena's cantina during the Imperial curfew",
            "from a very disturbing VR headset experience",
            "hidden inside a Life Day orb",
            "during Carrie Fisher's Life Day song. I have feelings now.",
            "from an animated segment that was somehow the best part"
        ),

        // Score messages by tier
        Arrays.asList(
            "A Life Day miracle! This route is actually sellable.",
            "The Tree of Life smiles upon this route score!",
            "Gormaanda would be proud. Stir, whip, stir, whip your way to victory!",
            "This route score is the real Holiday Special.",
            "Happy Life Day indeed! This score is a gift."
        ),
        Arrays.asList(
            "The Life Day orb glows with moderate approval.",
            "Somewhere, a Wookiee family nods in tentative approval.",
            "Getting warmer! Like a Kashyyyk fireplace.",
            "Lumpy would be... mildly entertained by this score."
        ),
        Arrays.asList(
            "This score is about as coherent as the Holiday Special plot.",
            "The Life Day spirit is... wavering.",
            "Even Gormaanda's cooking show made more sense than this score.",
            "Itchy's VR headset provided more value than this."
        ),
        Arrays.asList(
            "This route is going nowhere, like the Holiday Special narrative.",
            "The Tree of Life is unimpressed. Very unimpressed.",
            "This is the route score equivalent of watching Lumpy's circus acrobats.",
            "Somewhere, Bea Arthur is sighing at this score."
        ),
        Arrays.asList(
            "The Empire will definitely find you at this rate.",
            "Even the Holiday Special had better pacing than your strategy.",
            "Life Day celebrations are in jeopardy!",
            "Malla is worried about you. Very worried."
        ),
        Arrays.asList(
            "This is worse than Harvey Korman's cooking droid malfunction.",
            "The Life Day tree has wilted. You did this.",
            "Not even the Mind Evaporator can make you forget this score.",
            "Happy Life Day... I guess someone has to lose.",
            "Lumpy is crying. Are you happy now?"
        )
    );

    // List of all holidays (first matching wins)
    private static final List<Holiday> HOLIDAYS = Arrays.asList(
        LIFE_DAY
        // Add more holidays here: MAY_THE_FOURTH, etc.
    );
}
