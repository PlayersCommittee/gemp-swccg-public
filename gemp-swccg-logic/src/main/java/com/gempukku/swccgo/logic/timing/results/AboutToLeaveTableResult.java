package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.effects.PreventableCardEffect;

/**
 * This interface specifies the methods that any effect results that represent "about to leave table" must implement.
 */
public interface AboutToLeaveTableResult {

    /**
     * Gets the card about to leave table.
     * @return the card
     */
    PhysicalCard getCardAboutToLeaveTable();

    /**
     * Gets the interface that can be used to prevent the card from being removed from table.
     * @return the interface
     */
    PreventableCardEffect getPreventableCardEffect();

    /**
     * Determines if this is an all cards situation.
     * @return true or false
     */
    boolean isAllCardsSituation();
}
