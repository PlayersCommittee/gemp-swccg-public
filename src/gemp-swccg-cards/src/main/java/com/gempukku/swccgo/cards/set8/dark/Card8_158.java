package com.gempukku.swccgo.cards.set8.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.PresentCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Endor
 * Type: Location
 * Subtype: Site
 * Title: Endor: Ancient Forest
 */
public class Card8_158 extends AbstractSite {
    public Card8_158() {
        super(Side.DARK, "Endor: Ancient Forest", Title.Endor, Uniqueness.UNIQUE, ExpansionSet.ENDOR, Rarity.U);
        setLocationDarkSideGameText("Your aliens are deploy -1 here (or -2 if Yuzzum). If your Yuzzum present, Force drain +1 here.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.ENDOR, Icon.EXTERIOR_SITE, Icon.PLANET);
        addKeywords(Keyword.FOREST);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        Filter yourAlien = Filters.and(Filters.your(playerOnDarkSideOfLocation), Filters.alien);
        Filter yourYuzzum = Filters.and(Filters.your(playerOnDarkSideOfLocation), Filters.Yuzzum);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(yourAlien, Filters.not(Filters.Yuzzum)), -1, self, true));
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(yourAlien, Filters.Yuzzum), -2, self, true));
        modifiers.add(new ForceDrainModifier(self, new PresentCondition(self, yourYuzzum), 1, playerOnDarkSideOfLocation));
        return modifiers;
    }
}