package com.gempukku.swccgo.cards.set501.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;

/**
 * Set: Set 15
 * Type: Location
 * Subtype: Site
 * Title: Death Star: Hangar 327
 */
public class Card501_022 extends AbstractSite {
    public Card501_022() {
        super(Side.LIGHT, "Death Star: Hangar 327", Title.Death_Star);
        setLocationDarkSideGameText("While Obi-Wan alone here, he is immune to attrition.");
        setLocationLightSideGameText("While Obi-Wan alone here, Vader may not Force drain on Death Star.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.INTERIOR_SITE, Icon.EXTERIOR_SITE, Icon.MOBILE, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_15);
        setTestingText("Death Star: Hangar 327");
        hideFromDeckBuilder();
    }
}
