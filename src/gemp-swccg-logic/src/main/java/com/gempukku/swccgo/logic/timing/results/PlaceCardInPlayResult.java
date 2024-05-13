package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * The effect result that is emitted when a card was just placed (not deployed or played) in play.
 */
public class PlaceCardInPlayResult extends EffectResult {
    private PhysicalCard _placedCard;
    private Zone _placedFrom;
    private PhysicalCard _attachedTo;
    private PhysicalCard _atLocation;

    /**
     * Creates an effect result that is emitted when a card is placed (not deployed or played) in play.
     * @param performingPlayerId the player that performed the action
     * @param placedCard the card placed
     * @param placedFrom the zone the card was placed from
     * @param attachedTo the card that placed card was placed as attached to
     * @param atLocation the location the the card was placed to (if not attached to another card)
     */
    public PlaceCardInPlayResult(String performingPlayerId, PhysicalCard placedCard, Zone placedFrom, PhysicalCard attachedTo, PhysicalCard atLocation) {
        super(Type.PLACE_IN_PLAY, performingPlayerId);
        _placedCard = placedCard;
        _placedFrom = placedFrom;
        _attachedTo = attachedTo;
        _atLocation = atLocation;
    }

    /**
     * Gets the placed card.
     * @return the placed card
     */
    public PhysicalCard getPlacedCard() {
        return _placedCard;
    }

    /**
     * Gets the zone the placed card was placed from.
     * @return the zone
     */
    public Zone getPlacedCardFrom() {
        return _placedFrom;
    }

    /**
     * Gets the card the card placed is attached to when placed, or null if the card is not attached to another card.
     * @return the card the card is attached to.
     */
    public PhysicalCard getAttachedTo() {
        return _attachedTo;
    }

    /**
     * Gets the location the card placed is at when placed, or null if the card is not at the location (or attached to another card).
     * @return the location the card is at (without being attached to another card).
     */
    public PhysicalCard getAtLocation() {
        return _atLocation;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return GameUtils.getCardLink(_placedCard) + " just placed in play";
    }
}
