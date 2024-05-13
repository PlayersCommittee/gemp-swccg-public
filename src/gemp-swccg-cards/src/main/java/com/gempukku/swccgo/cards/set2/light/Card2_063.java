package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.cards.AbstractHolosite;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpecialRule;


/**
 * Set: A New Hope
 * Type: Location
 * Subtype: Site
 * Title: Dejarik Hologameboard
 */
public class Card2_063 extends AbstractHolosite {
    public Card2_063() {
        super(Side.LIGHT, "Dejarik Hologameboard", ExpansionSet.A_NEW_HOPE, Rarity.R1);
        setLocationDarkSideGameText("'Dejarik Rules' in effect here. Site converted by Imperial Holotable.");
        setLocationLightSideGameText("'Dejarik Rules' in effect here.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.A_NEW_HOPE);
        addSpecialRulesInEffectHere(SpecialRule.DEJARIK_RULES);
    }
}