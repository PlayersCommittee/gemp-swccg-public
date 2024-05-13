package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * This effect result is triggered when a 'coaxium' card is removed and put in another zone.
 */
public class RemovedCoaxiumCardResult extends EffectResult {
    private PhysicalCard _card;
    private String _cardPileOwner;
    private Zone _cardPile;

    /**
     * Creates an effect result is triggered when a 'coaxium' card is removed and put in another zone.
     *
     * @param performingPlayerId the performing player
     * @param card               the card
     * @param cardPileOwner      the card pile owner
     * @param cardPile           the card pile
     */
    public RemovedCoaxiumCardResult(String performingPlayerId, PhysicalCard card, String cardPileOwner, Zone cardPile) {
        super(Type.REMOVED_COAXIUM_CARD, performingPlayerId);
        _card = card;
        _cardPileOwner = cardPileOwner;
        _cardPile = cardPile;
    }

    /**
     * The card pile owner.
     *
     * @return the card
     */
    public String getCardPileOwner() {
        return _cardPileOwner;
    }

    /**
     * The card placed in card pile.
     *
     * @return the card
     */
    public PhysicalCard getCard() {
        return _card;
    }


    /**
     * Gets the card pile that the card that was not on the table (e.g. in a card pile, in hand, etc.) was place in.
     *
     * @return the card pile
     */
    public Zone getCardPile() {
        return _cardPile;
    }

    /**
     * Gets the text to show to describe the effect result.
     *
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "Placed 'coaxium' card in " + getCardPile().getHumanReadable();
    }
}
