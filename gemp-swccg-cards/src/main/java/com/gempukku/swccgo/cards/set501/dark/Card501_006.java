package com.gempukku.swccgo.cards.set501.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;

/**
 * Set: Set 15
 * Type: Location
 * Subtype: Site
 * Title: Mustafar: Private Platform (Docking Bay)
 */
public class Card501_006 extends AbstractSite {
    public Card501_006() {
        super(Side.DARK, "Mustafar: Private Platform (Docking Bay)", Title.Mustafar);
        setLocationDarkSideGameText("May deploy a starfighter with “Vader” in title here from Reserve Deck; reshuffle. Vanee is power +2 here.");
        setLocationLightSideGameText("If Vader or Vanee on table, your docking bay transit to or from here requires +3 Force (+5 if both).");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 0);
        addIcons(Icon.EXTERIOR_SITE, Icon.INTERIOR_SITE, Icon.PLANET, Icon.VIRTUAL_SET_15);
        setTestingText("Mustafar: Private Platform (Docking Bay)");
    }
}
