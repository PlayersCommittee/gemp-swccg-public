package com.gempukku.swccgo.cards.set7.dark;

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
 * Set: Special Edition
 * Type: Location
 * Subtype: Site
 * Title: Executor: Docking Bay
 */
public class Card7_282 extends AbstractUniqueStarshipSite {
    public Card7_282() {
        super(Side.DARK, "Executor: Docking Bay", Persona.EXECUTOR, ExpansionSet.SPECIAL_EDITION, Rarity.U);
        setLocationDarkSideGameText("Your docking bay transit from here is free. If you occupy, opponent may not move to this site.");
        setLocationLightSideGameText("Your docking bay transit from here requires 7 Force. Your transit to here requires +9 Force.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcons(Icon.SPECIAL_EDITION, Icon.INTERIOR_SITE, Icon.EXTERIOR_SITE, Icon.STARSHIP_SITE, Icon.MOBILE, Icon.SCOMP_LINK);
        addKeyword(Keyword.DOCKING_BAY);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DockingBayTransitFromForFreeModifier(self, playerOnDarkSideOfLocation));
        modifiers.add(new MayNotMoveToLocationModifier(self, Filters.opponents(playerOnDarkSideOfLocation),
                new OccupiesCondition(playerOnDarkSideOfLocation, self), self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DockingBayTransitFromCostModifier(self, 7, playerOnLightSideOfLocation));
        modifiers.add(new DockingBayTransitToCostModifier(self, 9, playerOnLightSideOfLocation));
        return modifiers;
    }
}