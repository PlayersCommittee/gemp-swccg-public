package com.gempukku.swccgo.common;

/**
 * Represents the card rarities in SWCCG.
 */
public enum Rarity {
    UR("Ultra Rare"),
    XR("Extra Rare"),
    PM("Premium"),
    PV("Preview"),
    R1("Rare (R1)"),
    R("Rare (R)"),
    R2("Rare (R2)"),
    F("Fixed (F)"),
    U1("Uncommon (U1)"),
    U("Uncommon (U)"),
    U2("Uncommon (U2)"),
    C1("Common (C1)"),
    C("Common (C)"),
    C2("Common (C2)"),
    C3("Common (C3)"),
    V("Virtual");

    private String _humanReadable;

    Rarity(String humanReadable) {
        _humanReadable = humanReadable;
    }

    public String getHumanReadable() {
        return _humanReadable;
    }

    public static Rarity getRarityFromString(String value) {
        for (Rarity rarity : values()) {
            if (rarity.toString().equals(value)) {
                return rarity;
            }
        }
        return null;
    }
}
