package com.gempukku.swccgo.logic;

import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgCardBlueprint;

import java.util.*;

/**
 * This class contains common game utilities methods.
 */
public class GameUtils {

    /**
     * Gets the card title of the specified card.
     * @param card the card
     * @return the card title
     */
    public static String getFullName(PhysicalCard card) {
        return card.getTitle() + getTitleSuffix(card.getBlueprint());     
    }

    /**
     * Gets the card title of the specified card by blueprint.
     * @param blueprint the card blueprint
     * @return the card title
     */
    public static String getFullName(SwccgCardBlueprint blueprint) {
        return blueprint.getTitle() + getTitleSuffix(blueprint);
    }

    /**
     * Gets the card title suffix.
     * @param blueprint the card blueprint
     * @return the card title suffix
     */
    private static String getTitleSuffix(SwccgCardBlueprint blueprint) {
        return (blueprint.hasAlternateImageSuffix() ? " (AI)" : "") + (blueprint.hasVirtualSuffix() ? " (V)" : "");
    }

    /**
     * Gets the full location name of a generic location on the specified system.
     * @param systemName the system name
     * @param blueprint the card blueprint
     * @return the full location name
     */
    private static String getGenericLocationFullName(String systemName, SwccgCardBlueprint blueprint) {
        return systemName + ": " + blueprint.getTitle() + getTitleSuffix(blueprint);
    }

    /**
     * Gets the specified number of random cards from the specified card list.
     * @param cards the card list
     * @param count the number of random cards to get
     * @return a list of the random cards selected
     */
    public static List<PhysicalCard> getRandomCards(Collection<PhysicalCard> cards, int count) {
        List<PhysicalCard> randomizedCards = new ArrayList<PhysicalCard>(cards);
        Collections.shuffle(randomizedCards);
        return new LinkedList<>(randomizedCards.subList(0, Math.min(count, randomizedCards.size())));
    }

    /**
     * Gets a string for "card(s)" based on the number of cards in the collection.
     * @param cards the cards
     * @return a string for "card(s)"
     */
    public static String numCards(Collection<PhysicalCard> cards) {
        int numCards = cards.size();
        if (cards.size() > 1)
            return numCards + " cards";
        return "a card";
    }

    /**
     * Gets a string for "s" (used to make a word plural) based on the number of cards in the collection.
     * @param cards the cards
     * @return a string for "s"
     */
    public static String s(Collection<PhysicalCard> cards) {
        if (cards.size() != 1)
            return "s";
        return "";
    }

    /**
     * Gets a string for "s" (used to make a word plural) based on the number of cards.
     * @param num the number of cards
     * @return a string for "s"
     */
    public static String s(int num) {
        if (num != 1)
            return "s";
        return "";
    }

    /**
     * Gets a string for "s" (used to make a word plural) based on the number of cards.
     * @param num the number of cards
     * @return a string for "s"
     */
    public static String s(float num) {
        if (num != 1)
            return "s";
        return "";
    }

    /**
     * Gets a string for the verb "to be" based on the number of cards.
     * @param cards the cards
     * @return a string for "to be"
     */
    public static String be(Collection<PhysicalCard> cards) {
        if (cards.size() != 1)
            return "are";
        return "is";
    }

    /**
     * Gets a string for the verb "to be" based on the number of items.
     * @param num the number of items
     * @return a string for "to be"
     */
    public static String be(int num) {
        if (num != 1)
            return "are";
        return "is";
    }

    /**
     * Gets a card link (which is shown as a clickable link in the User Interface) for the specified card.
     * @param card the card
     * @return the card link
     */
    public static String getCardLink(PhysicalCard card) {
        SwccgCardBlueprint blueprint = card.getBlueprint();
        SwccgCardBlueprint otherSideBlueprint = card.getOtherSideBlueprint();

        // Special case for Big One: Asteroid Cave Or Space Slug Belly
        if (blueprint.getTitle().equals(Title.Big_One_Asteroid_Cave_Or_Space_Slug_Belly)
                && card.getZone() != null && (card.getZone().isInPlay() || card.getZone() == Zone.CONVERTED_LOCATIONS)) {
            String titleToUse = card.isSpaceSlugBelly() ? Title.Space_Slug_Belly : Title.Big_One_Asteroid_Cave;
            return getCardLink(card.getBlueprintId(true), blueprint, titleToUse, otherSideBlueprint);
        }
        if (blueprint.getCardCategory() == CardCategory.LOCATION
                && blueprint.getUniqueness()!=null
                && blueprint.getUniqueness().isPerSystem()
                && card.getPartOfSystem()!=null) {
            return getCardLinkForGenericLocation(card.getBlueprintId(true), blueprint, card.getPartOfSystem(), otherSideBlueprint);
        }
        return getCardLink(card.getBlueprintId(true), blueprint, otherSideBlueprint);
    }

    /**
     * Gets a card link (which is shown as a clickable link in the User Interface) for the specified blueprint id and blueprint.
     * @param blueprintId the blueprint id
     * @param blueprint the card blueprint
     * @param otherSideBlueprint the blueprint of the other side of the card, or null
     * @return the card link
     */
    public static String getCardLink(String blueprintId, SwccgCardBlueprint blueprint, SwccgCardBlueprint otherSideBlueprint) {
        String testingText = blueprint.getTestingText();

        String dots = "";
        if (blueprint.getUniqueness()!=null) {
            dots = blueprint.getUniqueness().getHumanReadable();
        }

        String fullTitle = "";
        List<String> titles = blueprint.getTitles();
        for (String title : titles) {
            if (!fullTitle.isEmpty()) {
                fullTitle += " & ";
            }
            fullTitle += (dots + title);
        }
        fullTitle += getTitleSuffix(blueprint);

        String backSideTestingText = otherSideBlueprint != null ? otherSideBlueprint.getTestingText() : null;

        return "<div class='cardHint' value='" + blueprintId + "'" + (testingText != null ? (" data-testingText='" + convertTestingText(testingText) + "'") : "")
                + (backSideTestingText != null ? (" data-backSideTestingText='" + convertTestingText(backSideTestingText) + "'") : "") + ">" + fullTitle + "</div>";
    }

    /**
     * Gets a card link (which is shown as a clickable link in the User Interface) for the specified location using by
     * using the specified title.
     * @param blueprintId the blueprint id
     * @param blueprint the card blueprint
     * @param titleToUse the title to use
     * @param otherSideBlueprint the blueprint of the other side of the card, or null
     * @return the card link
     */
    private static String getCardLink(String blueprintId, SwccgCardBlueprint blueprint, String titleToUse, SwccgCardBlueprint otherSideBlueprint) {
        String testingText = blueprint.getTestingText();

        String dots = "";
        if (blueprint.getUniqueness()!=null) {
            dots = blueprint.getUniqueness().getHumanReadable();
        }

        String fullTitle = titleToUse;
        fullTitle += getTitleSuffix(blueprint);

        String backSideTestingText = otherSideBlueprint != null ? otherSideBlueprint.getTestingText() : null;

        return "<div class='cardHint' value='" + blueprintId + "'" + (testingText != null ? (" data-testingText='" + convertTestingText(testingText) + "'") : "")
                + (backSideTestingText != null ? (" data-backSideTestingText='" + convertTestingText(backSideTestingText) + "'") : "") + ">" + dots + fullTitle + "</div>";
    }

    /**
     * Gets a card link (which is shown as a clickable link in the User Interface) for the specified generic location when
     * part of the specified system.
     * @param blueprintId the blueprint id
     * @param blueprint the card blueprint
     * @param systemName the system name
     * @param otherSideBlueprint the blueprint of the other side of the card, or null
     * @return the card link
     */
    private static String getCardLinkForGenericLocation(String blueprintId, SwccgCardBlueprint blueprint, String systemName, SwccgCardBlueprint otherSideBlueprint) {
        String testingText = blueprint.getTestingText();

        String dots = "";
        if (blueprint.getUniqueness()!=null) {
            dots = blueprint.getUniqueness().getHumanReadable();
        }

        String backSideTestingText = otherSideBlueprint != null ? otherSideBlueprint.getTestingText() : null;

        return "<div class='cardHint' value='" + blueprintId + "'" + (testingText != null ? (" data-testingText='" + convertTestingText(testingText) + "'") : "")
                + (backSideTestingText != null ? (" data-backSideTestingText='" + convertTestingText(backSideTestingText) + "'") : "") + ">" + dots + GameUtils.getGenericLocationFullName(systemName, blueprint) + "</div>";
    }

    /**
     * Convert testing text for HTML.
     * @param testingText the testing text
     * @return the card titles
     */
    public static String convertTestingText(String testingText) {
        return testingText.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("'", "&apos;");
    }

    /**
     * Gets a string of the card titles of the cards in the collection.
     * @param cards the cards
     * @return the card titles
     */
    public static String getAppendedTextNames(Collection<PhysicalCard> cards) {
        StringBuilder sb = new StringBuilder();
        for (PhysicalCard card : cards) {
            sb.append(GameUtils.getFullName(card));
            sb.append(", ");
        }

        if (sb.length() == 0)
            return "none";
        else
            return sb.substring(0, sb.length() - 2);
    }

    /**
     * Gets a string of the card links (which are shown as a clickable link in the User Interface) of the cards in the collection.
     * @param cards the cards
     * @return the card links
     */
    public static String getAppendedNames(Collection<PhysicalCard> cards) {
        StringBuilder sb = new StringBuilder();
        for (PhysicalCard card : cards) {
            sb.append(GameUtils.getCardLink(card));
            sb.append(", ");
        }

        if (sb.length() == 0)
            return "none";
        else
            return sb.substring(0, sb.length() - 2);
    }

    /**
     * Gets the top of card pile zone for the specified card pile zone, otherwise just the passed in zone is returned if
     * the passed in zone is not a card pile zone.
     * @param zone the card pile zone
     * @return the top of card pile zone, otherwise the passed in zone
     */
    public static Zone getZoneTopFromZone(Zone zone) {
        Zone zoneTop = zone;
        if (zone == Zone.RESERVE_DECK)
            zoneTop = Zone.TOP_OF_RESERVE_DECK;
        else if (zone == Zone.FORCE_PILE)
            zoneTop = Zone.TOP_OF_FORCE_PILE;
        else if (zone == Zone.USED_PILE)
            zoneTop = Zone.TOP_OF_USED_PILE;
        else if (zone == Zone.LOST_PILE)
            zoneTop = Zone.TOP_OF_LOST_PILE;
        else if (zone == Zone.UNRESOLVED_DESTINY_DRAW)
            zoneTop = Zone.TOP_OF_UNRESOLVED_DESTINY_DRAW;

        return zoneTop;
    }

    /**
     * Gets the card pile zone for the specified top of card pile zone, otherwise just the passed in zone is returned if
     * the passed in zone is not a top of card pile zone.
     * @param zoneTop the top of card pile zone
     * @return the card pile zone, otherwise the passed in zone
     */
    public static Zone getZoneFromZoneTop(Zone zoneTop) {
        Zone zone = zoneTop;
        if (zoneTop == Zone.TOP_OF_RESERVE_DECK)
            zone = Zone.RESERVE_DECK;
        else if (zoneTop == Zone.TOP_OF_FORCE_PILE)
            zone = Zone.FORCE_PILE;
        else if (zoneTop == Zone.TOP_OF_USED_PILE)
            zone = Zone.USED_PILE;
        else if (zoneTop == Zone.TOP_OF_LOST_PILE)
            zone = Zone.LOST_PILE;
        else if (zoneTop == Zone.TOP_OF_UNRESOLVED_DESTINY_DRAW)
            zone = Zone.UNRESOLVED_DESTINY_DRAW;

        return zone;
    }
}
