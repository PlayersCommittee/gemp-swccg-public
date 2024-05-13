package com.gempukku.swccgo.logic.actions;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;


/**
 * An abstract action that has the base implementation for all play card actions.
 */
public abstract class AbstractPlayCardAction extends AbstractAction implements PlayCardAction {
    protected PhysicalCard _actionSource;
    protected PhysicalCard _cardToPlay;
    protected Zone _playedFromZone;
    protected boolean _reshuffle;
    protected boolean _placeOutOfPlay;
    protected String _text;

    /**
     * Creates an action for playing the specified card.
     * @param cardToPlay the card to play
     * @param actionSource the card to initiate the deployment
     */
    public AbstractPlayCardAction(PhysicalCard cardToPlay, PhysicalCard actionSource) {
        _cardToPlay = cardToPlay;
        _playedFromZone = cardToPlay.getZone();
        _actionSource = actionSource;
    }

    @Override
    public Type getType() {
        return Type.PLAY_CARD;
    }

    @Override
    public PhysicalCard getActionSource() {
        return _actionSource;
    }

    @Override
    public PhysicalCard getActionAttachedToCard() {
        return  _cardToPlay;
    }

    @Override
    public PhysicalCard getPlayedCard() {
        return _cardToPlay;
    }

    /**
     * Gets the other played card in cases that two cards are deployed simultaneously.
     * @return the other played card
     */
    @Override
    public PhysicalCard getOtherPlayedCard() {
        return null;
    }

    /**
     * Gets the zone the card is being played or deployed from.
     * @return the zone
     */
    @Override
    public Zone getPlayingFromZone() {
        return _playedFromZone;
    }

    @Override
    public String getText() {
        return _text;
    }

    /**
     * Sets the text shown for the action selection on the User Interface.
     * @param text the text to show for the action selection
     */
    @Override
    public void setText(String text) {
        _text = text;
    }

    /**
     * Sets if the card pile the card is played from is reshuffled.
     * @param reshuffle true if pile the card is played from is reshuffled, otherwise false
     */
    @Override
    public void setReshuffle(boolean reshuffle) {
        _reshuffle = reshuffle;
    }

    /**
     * Sets that the card is to be placed out of play when played.
     * @param placeOutOfPlay true if card is to be placed out of play
     */
    @Override
    public void setPlaceOutOfPlay(boolean placeOutOfPlay) {
        _placeOutOfPlay = placeOutOfPlay;
    }

    /**
     * Determines if the card is to be placed out of play when played.
     * @return true or false
     */
    @Override
    public boolean isToBePlacedOutOfPlay() {
        return _placeOutOfPlay;
    }
}
