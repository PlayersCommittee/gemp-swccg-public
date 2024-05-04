package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.PlayCardActionReason;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.actions.PlayCardAction;

/**
 * This class is used as the effect that is responded when a single interrupt card is being played for purposes of canceling, re-targeting, etc.
 */
public abstract class RespondablePlayCardEffect extends RespondableEffect implements RespondablePlayingCardEffect {
    private PlayCardAction _playCardAction;

    /**
     * Creates a respondable play card effect for when a single card is being played.
     * @param action the action performing this effect
     */
    public RespondablePlayCardEffect(PlayCardAction action) {
        super(action);
        _playCardAction = action;
    }

    @Override
    public Type getType() {
        return Type.PLAYING_CARD_EFFECT;
    }

    /**
     * Determines if the card is being deployed as a 'react'.
     * @return true or false
     */
    @Override
    public boolean isAsReact() {
        return false;
    }

    /**
     * Determines if card is being deployed as an 'insert' card.
     * @return true or false
     */
    @Override
    public boolean isAsInsertCard() {
        return false;
    }

    /**
     * Gets the card being played or deployed.
     * @return the card
     */
    @Override
    public PhysicalCard getCard() {
        return _playCardAction.getPlayedCard();
    }

    /**
     * Gets the zone the card is being played or deployed from.
     * @return the zone
     */
    @Override
    public Zone getPlayingFromZone() {
        return _playCardAction.getPlayingFromZone();
    }

    /**
     * Determines if the card is to be placed out of play when played.
     * @return true or false
     */
    @Override
    public boolean isToBePlacedOutOfPlay() {
        return _playCardAction.isToBePlacedOutOfPlay();
    }

    /**
     * Determines if card is being played for the specified reason.
     * @return true or false
     */
    @Override
    public boolean isPlayingForReason(PlayCardActionReason actionReason) {
        return _playCardAction.getGameTextActionId().isForActionReason(actionReason);
    }
}
