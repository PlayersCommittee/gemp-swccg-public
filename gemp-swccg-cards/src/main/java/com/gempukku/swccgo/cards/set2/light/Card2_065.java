package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayMoveOtherCardsAsReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: A New Hope
 * Type: Location
 * Subtype: System
 * Title: Ralltiir
 */
public class Card2_065 extends AbstractSystem {
    public Card2_065() {
        super(Side.LIGHT, Title.Ralltiir, 3);
        setLocationDarkSideGameText("Your capital starships deploy -2 here.");
        setLocationLightSideGameText("Your starships may move here as a 'react.'");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.A_NEW_HOPE, Icon.PLANET);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.your(playerOnDarkSideOfLocation),
                Filters.and(Filters.capital_starship, Filters.not(Filters.deploysAndMovesLikeStarfighter))), -2, self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayMoveOtherCardsAsReactToLocationModifier(self, "Move starship as a 'react'", playerOnLightSideOfLocation, Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.starship), self));
        return modifiers;
    }
}