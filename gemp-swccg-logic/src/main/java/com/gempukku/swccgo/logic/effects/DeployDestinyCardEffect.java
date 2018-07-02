package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.DrawDestinyState;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that causes the player performing the action to deploy the just drawn destiny card.
 */
public class DeployDestinyCardEffect extends AbstractSubActionEffect {
    private boolean _forFree;
    private boolean _playActionStacked;

    /**
     * Creates an effect that causes the player performing the action to deploy the just drawn destiny card.
     * @param action the action performing this effect
     * @param forFree true if deploying for free, otherwise false
     */
    public DeployDestinyCardEffect(Action action, boolean forFree) {
        super(action);
        _forFree = forFree;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        final SubAction subAction = new SubAction(_action);

        DrawDestinyState drawDestinyState = game.getGameState().getTopDrawDestinyState();
        if (drawDestinyState != null) {
            PhysicalCard destinyCard = drawDestinyState.getDrawDestinyEffect().getDrawnDestinyCard();
            if (destinyCard != null && destinyCard.getOwner().equals(_action.getPerformingPlayer())) {
                PlayCardAction playCardAction = destinyCard.getBlueprint().getPlayCardAction(_action.getPerformingPlayer(), game, destinyCard, _action.getActionSource(), _forFree, 0, null, null, null, null, null, false, 0, Filters.any, null);
                if (playCardAction != null) {
                    subAction.appendEffect(
                            new StackActionEffect(subAction, playCardAction));
                    _playActionStacked = true;
                }
            }
        }

        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return _playActionStacked;
    }
}
