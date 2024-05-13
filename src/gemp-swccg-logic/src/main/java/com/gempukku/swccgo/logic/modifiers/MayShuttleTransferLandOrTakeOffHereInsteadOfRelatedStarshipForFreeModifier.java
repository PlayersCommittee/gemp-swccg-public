package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.game.PhysicalCard;

/**
 * A modifier that allows specified player to shuttle, transfer, land, or take off for free from/to the source location
 * instead of related starship.
 */
public class MayShuttleTransferLandOrTakeOffHereInsteadOfRelatedStarshipForFreeModifier extends AbstractModifier {

    /**
     * Creates a modifier that allows specified player to shuttle, transfer, land, or take off for free from/to the source
     * location instead of related starship.
     * @param source the card that is the source of the modifier
     * @param playerId the player
     */
    public MayShuttleTransferLandOrTakeOffHereInsteadOfRelatedStarshipForFreeModifier(PhysicalCard source, String playerId) {
        super(source, null, source, null, ModifierType.MAY_USE_LOCATION_TO_SHUTTLE_TRANSFER_LAND_OR_TAKE_OFF_FOR_FREE_INSTEAD_OF_RELATED_STARSHIP, true);
        _playerId = playerId;
    }
}
