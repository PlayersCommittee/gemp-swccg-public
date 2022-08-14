package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.PresentCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Block 7
 * Type: Location
 * Subtype: Site
 * Title: Coruscant: Private Platform (Docking Bay)
 */
public class Card601_150 extends AbstractSite {
    public Card601_150() {
        super(Side.DARK, "Coruscant: Private Platform (Docking Bay)", Title.Coruscant);
        setLocationDarkSideGameText("If deployed by your [Block 7] objective, may not be separated from Palpatine's Quarters. Your docking bay transit to or from here is free.");
        setLocationLightSideGameText("All docking bay transit from same site as Insidious Prisoner is free. Your docking bay transit to or from here requires 2 Force.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcons(Icon.CLOUD_CITY, Icon.EPISODE_I, Icon.EXTERIOR_SITE, Icon.PLANET, Icon.LEGACY_BLOCK_7);
        addKeywords(Keyword.DOCKING_BAY);
        setAsLegacy(true);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        // If deployed by your [Block 7] objective, may not be separated from Palpatine's Quarters.

        modifiers.add(new DockingBayTransitFromForFreeModifier(self, playerOnDarkSideOfLocation));
        modifiers.add(new DockingBayTransitToForFreeModifier(self, playerOnDarkSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DockingBayTransitFromCostModifier(self, 2, playerOnLightSideOfLocation));
        modifiers.add(new DockingBayTransitToCostModifier(self, 2, playerOnLightSideOfLocation));
        modifiers.add(new DockingBayTransitFromForFreeModifier(self, Filters.at(Filters.Insidious_Prisoner), playerOnLightSideOfLocation));
        modifiers.add(new DockingBayTransitFromForFreeModifier(self, Filters.at(Filters.Insidious_Prisoner), game.getOpponent(playerOnLightSideOfLocation)));
        return modifiers;
    }
}