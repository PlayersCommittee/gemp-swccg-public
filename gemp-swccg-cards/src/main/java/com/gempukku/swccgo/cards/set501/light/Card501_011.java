package com.gempukku.swccgo.cards.set501.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;

/**
 * Set: Set 15
 * Type: Character
 * Subtype: Alien
 * Title: Offworld Jawas
 */
public class Card501_011 extends AbstractAlien {
    public Card501_011() {
        super(Side.LIGHT, 3, 4, 3, 3, 4, "Offworld Jawas", Uniqueness.RESTRICTED_2);
        setLore("");
        setGameText("Draws one battle destiny if unable to otherwise. If you just verified opponent's Reserve Deck, may use 1 force: search that Reserve Deck and place one weapon, device, or unpiloted starship found there in Lost Pile; if none there, opponent loses 1 force.");
        setArmor(4);
        addIcons(Icon.WARRIOR, Icon.WARRIOR, Icon.VIRTUAL_SET_15);
        setTestingText("Offworld Jawas");
        excludeFromDeckBuilder();
    }
}
