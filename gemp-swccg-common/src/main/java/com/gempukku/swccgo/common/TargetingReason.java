package com.gempukku.swccgo.common;

/**
 * Represents the reasons a card can be targeted. In SWCCG, cards can be immune
 * to being targeted in certain ways, and cards to respond to an action that
 * targets a card in certain ways.
 */
public enum TargetingReason {
    OTHER("other"), // Default: Other reason, but still need to check if card is immune to the card doing the targeting
    NONE("none"), // Just spotting the card, so no targeting reason, and do not need to check if card is immune to the card doing the spotting
    TO_BE_BLOWN_AWAY("to be 'blown away'"),
    TO_BE_CANCELED("to be canceled"),
    TO_BE_CAPTURED("to be captured"),
    TO_BE_CHOKED("to be 'choked'"),
    TO_BE_COLLAPSED("to be 'collapsed'"),
    TO_BE_CRASHED("to be 'crashed'"),
    TO_BE_DEPLOYED_ON("to be deployed on"),
    TO_BE_DISARMED("to be Disarmed"),
    TO_BE_DUELED("to be dueled"),
    TO_BE_EXCLUDED_FROM_BATTLE("to be excluded from battle"),
    TO_BE_FROZEN("to be frozen"),
    TO_BE_HIT("to be 'hit'"),
    TO_BE_LOST("to be lost"),
    TO_BE_MISSING("to go missing"),
    TO_BE_PLACED_OUT_OF_PLAY("to be placed out of play"),
    TO_BE_PURCHASED("to be 'purchased'"),
    TO_BE_RELEASED("to be released"),
    TO_BE_STOLEN("to be stolen"),
    TO_BE_STOLEN_EVEN_IF_CARDS_ABOARD("to be stolen"),
    TO_BE_SUSPENDED("to be suspended"),
    TO_BE_TORTURED("to be tortured"),
    TO_RELOCATE_STARDUST_TO("to relocate Stardust to");

    private String _humanReadable;

    TargetingReason(String humanReadable) {
        _humanReadable = humanReadable;
    }

    public String getHumanReadable() {
        return _humanReadable;
    }
}
