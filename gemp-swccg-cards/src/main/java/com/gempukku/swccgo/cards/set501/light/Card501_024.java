package com.gempukku.swccgo.cards.set501.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.common.*;

/**
 * Set: Set 15
 * Type: Character
 * Subtype: Rebel
 * Title: TK-422 (V)
 */
public class Card501_024 extends AbstractRebel {
    public Card501_024() {
        super(Side.LIGHT, 1, 6, 3, 3, 6, Title.TK422, Uniqueness.UNIQUE);
        setArmor(5);
        setLore("Corellian smuggler. Spy. Han stole the armor and identity of an enemy soldier that boarded the Millennium Falcon. Bluffed his way into the detention area.");
        setGameText("Stormtrooper. Deploy -3 to Death Star. Adds 3 to anything he pilots. Han's weapon destiny draws are +1 for each trooper here. If a battle just initiated here, may take Chewie or an Interrupt with \"triple\" in game text into hand from Reserve Deck; reshuffle.");
        addPersona(Persona.HAN);
        addIcons(Icon.SPECIAL_EDITION, Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.SPY, Keyword.SMUGGLER, Keyword.STORMTROOPER);
        setSpecies(Species.CORELLIAN);
        setVirtualSuffix(true);
        setTestingText("TK-422 (V)");
        excludeFromDeckBuilder();
    }
}
