package com.gempukku.swccgo.cards.set102.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.common.*;


/**
 * Set: Premium (Jedi Pack)
 * Type: Character
 * Subtype: Imperial
 * Title: Tarkin
 */
public class Card102_011 extends AbstractImperial {
    public Card102_011() {
        super(Side.DARK, 1, 4, 2, 2, 3, "Tarkin", Uniqueness.UNIQUE);
        setLore("Imperial Governor of the Seswenna Sector. Conceived and implemented the Death Star project. A leader in the effort to crush the Rebellion.");
        addPersona(Persona.TARKIN);
        addIcons(Icon.PREMIUM);
        addKeywords(Keyword.LEADER);
    }
}
