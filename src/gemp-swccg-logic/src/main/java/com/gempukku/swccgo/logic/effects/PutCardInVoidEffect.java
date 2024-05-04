package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;

/**
 * An effect places a card in the void (unless if is already out of play or on a grabber).
 */
public class PutCardInVoidEffect extends AbstractSuccessfulEffect {
    private PhysicalCard _card;

    /**
     * Creates an effect places a card in the void (unless if is already out of play or on a grabber).
     * @param action the action performing this effect
     * @param card the card
     */
    public PutCardInVoidEffect(Action action, PhysicalCard card) {
        super(action);
        _card = card;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        if (_card.getZone() == Zone.OUT_OF_PLAY)
            return;

        if (_card.getAttachedTo() != null && Filters.grabber.accepts(game, _card.getAttachedTo()))
            return;

        GameState gameState = game.getGameState();
        gameState.removeCardsFromZone(Collections.singleton(_card));
        gameState.addCardToZone(_card, Zone.VOID, _card.getOwner());
    }
}
