package com.gempukku.swccgo.cards.set102.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.common.*;


/**
 * Set: Premium (Jedi Pack)
 * Type: Character
 * Subtype: Imperial
 * Title: Motti
 */
public class Card102_010 extends AbstractImperial {
    public Card102_010() {
        super(Side.DARK, 1, 4, 2, 2, 3, "Motti", Uniqueness.UNIQUE);
        setLore("Considered an overrated leader by many subordinates. Has a disturbing lack of faith. Became a member of the Death Star's command triumvirate despite his failings.");
        addPersona(Persona.MOTTI);
        addIcons(Icon.PREMIUM, Icon.PILOT);
        addKeywords(Keyword.LEADER);
    }
}
