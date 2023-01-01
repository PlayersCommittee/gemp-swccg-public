package com.gempukku.swccgo.cards.set102.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;


/**
 * Set: Premium (Jedi Pack)
 * Type: Character
 * Subtype: Rebel
 * Title: Leia
 */
public class Card102_003 extends AbstractRebel {
    public Card102_003() {

        super(Side.LIGHT, 1, 4, 2, 2, 3, "Leia", Uniqueness.UNIQUE, ExpansionSet.JEDI_PACK, Rarity.PM);
        setLore("Adopted daughter of the Viceroy and First Chairman of Alderaan. Became a political leader at a young age. The injustices of the New Order led her to join the Rebellion.");
        addPersona(Persona.LEIA);
        addIcons(Icon.PREMIUM);
        addKeywords(Keyword.LEADER, Keyword.FEMALE);
        setSpecies(Species.ALDERAANIAN);
    }
}
