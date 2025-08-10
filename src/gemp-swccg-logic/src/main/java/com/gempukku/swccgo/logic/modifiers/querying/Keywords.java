package com.gempukku.swccgo.logic.modifiers.querying;

import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifierCollector;
import com.gempukku.swccgo.logic.modifiers.ModifierCollectorImpl;
import com.gempukku.swccgo.logic.modifiers.ModifierType;

public interface Keywords extends BaseQuery {
	default boolean hasKeyword(GameState gameState, PhysicalCard physicalCard, Keyword keyword) {
		return hasKeyword(gameState, physicalCard, keyword, new ModifierCollectorImpl());
	}

	default boolean hasKeyword(GameState gameState, PhysicalCard physicalCard, Keyword keyword, ModifierCollector modifierCollector) {
		// 'Blown away' docking bay is no longer a docking bay
		if (keyword == Keyword.DOCKING_BAY && physicalCard.isBlownAway())
			return false;

		boolean retVal = physicalCard.getBlueprint().hasKeyword(keyword);

		for (Modifier modifier : getKeywordModifiersAffectingCard(gameState, ModifierType.GIVE_KEYWORD, keyword, physicalCard)) {
			if (modifier.hasKeyword(gameState, query(), physicalCard, keyword)) {
				retVal = true;
				modifierCollector.addModifier(modifier);
			}
		}

		for (Modifier modifier : getKeywordModifiersAffectingCard(gameState, ModifierType.REMOVE_KEYWORD, keyword, physicalCard)) {
			if (modifier.hasKeyword(gameState, query(), physicalCard, keyword)) {
				retVal = false;
				modifierCollector.addModifier(modifier);
			}
		}

		return retVal;
	}

	/**
	 * Gets the sites marker number.
	 * @param gameState the game state
	 * @param physicalCard a marker site
	 * @return the marker number, or null if not a marker site
	 */
	default Integer getMarkerNumber(GameState gameState, PhysicalCard physicalCard) {
		if (physicalCard.getBlueprint().hasKeyword(Keyword.MARKER_1)) {
			return 1;
		}
		if (physicalCard.getBlueprint().hasKeyword(Keyword.MARKER_2)) {
			return 2;
		}
		if (physicalCard.getBlueprint().hasKeyword(Keyword.MARKER_3)) {
			return 3;
		}
		if (physicalCard.getBlueprint().hasKeyword(Keyword.MARKER_4)) {
			return 4;
		}
		if (physicalCard.getBlueprint().hasKeyword(Keyword.MARKER_5)) {
			return 5;
		}
		if (physicalCard.getBlueprint().hasKeyword(Keyword.MARKER_6)) {
			return 6;
		}
		if (physicalCard.getBlueprint().hasKeyword(Keyword.MARKER_7)) {
			return 7;
		}
		return null;
	}
}
