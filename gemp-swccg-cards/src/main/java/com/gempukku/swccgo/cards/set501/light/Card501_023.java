package com.gempukku.swccgo.cards.set501.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.common.*;

/**
 * Set: Set 15
 * Type: Character
 * Subtype: Rebel
 * Title: TK-421
 */
public class Card501_023 extends AbstractRebel {
    public Card501_023() {
        super(Side.LIGHT, 1, 6, 3, 4, 7, "TK-421", Uniqueness.UNIQUE);
        setArmor(5);
        setLore("Spy. Luke Skywalker. Stormtrooper");
        setGameText("Adds 3 to anything he pilots. Deploy -3 to Death Star. Once per battle, may cancel the game text of an Imperial of ability < 3 here. Immune to Nevar Yalnal and attrition < 4 (< 6 if I Can't Believe He's Gone added power this battle).");
        addPersona(Persona.LUKE);
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_15);
        addKeywords(Keyword.SPY, Keyword.STORMTROOPER);
        setTestingText("TK-421");
    }
}
