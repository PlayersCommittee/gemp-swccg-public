package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.AbstractStandardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.results.RemovedCoaxiumCardResult;

import java.util.Collections;

/**
 * An effect to take the specified card into hand that is not "on table".
 */
abstract class TakeOneCardIntoHandFromOffTableEffect extends AbstractStandardEffect {
    private String _playerId;
    private PhysicalCard _card;
    private String _msgText;

    /**
     * Creates an effect that causes the specified card to be taken into hand that is not "on table".
     * @param action the action performing this effect
     * @param playerId the player
     * @param card the card
     * @param msgText the message to send
     */
    public TakeOneCardIntoHandFromOffTableEffect(Action action, String playerId, PhysicalCard card, String msgText) {
        super(action);
        _playerId = playerId;
        _card = card;
        _msgText = msgText;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected FullEffectResult playEffectReturningResult(SwccgGame game) {
        if (_card.getZone().isInPlay())
            throw new UnsupportedOperationException("TakeOneCardIntoHandFromOffTableEffect should not be used on " + GameUtils.getFullName(_card) + " on table");

        GameState gameState = game.getGameState();
        gameState.removeCardsFromZone(Collections.singleton(_card));
        _card.setOwner(_playerId);
        gameState.addCardToZone(_card, Zone.HAND, _playerId);
        gameState.sendMessage(_msgText);

        if (Filters.coaxiumCard.accepts(game, _card)) {
            game.getActionsEnvironment().emitEffectResult(
                    new RemovedCoaxiumCardResult(_playerId, _card, _playerId, Zone.HAND));
            _card.setCoaxiumCard(false);
        }

        // A callback that can be used to schedule the next card to be taken into hand
        afterCardTakenIntoHand();

        return new FullEffectResult(true);
    }

    protected abstract void afterCardTakenIntoHand();
}
