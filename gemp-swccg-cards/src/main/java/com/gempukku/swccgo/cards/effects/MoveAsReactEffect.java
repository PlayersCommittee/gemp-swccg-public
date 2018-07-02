package com.gempukku.swccgo.cards.effects;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.ReactActionOption;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.effects.StackActionEffect;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

/**
 * An effect that performs a move as a 'react'.
 */
public class MoveAsReactEffect extends AbstractSubActionEffect {
    private PhysicalCard _cardToMove;
    private boolean _forFree;

    /**
     * Creates an effect that performs a move as a 'react'.
     * @param action the action performing this effect
     * @param cardToMove the card to move as a 'react'
     * @param forFree true if the movement is to be free, otherwise false
     */
    public MoveAsReactEffect(Action action, PhysicalCard cardToMove, boolean forFree) {
        super(action);
        _cardToMove = cardToMove;
        _forFree = forFree;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(final SwccgGame game) {

        final SubAction subAction = new SubAction(_action);
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        ReactActionOption reactActionOption = new ReactActionOption(_action.getActionSource(), _forFree, 0, false, "Move as a 'react'", _cardToMove, Filters.any, null, false);
                        Action moveAsReactAction = _cardToMove.getBlueprint().getMoveAsReactAction(_cardToMove.getOwner(), game,
                                _cardToMove, reactActionOption, reactActionOption.getTargetFilter());
                        if (moveAsReactAction != null) {
                            subAction.appendEffect(
                                    new StackActionEffect(subAction, moveAsReactAction));
                        }
                    }
                }
        );

        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }
}