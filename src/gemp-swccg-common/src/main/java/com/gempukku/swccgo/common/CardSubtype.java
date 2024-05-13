package com.gempukku.swccgo.common;

/**
 * Represents the card subtypes that can exist within various card types
 */
public enum CardSubtype implements Filterable {

    NORMAL(""),
    ARTILLERY("Artillery"),
    AUTOMATED("Automated"),
    CAPITAL("Capital"),
    CHARACTER("Character"),
    COMBAT("Combat"),
    CREATURE("Creature"),
    DEATH_STAR("Death Star"),
    DEATH_STAR_II("Death Star II"),
    IMMEDIATE("Immediate"),
    LOST("Lost"),
    LOST_OR_STARTING("Lost Or Starting"),
    MOBILE("Mobile"),
    OUT_OF_PLAY("Out Of Play"),
    POLITICAL("Political"),
    SECTOR("Sector"),
    SHUTTLE("Shuttle"),
    SITE("Site"),
    SQUADRON("Squadron"),
    STARFIGHTER("Starfighter"),
    STARSHIP("Starship"),
    STARTING("Starting"),
    SYSTEM("System"),
    TRANSPORT("Transport"),
    USED("Used"),
    USED_OR_LOST("Used Or Lost"),
    USED_OR_STARTING("Used Or Starting"),
    UTINNI("Utinni"),
    VEHICLE("Vehicle");

    private String _humanReadable;

    CardSubtype(String humanReadable) {
        _humanReadable = humanReadable;
    }

    public String getHumanReadable() {
        return _humanReadable;
    }
}
