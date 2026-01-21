package com.gempukku.swccgo.ai.models.rando.evaluators;

/**
 * Types of actions the bot can take.
 * Used for categorizing and scoring different decision types.
 */
public enum ActionType {
    // Core actions
    DEPLOY("deploy"),
    PASS("pass"),
    ACTIVATE_FORCE("activate_force"),
    BATTLE("battle"),
    MOVE("move"),
    DRAW("draw"),
    DRAW_DESTINY("draw_destiny"),
    SELECT_CARD("select_card"),
    ARBITRARY("arbitrary"),
    PLAY_CARD("play_card"),

    // Combat related
    FIRE_WEAPON("fire_weapon"),
    BATTLE_DESTINY("battle_destiny"),
    SUBSTITUTE_DESTINY("substitute_destiny"),
    CANCEL_DAMAGE("cancel_damage"),

    // Special actions
    FORCE_DRAIN("force_drain"),
    RACE_DESTINY("race_destiny"),
    REACT("react"),
    STEAL("steal"),
    SABACC("sabacc"),
    CANCEL("cancel"),
    EMBARK("embark"),

    // Unknown/fallback
    UNKNOWN("unknown");

    private final String value;

    ActionType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
