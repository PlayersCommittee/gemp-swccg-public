package com.gempukku.swccgo.logic.modifiers.querying;

import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.common.CardState;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;

public interface Cards extends BaseQuery, Captives, Battle {
	default CardState getCardState(GameState gameState, PhysicalCard physicalCard, boolean includeExcludedFromBattle, boolean includeUndercover, boolean includeCaptives,
			boolean includeConcealed, boolean includeWeaponsForStealing, boolean includeMissing, boolean includeBinaryOff, boolean includeSuspended) {
		Zone zone = GameUtils.getZoneFromZoneTop(physicalCard.getZone());
		CardCategory cardCategory = physicalCard.getBlueprint().getCardCategory();
		CardSubtype cardSubtype = physicalCard.getBlueprint().getCardSubtype();

		if (zone==Zone.OUT_OF_PLAY)
			return CardState.OUT_OF_PLAY;

		if (zone.isLifeForce() || zone==Zone.HAND || zone==Zone.LOST_PILE)
			return CardState.UNIT_OF_FORCE;

		if (zone==Zone.STACKED_FACE_DOWN)
			return CardState.SUPPORTING;

		if (zone==Zone.STACKED) {
			if (!physicalCard.isStackedAsInactive()
					|| (physicalCard.getStackedOn() != null && Filters.grabber.accepts(gameState, query(), physicalCard.getStackedOn()))) {
				return CardState.SUPPORTING;
			}
			return CardState.INACTIVE;
		}

		if (zone==Zone.CONVERTED_LOCATIONS)
			return CardState.INACTIVE;

		if (!includeSuspended && physicalCard.isSuspended())
			return CardState.INACTIVE;

		if (!includeBinaryOff && physicalCard.isBinaryOff())
			return CardState.INACTIVE;

		if (!includeMissing && physicalCard.isMissing())
			return CardState.INACTIVE;

		if (!includeCaptives && (physicalCard.isCaptive() || physicalCard.isCapturedStarship()) &&
				!captiveMayParticipateInBattle(gameState, physicalCard))
			return CardState.INACTIVE;

		if (!includeExcludedFromBattle && zone.isInPlay() && gameState.isDuringBattle() && isExcludedFromBattle(gameState, physicalCard))
			return CardState.INACTIVE;

		if (!includeConcealed && physicalCard.isConcealed())
			return CardState.INACTIVE;

		if (!includeUndercover && physicalCard.isUndercover())
			return CardState.INACTIVE;

		if (!includeWeaponsForStealing) {
			if (cardCategory == CardCategory.WEAPON && cardSubtype == CardSubtype.CHARACTER && physicalCard.getAttachedTo() != null
					&& !physicalCard.getBlueprint().getValidToUseWeaponFilter(physicalCard.getOwner(), gameState.getGame(), physicalCard).accepts(gameState, query(), physicalCard.getAttachedTo()))
				return CardState.INACTIVE;

			if (cardCategory == CardCategory.DEVICE && cardSubtype == CardSubtype.CHARACTER && physicalCard.getAttachedTo() != null
					&& !physicalCard.getBlueprint().getValidToUseDeviceFilter(physicalCard.getOwner(), gameState.getGame(), physicalCard).accepts(gameState, query(), physicalCard.getAttachedTo()))
				return CardState.INACTIVE;
		}

		if (physicalCard.getAttachedTo() != null) {
			return getCardState(gameState, physicalCard.getAttachedTo(), includeExcludedFromBattle, true, includeCaptives, includeConcealed, includeWeaponsForStealing, includeMissing, includeBinaryOff, includeSuspended);
		}

		if (zone.isInPlay() && !physicalCard.getBlueprint().isInactiveInsteadOfActive(gameState.getGame(), physicalCard))
			return CardState.ACTIVE;

		return CardState.INACTIVE;
	}
}
