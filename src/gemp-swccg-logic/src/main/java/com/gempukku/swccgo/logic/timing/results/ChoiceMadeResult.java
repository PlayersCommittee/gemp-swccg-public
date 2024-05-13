package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * This effect result is triggered when a choice was just made.
 */
public class ChoiceMadeResult extends EffectResult {
    private PhysicalCard _card;
    private String _choice;

    /**
     * Creates an effect result that is triggered when a choice was just made.
     * @param performingPlayerId the player that made the choice
     * @param choice the choice made
     * @param card the card that required the choice
     */
    public ChoiceMadeResult(String performingPlayerId, PhysicalCard card, String choice) {
        super(Type.CHOICE_MADE, performingPlayerId);
        _card = card;
        _choice = choice;
    }

    /**
     * Gets the card that required the choice
     * @return the card
     */
    public PhysicalCard getCard() {
        return _card;
    }

    /**
     * Gets the choice
     * @return the choice
     */
    public String getChoice() {
        return _choice;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "Chose " + _choice + " for " + GameUtils.getCardLink(_card);
    }
}
