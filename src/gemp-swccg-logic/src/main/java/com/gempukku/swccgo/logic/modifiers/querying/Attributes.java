package com.gempukku.swccgo.logic.modifiers.querying;

import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;

public interface Attributes extends BaseQuery {
	/**
	 * Determines if the card has a deploy cost attribute.
	 *
	 * @param card a card
	 * @return true if card has a deploy cost attribute, otherwise false
	 */
	default boolean hasDeployCostAttribute(PhysicalCard card) {

		// TODO: Add a way to check if this card has a deploy cost attribute

		if (card.getBlueprint().getCardCategory() == CardCategory.INTERRUPT)
			return false;

		return true;
	}

	/**
	 * Determines if the card has a forfeit value attribute.
	 *
	 * @param card a card
	 * @return true if card has a forfeit value attribute, otherwise false
	 */
	default boolean hasForfeitValueAttribute(PhysicalCard card) {

		// TODO: Add a way to check if this card has a forfeit value attribute

		if (card.getBlueprint().getCardCategory() == CardCategory.INTERRUPT)
			return false;

		return true;
	}

	/**
	 * Determines if the card has a power attribute.
	 *
	 * @param card a card
	 * @return true if card has a power attribute, otherwise false
	 */
	default boolean hasPowerAttribute(PhysicalCard card) {

		// TODO: Add a way to check if this card has a power attribute

		if (card.getBlueprint().getCardCategory() == CardCategory.INTERRUPT)
			return false;

		return true;
	}

	/**
	 * Determines if the card has a politics attribute.
	 *
	 * @param card a card
	 * @return true if card has a politics attribute, otherwise false
	 */
	default boolean hasPoliticsAttribute(PhysicalCard card) {
		return (card.getBlueprint().getCardCategory() == CardCategory.CHARACTER);
	}

	/**
	 * Determines if query() deploys like a starfighter.
	 * @param gameState the game state
	 * @param card the card
	 * @return true if deploys like a starfighter, otherwise false
	 */
	default boolean isDeploysLikeStarfighter(GameState gameState, PhysicalCard card) {
		return card.getBlueprint().isDeploysLikeStarfighter() || Filters.squadron.accepts(gameState, query(), card);
	}

	/**
	 * Determines if query() moves like a starfighter.
	 * @param gameState the game state
	 * @param card the card
	 * @return true if moves like a starfighter, otherwise false
	 */
	default boolean isMovesLikeStarfighter(GameState gameState, PhysicalCard card) {
		return card.getBlueprint().isMovesLikeStarfighter() || Filters.squadron.accepts(gameState, query(), card);
	}

	/**
	 * Determines if query() deploys like a starfighter at cloud sectors.
	 * @param gameState the game state
	 * @param card the card
	 * @return true if deploys like a starfighter at cloud sectors, otherwise false
	 */
	default boolean isDeploysLikeStarfighterAtCloudSectors(GameState gameState, PhysicalCard card) {
		if (isDeploysLikeStarfighter(gameState, card))
			return true;

		if (Filters.or(Filters.shuttle_vehicle, Filters.cloud_car, Filters.Patrol_Craft).accepts(gameState, query(), card))
			return true;

		return card.getBlueprint().isDeploysLikeStarfighterAtCloudSectors();
	}

	/**
	 * Determines if query() moves like a starfighter at cloud sectors.
	 * @param gameState the game state
	 * @param card the card
	 * @return true if moves like a starfighter at cloud sectors, otherwise false
	 */
	default boolean isMovesLikeStarfighterAtCloudSectors(GameState gameState, PhysicalCard card) {
		if (isMovesLikeStarfighter(gameState, card))
			return true;

		if (Filters.or(Filters.shuttle_vehicle, Filters.cloud_car, Filters.Patrol_Craft).accepts(gameState, query(), card))
			return true;

		return card.getBlueprint().isMovesLikeStarfighterAtCloudSectors();
	}



	default boolean isVehicleSlotOfStarshipCompatible(GameState gameState, PhysicalCard card) {
		return card.getBlueprint().isVehicleSlotOfStarshipCompatible();
	}
}
