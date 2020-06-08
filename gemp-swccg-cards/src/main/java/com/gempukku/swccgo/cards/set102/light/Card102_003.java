package com.gempukku.swccgo.cards.set102.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.common.*;


/**
 * Set: Premium (Jedi Pack)
 * Type: Character
 * Subtype: Rebel
 * Title: Leia
 */
public class Card102_003 extends AbstractRebel {
    public Card102_003() {
        super(Side.LIGHT, 1, 4, 2, 2, 3, "Leia", Uniqueness.UNIQUE);
        setLore("Adopted daughter of the Viceroy and First Chairman of Alderaan. Became a political leader at a young age. The injustices of the New Order led her to join the Rebellion.");
        addPersona(Persona.LEIA);
        addIcons(Icon.PREMIUM);
        addKeywords(Keyword.LEADER, Keyword.FEMALE);
        setSpecies(Species.ALDERAANIAN);
    }
}
