package com.gempukku.swccgo.cards.set501.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;

/**
 * Set: Block 15
 * Type: Effect
 * Title: Ubrikkian Industries
 */
public class Card501_005 extends AbstractNormalEffect {
    public Card501_005() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Ubrikkian Industries");
        setLore("");
        setGameText("Deploy on table. During your deploy phase, reveal an alien with 'vehicle' in gametext from hand to take a transport vehicle (or vice versa) from Reserve Deck and deploy both simultaneously. Drivers are immune to Clash of Sabers. [Immune to Alter]");
        addIcons(Icon.VIRTUAL_SET_15);
        addImmuneToCardTitle(Title.Alter);
        setTestingText("Ubrikkian Industries");
        hideFromDeckBuilder();
    }
}
