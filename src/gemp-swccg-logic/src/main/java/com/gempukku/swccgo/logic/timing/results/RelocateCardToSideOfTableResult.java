package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * An effect result that is emitted when a card from table is relocated to the side of table.
 */
public class RelocateCardToSideOfTableResult extends EffectResult {
    private PhysicalCard _cardRelocated;

    /**
     * Creates an effect result that is emitted when a card from table is relocated to the side of table.
     * @param performingPlayerId the performing player
     * @param cardRelocated the card relocated to side of table
     */
    public RelocateCardToSideOfTableResult(String performingPlayerId, PhysicalCard cardRelocated) {
        super(Type.RELOCATE_TO_SIDE_OF_TABLE, performingPlayerId);
        _cardRelocated = cardRelocated;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "Relocated " + GameUtils.getCardLink(_cardRelocated) + " to side of table";
    }
}
