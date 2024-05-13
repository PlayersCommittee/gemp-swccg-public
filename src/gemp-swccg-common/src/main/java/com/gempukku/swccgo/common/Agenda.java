package com.gempukku.swccgo.common;

/**
 * Represents the political agendas in a SWCCG game.
 */
public enum Agenda implements Filterable {
    AMBITION("Ambition"),
    BLOCKADE("Blockade"),
    JUSTICE("Justice"),
    PEACE("Peace"),
    ORDER("Order"),
    REBELLION("Rebellion"),
    TAXATION("Taxation"),
    TRADE("Trade"),
    WEALTH("Wealth");

    private String _humanReadable;

    Agenda(String humanReadable) {
        _humanReadable = humanReadable;
    }

    public String getHumanReadable() {
        return _humanReadable;
    }
}
