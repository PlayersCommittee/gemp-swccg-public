package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * An effect result that is emitted when an 'artwork' card is revealed.
 */
public class ArtworkCardRevealedResult extends EffectResult {
    private PhysicalCard _physicalCard;

    /**
     * Creates an effect result that is emitted when an 'artwork' card is revealed.
     * @param physicalCard the 'artwork' card
     */
    public ArtworkCardRevealedResult(PhysicalCard physicalCard) {
        super(Type.ARTWORK_CARD_REVEALED, null);
        _physicalCard = physicalCard;
    }

    /**
     * Gets the 'artwork' card that was revealed.
     * @return the 'artwork' card
     */
    public PhysicalCard getCard() {
        return _physicalCard;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "'Artwork' card, " + GameUtils.getCardLink(_physicalCard) + ", is revealed";
    }
}
