package com.gempukku.swccgo.logic.actions;

import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An interface to define the methods that game text actions need to implement.
 */
public interface GameTextAction extends Action {

    /**
     * Gets the card id of the card the game text is originally from
     * @return the card id
     */
    int getGameTextSourceCardId();

    /**
     * Gets the game text action id.
     * @return the game text action id
     */
    GameTextActionId getGameTextActionId();
}
