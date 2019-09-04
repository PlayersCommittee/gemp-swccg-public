package com.gempukku.swccgo.cards.set301.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;

/**
 * Set: Virtual Premium Set
 * Type: Character
 * Subtype: Rebel
 * Title: Puck
 */
public class Card301_007 extends AbstractRebel {
    public Card301_007() {
        super(Side.LIGHT, 1, 2, 2, 2, 4, "Puck", Uniqueness.UNIQUE);
        setLore("");
        setGameText("");
        setAlternateDestiny(7);
        setAlternateDestinyCost(3);
        addIcons(Icon.PREMIUM, Icon.VIRTUAL_SET_P, Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.RED_SQUADRON, Keyword.GREEN_SQUADRON);
    }
}
