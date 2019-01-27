package com.gempukku.swccgo.common;

/**
 * Represents the destiny types.
 */
public enum DestinyType {
    ASTEROID_DESTINY("asteroid destiny"),
    BATTLE_DESTINY("battle destiny"),
    CARBON_FREEZING_DESTINY("carbon freezing destiny"),
    DARK_HOURS_DESTINY("Dark Hours destiny"),
    DESTINY("destiny"),
    DESTINY_TO_ATTRITION("destiny to attrition"),
    DESTINY_TO_TOTAL_POWER("destiny to total power"),
    DUEL_DESTINY("duel destiny"),
    EPIC_EVENT_DESTINY("epic event destiny"),
    EPIC_EVENT_AND_WEAPON_DESTINY("epic event and weapon destiny"),
    LIGHTSABER_COMBAT_DESTINY("lightsaber combat destiny"),
    MOVEMENT_DESTINY("movement destiny"),
    RACE_DESTINY("race destiny"),
    SEARCH_PARTY_DESTINY("search party destiny"),
    TRACTOR_BEAM_DESTINY("tractor beam destiny"),
    TRAINING_DESTINY("training destiny"),
    WEAPON_DESTINY("weapon destiny");

    private String _humanReadable;

    DestinyType(String humanReadable) {
        _humanReadable = humanReadable;
    }

    public String getHumanReadable() {
        return _humanReadable;
    }
}
