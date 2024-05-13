package com.gempukku.swccgo.common;

/**
 * Represents the card categories of SWCCG cards.
 */
public enum CardCategory implements Filterable {

    ADMIRALS_ORDER("Admiral's Order"),
    CHARACTER("Character"),
    CREATURE("Creature"),
    DEFENSIVE_SHIELD("Defensive Shield"),
    DEVICE("Device"),
    EFFECT("Effect"),
    EPIC_EVENT("Epic Event"),
    GAME_AID("Game Aid"),
    INTERRUPT("Interrupt"),
    JEDI_TEST("Jedi Test"),
    LOCATION("Location"),
    OBJECTIVE("Objective"),
    PODRACER("Podracer"),
    STARSHIP("Starship"),
    VEHICLE("Vehicle"),
    WEAPON("Weapon");

    private String _humanReadable;

    CardCategory(String humanReadable) {
        _humanReadable = humanReadable;
    }

    public String getHumanReadable() {
        return _humanReadable;
    }
}
