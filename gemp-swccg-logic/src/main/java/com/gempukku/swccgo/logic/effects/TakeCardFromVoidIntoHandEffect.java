package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;

/**
 * An effect to take the specified card from void into hand.
 */
public class TakeCardFromVoidIntoHandEffect extends AbstractSuccessfulEffect {
    private String _playerId;
    private PhysicalCard _card;

    /**
     * Creates an effect that causes the specified card from void to be taken into hand.
     * @param action the action performing this effect
     * @param playerId the player to take the card into hand
     * @param card the card
     */
    public TakeCardFromVoidIntoHandEffect(Action action, String playerId, PhysicalCard card) {
        super(action);
        _playerId = playerId;
        _card = card;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        if (_card.getZone() == Zone.VOID) {
            GameState gameState = game.getGameState();
            gameState.removeCardsFromZone(Collections.singleton(_card));
            _card.setOwner(_playerId);
            gameState.addCardToZone(_card, Zone.HAND, _playerId);
            gameState.sendMessage(_playerId + " takes " + GameUtils.getCardLink(_card) + " into hand");
        }
    }
}
