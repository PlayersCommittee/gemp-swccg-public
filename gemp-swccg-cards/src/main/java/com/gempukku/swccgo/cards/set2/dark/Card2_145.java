package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.cards.AbstractHolosite;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpecialRule;
import com.gempukku.swccgo.common.Title;


/**
 * Set: A New Hope
 * Type: Location
 * Subtype: Site
 * Title: Imperial Holotable
 */
public class Card2_145 extends AbstractHolosite {
    public Card2_145() {
        super(Side.DARK, Title.Imperial_Holotable);
        setLocationDarkSideGameText("'Dejarik Rules' in effect here.");
        setLocationLightSideGameText("'Dejarik Rules' in effect here. Site converted by Dejarik Hologameboard.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.A_NEW_HOPE);
        addSpecialRulesInEffectHere(SpecialRule.DEJARIK_RULES);
    }
}