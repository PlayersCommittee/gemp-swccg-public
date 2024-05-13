package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * An effect result that is emitted when an epic event total is being calculated.
 */
public class CalculatingEpicEventTotalResult extends EffectResult {
    private PhysicalCard _epicEvent;

    /**
     * Creates the effect result that is emitted when an epic event total is being calculated.
     * @param performingPlayerId the player performing the action
     * @param epicEvent the epic event
     */
    public CalculatingEpicEventTotalResult(String performingPlayerId, PhysicalCard epicEvent) {
        super(Type.CALCULATING_EPIC_EVENT_TOTAL, performingPlayerId);
        _epicEvent = epicEvent;
    }

    /**
     * Gets the Epic Event card.
     * @return the card
     */
    public PhysicalCard getEpicEvent() {
        return _epicEvent;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "Calculating " + GameUtils.getCardLink(_epicEvent) + " total";
    }
}
