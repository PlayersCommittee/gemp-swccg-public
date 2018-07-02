package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.results.PutCardInCardPileFromOffTableResult;

import java.util.Collections;

/**
 * An effect to put the specified card from a specified card pile into another specified card pile.
 */
public abstract class PutOneCardFromCardPileInCardPileEffect extends PutOneCardInCardPileEffect {
    private Zone _fromCardPile;

    /**
     * Creates an effect that causes the specified card from a specified card pile into another specified card pile.
     * @param action the action performing this effect
     * @param card the card
     * @param fromCardPile the card pile to take card from
     * @param toCardPile the card pile to put card in
     * @param cardPileOwner the card pile owner
     * @param bottom true if cards are to be put on the bottom of the card pile, otherwise false
     * @param msgText the message to send
     */
    public PutOneCardFromCardPileInCardPileEffect(Action action, PhysicalCard card, Zone fromCardPile, Zone toCardPile, String cardPileOwner, boolean bottom, String msgText) {
        super(action, card, toCardPile, cardPileOwner, bottom, msgText);
        _fromCardPile = fromCardPile;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        if (GameUtils.getZoneFromZoneTop(_card.getZone()) == _fromCardPile && _card.getZoneOwner().equals(_cardPileOwner)) {
            GameState gameState = game.getGameState();
            gameState.removeCardsFromZone(Collections.singleton(_card));
            _card.setOwner(_cardPileOwner);
            if (_bottom)
                gameState.addCardToZone(_card, _zone, _cardPileOwner);
            else
                gameState.addCardToTopOfZone(_card, _zone, _cardPileOwner);

            gameState.sendMessage(_msgText);

            // Emit effect result
            game.getActionsEnvironment().emitEffectResult(
                    new PutCardInCardPileFromOffTableResult(_action, _card, _cardPileOwner, _zone, false));

            // A callback that can be used after card is placed in card pile
            afterCardPutInCardPile();
        }

        // A callback that can be used to schedule the next card to be put in card pile
        scheduleNextStep();
    }

    @Override
    protected final void afterCardPutInCardPile() {
    }
}
