package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
import com.gempukku.swccgo.cards.conditions.DuringBattleAtCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.MayMoveOtherCardsAsReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayMoveOtherCardsAwayAsReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Dagobah
 * Type: Location
 * Subtype: System
 * Title: Anoat
 */
public class Card4_154 extends AbstractSystem {
    public Card4_154() {
        super(Side.DARK, Title.Anoat, 5, ExpansionSet.DAGOBAH, Rarity.U);
        setLocationDarkSideGameText("If opponent initiates a battle here, your starships may move as a 'react' to or from nearest related asteroid sector.");
        setLocationLightSideGameText("If you control, all opponent's Ugnaughts on table are forfeit -1.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.DAGOBAH, Icon.PLANET);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        Condition duringBattleHere = new DuringBattleAtCondition(self);
        Filter yourStarships = Filters.and(Filters.your(playerOnDarkSideOfLocation), Filters.starship);
        Filter nearestAsteroidSector = Filters.nearestRelatedAsteroidSector(self);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayMoveOtherCardsAsReactToLocationModifier(self, "Move starship as a 'react'", duringBattleHere,
                playerOnDarkSideOfLocation, Filters.and(yourStarships, Filters.at(nearestAsteroidSector)), self));
        modifiers.add(new MayMoveOtherCardsAwayAsReactToLocationModifier(self, "Move starship away as a 'react'", duringBattleHere,
                playerOnDarkSideOfLocation, Filters.and(yourStarships, Filters.at(self)), nearestAsteroidSector));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForfeitModifier(self, Filters.and(Filters.opponents(playerOnLightSideOfLocation), Filters.Ugnaught, Filters.onTable),
                new ControlsCondition(playerOnLightSideOfLocation, self), -1));
        return modifiers;
    }
}