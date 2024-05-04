package com.gempukku.swccgo.logic.actions;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An interface to define the methods that play card actions need to implement.
 */
public interface PlayCardAction extends Action {

    /**
     * Gets the played card.
     * @return the played card
     */
    PhysicalCard getPlayedCard();

    /**
     * Gets the other played card in cases that two cards are deployed simultaneously.
     * @return the other played card
     */
    PhysicalCard getOtherPlayedCard();

    /**
     * Gets the zone the card is being played or deployed from.
     * @return the zone
     */
    Zone getPlayingFromZone();

    /**
     * Sets the text to show on the action pop-up on the User Interface.
     * @param text the text
     */
    void setText(String text);

    /**
     * Sets if the card pile the card is played from is reshuffled.
     * @param reshuffle true if pile the card is played from is reshuffled, otherwise false
     */
    void setReshuffle(boolean reshuffle);

    /**
     * Sets that the card is to be placed out of play when played.
     * @param placeOutOfPlay true if card is to be placed out of play
     */
    void setPlaceOutOfPlay(boolean placeOutOfPlay);

    /**
     * Determines if the card is to be placed out of play when played.
     * @return true or false
     */
    boolean isToBePlacedOutOfPlay();
}
