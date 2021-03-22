package com.gempukku.swccgo.cards.set501.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;

/**
 * Set: Set 15
 * Type: Location
 * Subtype: Site
 * Title: Death Star: Trash Compactor (V)
 */
public class Card501_020 extends AbstractSite {
    public Card501_020() {
        super(Side.LIGHT, Title.Trash_Compactor, Title.Death_Star);
        setLocationLightSideGameText("During your move phase, you may move to here for free from any Death Star site.");
        setLocationDarkSideGameText("Unless you control adjacent site, We're All Going To Be A Lot Thinner is canceled.");
        addIcons(Icon.INTERIOR_SITE, Icon.MOBILE, Icon.VIRTUAL_SET_15);
        setVirtualSuffix(true);
        setTestingText("Death Star: Trash Compactor (V)");
        hideFromDeckBuilder();
    }
}
