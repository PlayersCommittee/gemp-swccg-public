package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.effects.StackActionEffect;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

/**
 * An effect that causes the player performing the action to move a card using landspeed.
 */
public class MoveCardUsingLandspeedEffect extends AbstractSubActionEffect {
    private String _playerId;
    private PhysicalCard _cardToMove;
    private Filter _targetFilter;
    private boolean _forFree;
    private float _changeInCost;

    /**
     * Creates an effect that causes the specified card to move using landspeed to a location accepted by the move target filter.
     * @param action the action performing this effect
     * @param performingPlayerId the performing player
     * @param cardToMove the card to move
     * @param forFree true if moving for free, otherwise false
     * @param moveTargetFilter the move target filter
     */
    public MoveCardUsingLandspeedEffect(Action action, String performingPlayerId, PhysicalCard cardToMove, boolean forFree, Filterable moveTargetFilter) {
        super(action);
        _playerId = performingPlayerId;
        _cardToMove = cardToMove;
        _forFree = forFree;
        _changeInCost = 0;
        _targetFilter = Filters.and(moveTargetFilter);
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(final SwccgGame game) {
        final SubAction subAction = new SubAction(_action, _playerId);
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        Action moveCardAction =  _cardToMove.getBlueprint().getMoveUsingLandspeedAction(_playerId, game, _cardToMove, _forFree, _changeInCost, false, false, true, false, null, _targetFilter);
                        subAction.appendEffect(
                                new StackActionEffect(subAction, moveCardAction));
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
