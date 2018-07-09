package com.gempukku.swccgo.cards.set209.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;

/**
 * Set: Set 9
 * Type: Location
 * Subtype: Site
 * Title: Xizor's Palace: Sewer
 */
public class Card209_051 extends AbstractSite {
    public Card209_051() {
        super(Side.DARK, Title.Sewer, Title.Xizors_Palace);
        setLocationDarkSideGameText("Once per game, if you occupy three battlegrounds, may retrieve a Black Sun agent into hand.");
        setLocationLightSideGameText("Once per game, if you control, may retrieve [Reflections II] Dash into hand.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 2);
    }
}