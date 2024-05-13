package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.results.ConcealedResult;
import com.gempukku.swccgo.logic.timing.results.EnslavedResult;

/**
 * An effect that causes a specified card to become 'enslaved' and be stacked on the specified card
 */
public class EnslavedEffect extends AbstractSuccessfulEffect {
    private PhysicalCard _card;
    private PhysicalCard _stackOn;

    /**
     * Creates an effect that causes a specified card to become 'enslaved' and be stacked on the specified card
     * @param action the action performing this effect
     * @param card the card to become 'enslaved'
     * @param stackOn the card to be stacked on
     */
    public EnslavedEffect(Action action, PhysicalCard card, PhysicalCard stackOn) {
        super(action);
        _card = card;
        _stackOn = stackOn;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();

        gameState.sendMessage(GameUtils.getCardLink(_card) + " 'enslaved' and stacked on " + GameUtils.getCardLink(_stackOn) + " using " + GameUtils.getCardLink(_action.getActionSource()));

        gameState.removeCardFromZone(_card);
        gameState.stackCard(_card, _stackOn, true, false, false);
        _card.setEnslavedCard(true);
        game.getActionsEnvironment().emitEffectResult(new EnslavedResult(_action.getPerformingPlayer(), _card));
    }
}
