package com.gempukku.swccgo.cards.set304.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.modifiers.HyperspeedModifier;
import com.gempukku.swccgo.logic.modifiers.MayMoveOtherCardsAsReactToLocationModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: The Great Hutt Expansion
 * Type: Character
 * Subtype: Alien
 * Title: Tia'nah
 */
public class Card304_061 extends AbstractAlien {
    public Card304_061() {
        super(Side.LIGHT, 3, 2, 0, 3, 3, "Tia'nah", Uniqueness.UNIQUE, ExpansionSet.GREAT_HUTT_EXPANSION, Rarity.V);
        setLore("Tia'nah is presented as the daughter of Locita. She rarely is seen alone and spends most of her time resting in their room. She seems awkward planetside but comes alive when on a starship.");
        setGameText("Adds 3 to power of any capital ship she is aboard as a passenger. Power is +2 when with Locita. Otherwise power is 0. When aboard a starship adds 3 to hyperspeed and may move to systems or sectors as a 'react.'");
		addIcons(Icon.NAV_COMPUTER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
		Filter filter = Filters.and(Filters.your(self), Filters.starship, Filters.here(self),
			Filters.hasAboard(self, Filters.and(Filters.astromech_droid, Filters.character)));
			
		List<Modifier> modifiers = new LinkedList<Modifier>();
		modifiers.add(new HyperspeedModifier(self, Filters.and(Filters.starship, Filters.hasAboard(self)), 3));
		modifiers.add(new PowerModifier(self, new WithCondition(self, Filters.Locita), 2));
		modifiers.add(new PowerModifier(self, Filters.and(Filters.capital_starship, Filters.hasPassenger(self)), 3));
		modifiers.add(new MayMoveOtherCardsAsReactToLocationModifier(self, "Move a starship as a react", self.getOwner(), filter, Filters.or(Filters.system, Filters.sector)));
        return modifiers;
    }
}