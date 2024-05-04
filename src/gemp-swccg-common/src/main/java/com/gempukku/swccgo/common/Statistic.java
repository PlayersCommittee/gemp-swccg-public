package com.gempukku.swccgo.common;

/**
 * Represents the card statistics that can be targeted when drawing destiny.
 */
public enum Statistic {
    DESTINY("Destiny"),
    DEPLOY("Deploy cost"),
    POWER("Power"),
    ABILITY("Ability"),
    MANEUVER("Maneuver"),
    ARMOR("Armor"),
    DEFENSE_VALUE("Defense value"),
    FORFEIT("Forfeit");

    private String _humanReadable;

    Statistic(String humanReadable) {
        _humanReadable = humanReadable;
    }

    public String getHumanReadable() {
        return _humanReadable;
    }
}
