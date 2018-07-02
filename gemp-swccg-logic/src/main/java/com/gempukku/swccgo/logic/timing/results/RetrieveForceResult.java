package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * The result triggered after a unit of Force has been retrieved.
 */
public class RetrieveForceResult extends EffectResult {
    private PhysicalCard _source;
    private int _amount;

    /**
     * Creates a result to trigger after a unit of Force has been retrieved by the player.
     * @param source the source card
     * @param playerId the player
     * @param amount the amount of Force retrieved so far as part of this Force retrieval
     */
    public RetrieveForceResult(PhysicalCard source, String playerId, int amount) {
        super(Type.RETRIEVED_FORCE, playerId);
        _source = source;
        _amount = amount;
    }

    /**
     * Gets the source card of the action.
     * @return the source card
     */
    public PhysicalCard getSourceCard() {
        return _source;
    }

    /**
     * Gets the amount of Force retrieved so far.
     * @return the amount of Force
     */
    public int getAmountOfForceRetrieved() {
        return _amount;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "Just retrieved " + getAmountOfForceRetrieved() + " Force";
    }
}
