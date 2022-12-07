package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractSite;
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
import com.gempukku.swccgo.logic.modifiers.DockingBayTransitFromCostModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Location
 * Subtype: Site
 * Title: Spaceport Docking Bay
 */
public class Card7_126 extends AbstractSite {
    public Card7_126() {
        super(Side.LIGHT, "Spaceport Docking Bay", Uniqueness.DIAMOND_1, ExpansionSet.SPECIAL_EDITION, Rarity.F);
        setLocationDarkSideGameText("May not be deployed to Bespin, Dagobah, Endor, Hoth or Yavin 4. Your docking bay transit from here requires 3 Force. Your pilots deploy +1 here.");
        setLocationLightSideGameText("May not be deployed to Bespin, Dagobah, Endor, Hoth or Yavin 4. Your docking bay transit from here requires 2 Force. Your starfighters deploy -1 (or -2 if freighter) here.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.SPECIAL_EDITION, Icon.EXTERIOR_SITE, Icon.PLANET, Icon.SCOMP_LINK);
        addKeywords(Keyword.SPACEPORT_SITE, Keyword.DOCKING_BAY);
        addMayNotBePartOfSystem(Title.Bespin, Title.Dagobah, Title.Endor, Title.Hoth, Title.Yavin_4, Title.Ahch_To);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DockingBayTransitFromCostModifier(self, 3, playerOnDarkSideOfLocation));
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.your(playerOnDarkSideOfLocation), Filters.pilot), 1, self, true));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        Filter yourStarfighters = Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.or(Filters.starfighter, Filters.deploysAndMovesLikeStarfighter));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DockingBayTransitFromCostModifier(self, 2, playerOnLightSideOfLocation));
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(yourStarfighters, Filters.not(Filters.freighter)), -1, self, true));
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(yourStarfighters, Filters.freighter), -2, self, true));
        return modifiers;
    }
}