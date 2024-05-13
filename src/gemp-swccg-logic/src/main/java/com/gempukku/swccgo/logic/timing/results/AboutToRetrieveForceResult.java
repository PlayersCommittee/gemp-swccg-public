package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * This effect result is triggered during Force retrieval when the Force retrieval amount is set and player is about to retrieve.
 */
public class AboutToRetrieveForceResult extends EffectResult {
    private PhysicalCard _source;
    private float _amountToRetrieve;

    /**
     * Creates an effect result that is triggered during Force retrieval when the Force retrieval amount is set and player
     * is about to retrieve.
     * @param source the source card
     * @param performingPlayerId the player to retrieve Force
     * @param amountToRetrieve the amount of Force to retrieve
     */
    public AboutToRetrieveForceResult(PhysicalCard source, String performingPlayerId, float amountToRetrieve) {
        super(Type.FORCE_RETRIEVAL_ABOUT_TO_RETRIEVE, performingPlayerId);
        _source = source;
        _amountToRetrieve = amountToRetrieve;
    }

    /**
     * Gets the source card of the action.
     * @return the source card
     */
    public PhysicalCard getSourceCard() {
        return _source;
    }

    /**
     * Gets the amount of Force to be retrieved.
     * @return the amount
     */
    public float getAmountOfForceToRetrieve() {
        return _amountToRetrieve;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "About to retrieve " + GuiUtils.formatAsString(getAmountOfForceToRetrieve()) + " Force";
    }
}
