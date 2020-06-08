package com.gempukku.swccgo.cards.set102.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.common.*;


/**
 * Set: Premium (Jedi Pack)
 * Type: Character
 * Subtype: Alien
 * Title: Han
 */
public class Card102_002 extends AbstractAlien {
    public Card102_002() {
        super(Side.LIGHT, 1, 4, 2, 2, 3, "Han", Uniqueness.UNIQUE);
        setLore("Corellian. Graduated with honors from the Imperial Academy. Dishonorably discharged. Wanders the galaxy building a reputation as a gambler and a hot-shot pilot.");
        addPersona(Persona.HAN);
        addIcons(Icon.PREMIUM, Icon.PILOT);
        addKeywords(Keyword.GAMBLER);
        setSpecies(Species.CORELLIAN);
    }
}
