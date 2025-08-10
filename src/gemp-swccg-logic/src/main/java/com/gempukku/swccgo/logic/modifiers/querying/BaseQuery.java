package com.gempukku.swccgo.logic.modifiers.querying;

import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.*;

/**
 * The basic shared functionality that is used by nearly all subinterfaces of ModifiersQuerying.
 */
public interface BaseQuery extends ModifiersState {
	ModifiersQuerying query();
	SwccgGame game();

	default Collection<PhysicalCard> getActiveCardsAffectedByModifier(GameState gameState, ModifierType modifierType) {
		Collection<PhysicalCard> allCards = Filters.filterActive(gameState.getGame(), null, Filters.any);
		Collection<PhysicalCard> subset = new HashSet<PhysicalCard>();
		for(PhysicalCard card: allCards) {
			if (!getModifiersAffectingCard(gameState, modifierType, card).isEmpty())
				subset.add(card);
		}
		return subset;
	}

	default boolean isDoubled(GameState gameState, PhysicalCard physicalCard) {
		return isDoubled(gameState, physicalCard, new ModifierCollectorImpl());
	}

	default boolean isDoubled(GameState gameState, PhysicalCard physicalCard, ModifierCollector modifierCollector) {
		boolean retVal = false;
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.IS_DOUBLED, physicalCard)) {
			retVal = true;
			modifierCollector.addModifier(modifier);
		}
		return retVal;
	}
}
