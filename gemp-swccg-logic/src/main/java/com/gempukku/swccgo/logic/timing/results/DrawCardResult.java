package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * The effect result to emit when a card is drawn from a card pile.
 */
public class DrawCardResult extends EffectResult {
    private Zone _cardPile;

    /**
     * Creates an effect result to emit when a card is drawn from a card pile.
     * @param performingPlayerId the performing player
     * @param cardPile the card pile the card was drawn from
     */
    public DrawCardResult(String performingPlayerId, Zone cardPile) {
        super(Type.DRAW_CARD, performingPlayerId);
        _cardPile = cardPile;
    }

    /**
     * Gets the card pile the card was drawn from.
     * @return the card pile
     */
    public Zone getCardPile() {
        return _cardPile;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "Drew card from " + getCardPile().getHumanReadable();
    }
}
