package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect to steal a card into hand from the opponent's hand.
 */
public class StealCardIntoHandFromOpponentsHand extends AbstractSubActionEffect {
    private PhysicalCard _cardToSteal;

    /**
     * Creates an effect that causes the player to steal a specified card into hand from opponent's hand.
     * @param action the action performing this effect
     * @param cardToSteal the card to steal
     */
    public StealCardIntoHandFromOpponentsHand(Action action, PhysicalCard cardToSteal) {
        super(action);
        _cardToSteal = cardToSteal;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        String opponent = game.getOpponent(_action.getPerformingPlayer());

        SubAction subAction = new SubAction(_action);

        if (Filters.inHand(opponent).accepts(game, _cardToSteal)) {
            subAction.appendEffect(
                    new StealOneCardIntoHandEffect(subAction, _cardToSteal));
        }

        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }
}
