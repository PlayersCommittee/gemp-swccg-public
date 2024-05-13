package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.PlayCardActionReason;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.StandardEffect;

/**
 * An interface to define the methods that playing/deploying card effects (for purposes of canceling, re-targeting, etc)
 * need to implement.
 */
public interface RespondablePlayingCardEffect extends StandardEffect {

    /**
     * Gets the card being played or deployed.
     * @return the card
     */
    PhysicalCard getCard();

    /**
     * Gets the zone the card is being played or deployed from.
     * @return the zone
     */
    Zone getPlayingFromZone();

    /**
     * Gets the targeting action for the playing/deployment of the card.
     * @return the targeting action
     */
    Action getTargetingAction();

    /**
     * Determines if the card is being deployed as a 'react'.
     * @return true or false
     */
    boolean isAsReact();

    /**
     * Determines if card is being deployed as an 'insert' card.
     * @return true or false
     */
    boolean isAsInsertCard();

    /**
     * Determines if the card is to be placed out of play when played.
     * @return true or false
     */
    boolean isToBePlacedOutOfPlay();

    /**
     * Determines if card is being played for the specified reason.
     * @return true or false
     */
    boolean isPlayingForReason(PlayCardActionReason actionReason);
}
