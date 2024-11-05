package com.gempukku.swccgo.cards.set304.light;

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.cards.conditions.DuringBattleAtCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.MayMoveOtherCardsAsReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayMoveOtherCardsAwayAsReactToLocationModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: The Great Hutt Expansion
 * Type: Location
 * Subtype: System
 * Title: Ulress
 */
public class Card304_079 extends AbstractSystem {
    public Card304_079() {
        super(Side.LIGHT, Title.Ulress, 6, ExpansionSet.GREAT_HUTT_EXPANSION, Rarity.V);
        setLocationDarkSideGameText("Your Bounty Hunters are deploy -2 here.");
		setLocationLightSideGameText("If opponent initiates a battle here, your starships may move as a 'react' to or from nearest related sector.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.PLANET);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.your(playerOnDarkSideOfLocation), Filters.bounty_hunter), -2, self));
        return modifiers;
    }
	
	@Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        Condition duringBattleHere = new DuringBattleAtCondition(self);
        Filter yourStarships = Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.starship);
        Filter nearestRelatedSector = Filters.nearestRelatedSector(self);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayMoveOtherCardsAsReactToLocationModifier(self, "Move starship as a 'react'", duringBattleHere,
                playerOnLightSideOfLocation, Filters.and(yourStarships, Filters.at(nearestRelatedSector)), self));
        modifiers.add(new MayMoveOtherCardsAwayAsReactToLocationModifier(self, "Move starship away as a 'react'", duringBattleHere,
                playerOnLightSideOfLocation, Filters.and(yourStarships, Filters.at(self)), nearestRelatedSector));
        return modifiers;
    }
}