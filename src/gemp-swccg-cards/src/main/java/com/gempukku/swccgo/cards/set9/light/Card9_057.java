package com.gempukku.swccgo.cards.set9.light;

import com.gempukku.swccgo.cards.AbstractUniqueStarshipSite;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DockingBayTransitFromCostModifier;
import com.gempukku.swccgo.logic.modifiers.DockingBayTransitFromForFreeModifier;
import com.gempukku.swccgo.logic.modifiers.DockingBayTransitToCostModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotMoveToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Death Star II
 * Type: Location
 * Subtype: Site
 * Title: Home One: Docking Bay
 */
public class Card9_057 extends AbstractUniqueStarshipSite {
    public Card9_057() {
        super(Side.LIGHT, "Home One: Docking Bay", Persona.HOME_ONE, ExpansionSet.DEATH_STAR_II, Rarity.C);
        setLocationDarkSideGameText("Your docking bay transit from here requires 7 Force. Your transit to here requires +9 Force.");
        setLocationLightSideGameText("Your docking bay transit from here is free. If you occupy, opponent may not move to this site.");
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.DEATH_STAR_II, Icon.INTERIOR_SITE, Icon.EXTERIOR_SITE, Icon.STARSHIP_SITE, Icon.MOBILE, Icon.SCOMP_LINK);
        addKeywords(Keyword.DOCKING_BAY);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DockingBayTransitFromCostModifier(self, 7, playerOnDarkSideOfLocation));
        modifiers.add(new DockingBayTransitToCostModifier(self, 9, playerOnDarkSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DockingBayTransitFromForFreeModifier(self, playerOnLightSideOfLocation));
        modifiers.add(new MayNotMoveToLocationModifier(self, Filters.opponents(playerOnLightSideOfLocation),
                new OccupiesCondition(playerOnLightSideOfLocation, self), self));
        return modifiers;
    }
}