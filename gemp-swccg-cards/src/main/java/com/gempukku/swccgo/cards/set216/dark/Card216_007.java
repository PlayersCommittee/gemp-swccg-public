package com.gempukku.swccgo.cards.set216.dark;

import com.gempukku.swccgo.cards.AbstractMobileSystem;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.*;
import com.gempukku.swccgo.logic.modifiers.MayMoveOtherCardsAsReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 16
 * Type: Location
 * Subtype: System
 * Title: Death Star (V)
 */
public class Card216_007 extends AbstractMobileSystem {
    public Card216_007() {
        super(Side.DARK, Title.Death_Star, 2, 4);
        setVirtualSuffix(true);
        setLocationDarkSideGameText("X = parsec of current position (starts at 4). Deploys only if On The Verge Of Greatness on table. Hyperspeed = 2. Starships may move between Death Star and system it orbits as a 'react.'");
        addIcon(Icon.DARK_FORCE, 2);
        addIcons(Icon.VIRTUAL_SET_16);
    }

    @Override
    protected boolean checkPlayRequirements(String playerId, SwccgGame game, PhysicalCard self, DeploymentRestrictionsOption deploymentRestrictionsOption, PlayCardOption playCardOption, ReactActionOption reactActionOption) {
        return super.checkPlayRequirements(playerId, game, self, deploymentRestrictionsOption, playCardOption, reactActionOption)
                && GameConditions.canSpot(game, self, Filters.On_The_Verge_Of_Greatness);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new MayMoveOtherCardsAsReactToLocationModifier(self, "Move starship from system this orbits to here as a 'react'", null,
                Filters.and(Filters.starship, Filters.at(Filters.isOrbitedBy(self))), self));
        modifiers.add(new MayMoveOtherCardsAsReactToLocationModifier(self, "Move starship from here to system this orbits as a 'react'", null,
                Filters.and(Filters.starship, Filters.here(self)), Filters.isOrbitedBy(self)));
        return modifiers;
    }
}