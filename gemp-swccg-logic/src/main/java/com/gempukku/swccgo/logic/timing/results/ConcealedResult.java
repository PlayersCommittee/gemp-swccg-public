package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * The effect result that is emitted when a card becomes 'concealed'.
 */
public class ConcealedResult extends EffectResult {
    private PhysicalCard _concealedCard;

    /**
     * Creates an effect result that is emitted when a card becomes 'concealed'.
     * @param performingPlayerId the performing player
     * @param concealedCard the concealed card
     */
    public ConcealedResult(String performingPlayerId, PhysicalCard concealedCard) {
        super(Type.CONCEALED, performingPlayerId);
        _concealedCard = concealedCard;
    }

    /**
     * Gets the card that was 'concealed'.
     * @return the card
     */
    public PhysicalCard getConcealedCard() {
        return _concealedCard;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "'Concealed' " + GameUtils.getCardLink(_concealedCard);
    }
}
