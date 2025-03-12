package com.gempukku.swccgo.common;

/**
 * Represents the card types of SWCCG cards.
 */
public enum CardType implements Filterable {

    ADMIRALS_ORDER("Admiral's Order"),
    ALIEN("Alien"),
    CREATURE("Creature"),
    DARK_JEDI_MASTER("Dark Jedi Master"),
    DEFENSIVE_SHIELD("Defensive Shield"),
    DEVICE("Device"),
    DROID("Droid"),
    EFFECT("Effect"),
    EPIC_EVENT("Epic Event"),
    FIRST_ORDER("First Order"),
    IMPERIAL("Imperial"),
    INTERRUPT("Interrupt"),
    MANDALORIAN("Mandalorian"),
    JEDI_MASTER("Jedi Master"),
    JEDI_TEST("Jedi Test"),
    LOCATION("Location"),
    NEW_REPUBLIC("New Republic"),
    OBJECTIVE("Objective"),
    PODRACER("Podracer"),
    REBEL("Rebel"),
    REPUBLIC("Republic"),
    RESISTANCE("Resistance"),
    SITH("Sith"),
    STARSHIP("Starship"),
    VEHICLE("Vehicle"),
    WEAPON("Weapon");

    private String _humanReadable;

    CardType(String humanReadable) {
        _humanReadable = humanReadable;
    }

    public String getHumanReadable() {
        return _humanReadable;
    }
}
