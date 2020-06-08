package com.gempukku.swccgo.cards.set9.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.evaluators.PerTIEEvaluator;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Death Star II
 * Type: Location
 * Subtype: Site
 * Title: Death Star II: Docking Bay
 */
public class Card9_145 extends AbstractSite {
    public Card9_145() {
        super(Side.DARK, Title.Death_Star_II_Docking_Bay, Title.Death_Star_II);
        setLocationDarkSideGameText("Your TIEs deploy -1 here. Your docking bay transit from here is free.");
        setLocationLightSideGameText("Your docking bay transit to or from here requires 8 Force (ignore other docking bay's text).");
        addIcon(Icon.DARK_FORCE, 1);
        addIcons(Icon.DEATH_STAR_II, Icon.INTERIOR_SITE, Icon.EXTERIOR_SITE, Icon.MOBILE, Icon.SCOMP_LINK);
        addKeywords(Keyword.DOCKING_BAY);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.your(playerOnDarkSideOfLocation), Filters.TIE), new PerTIEEvaluator(-1), self));
        modifiers.add(new DockingBayTransitFromForFreeModifier(self, playerOnDarkSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DockingBayTransitFromCostModifier(self, 8, playerOnLightSideOfLocation));
        modifiers.add(new DockingBayTransitToCostModifier(self, 8, playerOnLightSideOfLocation));
        modifiers.add(new IgnoreOtherDockingBayTransitCostModifier(self, playerOnLightSideOfLocation));
        return modifiers;
    }
}