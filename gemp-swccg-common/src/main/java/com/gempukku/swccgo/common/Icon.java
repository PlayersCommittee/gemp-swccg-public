package com.gempukku.swccgo.common;

/**
 * Represents the icons on a SWCCG card.
 */
public enum Icon implements Filterable {

    // Expansion sets
    PREMIUM("Premium", true),
    A_NEW_HOPE("A New Hope", true),
    HOTH("Hoth", true),
    DAGOBAH("Dagobah", true),
    CLOUD_CITY("Cloud City", true),
    JABBAS_PALACE("Jabba's Palace", true),
    SPECIAL_EDITION("Special Edition", true),
    ENDOR("Endor", true),
    DEATH_STAR_II("Death Star II", true),
    REFLECTIONS_II("Reflections II", true),
    TATOOINE("Tatooine", true),
    CORUSCANT("Coruscant", true),
    REFLECTIONS_III("Reflections III", true),
    THEED_PALACE("Theed Palace", true),
    VIRTUAL_DEFENSIVE_SHIELD("Virtual Defensive Shield", true),
    VIRTUAL_SET_P("Set P", true),
    VIRTUAL_SET_0("Set 0", true),
    VIRTUAL_SET_1("Set 1", true),
    VIRTUAL_SET_2("Set 2", true),
    VIRTUAL_SET_3("Set 3", true),
    VIRTUAL_SET_4("Set 4", true),
    VIRTUAL_SET_5("Set 5", true),
    VIRTUAL_SET_6("Set 6", true),
    VIRTUAL_SET_7("Set 7", true),
    VIRTUAL_SET_8("Set 8", true),
    VIRTUAL_SET_9("Set 9", true),

    // Card types
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
    JEDI_MASTER("Jedi Master"),
    JEDI_TEST("Jedi Test"),
    OBJECTIVE("Objective"),
    PODRACER("Podracer"),
    REBEL("Rebel"),
    REPUBLIC("Republic"),
    RESISTANCE("Resistance"),
    SITH("Sith"),
    STARSHIP("Starship"),
    VEHICLE("Vehicle"),
    WEAPON("Weapon"),

    // Skills
    NAV_COMPUTER("Nav Computer"),
    PERMANENT_WEAPON("Permanent Weapon"),
    PILOT("Pilot"),
    PRESENCE("Presence"),
    SELECTIVE_CREATURE("Selective Creature"),
    WARRIOR("Warrior"),

    // Location features
    CREATURE_SITE("Creature Site"),
    DARK_FORCE("Force (Dark)"),
    EXTERIOR_SITE("Exterior Site"),
    INTERIOR_SITE("Interior Site"),
    LIGHT_FORCE("Force (Light)"),
    MOBILE("Mobile"),
    PLANET("Planet"),
    SPACE("Space"),
    STARSHIP_SITE("Starship Site"),
    UNDERGROUND("Underground"),
    UNDERWATER("Underwater"),
    VEHICLE_SITE("Vehicle Site"),

    // Other features
    CLONE_ARMY("Clone Army"),
    EPISODE_I("Episode I"),
    EPISODE_VII("Episode VII"),
    GRABBER("Grabber"),
    INDEPENDENT("Independent"),
    MAINTENANCE("Maintenance"),
    SEPERATIST("Seperatist"),
    SCOMP_LINK("Scomp Link"),
    TRADE_FEDERATION("Trade Federation");

    private String _humanReadable;
    private boolean _expansionIcon;

    Icon(String humanReadable) {
        this(humanReadable, false);
    }

    Icon(String humanReadable, boolean expansionIcon) {
        _humanReadable = humanReadable;
        _expansionIcon = expansionIcon;
    }

    public static Icon getIconFromName(String name) {
        for (Icon icon : values()) {
            if (icon.name().equals(name)) {
                return icon;
            }
        }
        return null;
    }

    public String getHumanReadable() {
        return _humanReadable;
    }

    public boolean isExpansionIcon() {
        return _expansionIcon;
    }
}
