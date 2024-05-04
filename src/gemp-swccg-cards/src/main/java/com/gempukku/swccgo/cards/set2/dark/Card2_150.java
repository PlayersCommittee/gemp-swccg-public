package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpecialRule;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;

/**
 * Set: A New Hope
 * Type: Location
 * Subtype: Site
 * Title: Tatooine: Bluffs
 */
public class Card2_150 extends AbstractSite {
    public Card2_150() {
        super(Side.DARK, Title.Bluffs, Title.Tatooine, Uniqueness.UNIQUE, ExpansionSet.A_NEW_HOPE, Rarity.R1);
        setLocationLightSideGameText("'Bluff Rules' in effect here.");
        setLocationDarkSideGameText("'Bluff Rules' in effect here.");
        addIcons(Icon.A_NEW_HOPE, Icon.EXTERIOR_SITE, Icon.PLANET);
        addSpecialRulesInEffectHere(SpecialRule.BLUFF_RULES);
    }
}