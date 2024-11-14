package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.results.PlacedCardOutOfPlayFromOffTableResult;

import java.util.Collections;

/**
 * An effect to put the specified card from void out of play.
 */
public class PlaceCardFromVoidOutOfPlayEffect extends AbstractSuccessfulEffect {
    private PhysicalCard _card;

    /**
     * Creates an effect that causes the specified card from void to be placed out of play.
     * @param action the action performing this effect
     * @param card the card
     */
    public PlaceCardFromVoidOutOfPlayEffect(Action action, PhysicalCard card) {
        super(action);
        _card = card;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        if (_card.getZone() == Zone.VOID) {
            GameState gameState = game.getGameState();
            gameState.removeCardsFromZone(Collections.singleton(_card));
            gameState.addCardToZone(_card, Zone.OUT_OF_PLAY, _card.getOwner());
            gameState.sendMessage(GameUtils.getCardLink(_card) + " is placed out of play");

            // Emit effect result
            game.getActionsEnvironment().emitEffectResult(
                    new PlacedCardOutOfPlayFromOffTableResult(_action, _action.getPerformingPlayer(), _card, Zone.VOID));
        }
    }
}
