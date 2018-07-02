package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * This effect result is triggered after a player looked at the cards in own card pile.
 */
public class LookedAtCardsInOwnCardPileResult extends EffectResult {
    private Zone _cardPile;

    /**
     * Creates an effect result that is triggered after a player looked at the cards in own card pile.
     * @param playerId the player
     * @param cardPile the card pile
     */
    public LookedAtCardsInOwnCardPileResult(String playerId, Zone cardPile) {
        super(Type.LOOKED_AT_OWN_CARD_PILE, playerId);
        _cardPile = cardPile;
    }

    /**
     * Gets the card pile.
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
        return getPerformingPlayerId() + " just looked at cards in own " + getCardPile().getHumanReadable();
    }
}
