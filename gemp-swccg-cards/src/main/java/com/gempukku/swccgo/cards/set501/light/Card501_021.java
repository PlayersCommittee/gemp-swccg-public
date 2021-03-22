package com.gempukku.swccgo.cards.set501.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;

/**
 * Set: Set 15
 * Type: Location
 * Subtype: Site
 * Title: Death Star: Central Core (V)
 */
public class Card501_021 extends AbstractSite {
    public Card501_021() {
        super(Side.LIGHT, Title.Death_Star_Central_Core, Title.Death_Star);
        setLocationDarkSideGameText("May not be converted. Once per game, may deploy a trooper here from Reserve deck; reshuffle.");
        setLocationLightSideGameText("If you just initiated a Force drain here, place a card stacked on A Power Loss in owner's Used Pile.");
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcon(Icon.DARK_FORCE, 1);
        addIcons(Icon.INTERIOR_SITE, Icon.MOBILE, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_15);
        setVirtualSuffix(true);
        setTestingText("Death Star: Central Core (V)");
        excludeFromDeckBuilder();
    }
}
