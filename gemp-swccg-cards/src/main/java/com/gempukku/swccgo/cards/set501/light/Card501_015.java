package com.gempukku.swccgo.cards.set501.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;

/**
 * Set: Set 15
 * Type: Location
 * Subtype: Site
 * Title: Ajan Kloss: Jedi Training Ground
 */
public class Card501_015 extends AbstractSite {
    public Card501_015() {
        super(Side.LIGHT, "Ajan Kloss: Jedi Training Ground", Title.Ajan_Kloss);
        setLocationDarkSideGameText("Players may not deploy or move cards to this location.");
        setLocationLightSideGameText("May only be deployed as a starting location.");
        addIcon(Icon.LIGHT_FORCE, 3);
        addIcons(Icon.EPISODE_VII, Icon.EXTERIOR_SITE, Icon.PLANET, Icon.VIRTUAL_SET_15);
        setTestingText("Ajan Kloss: Jedi Training Ground");
        excludeFromDeckBuilder();
    }
}
