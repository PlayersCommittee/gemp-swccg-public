package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

/**
 * An effect that causes the player performing the action to move a card as a regular move.
 */
public class MoveCardAsRegularMoveEffect extends AbstractSubActionEffect {
    private String _playerId;
    private PhysicalCard _cardToMove;
    private Filter _targetFilter;
    private boolean _forFree;
    private boolean _asAdditionalMove;
    private float _changeInCost;

    /**
     * Creates an effect that causes the specified card to move a card as regular move to a location accepted by the move target filter.
     * @param action the action performing this effect
     * @param performingPlayerId the performing player
     * @param cardToMove the card to move
     * @param forFree true if moving for free, otherwise false
     * @param asAdditionalMove true if the move is as an additional move, otherwise false
     * @param moveTargetFilter the move target filter
     */
    public MoveCardAsRegularMoveEffect(Action action, String performingPlayerId, PhysicalCard cardToMove, boolean forFree, boolean asAdditionalMove, Filterable moveTargetFilter) {
        super(action);
        _playerId = performingPlayerId;
        _cardToMove = cardToMove;
        _forFree = forFree;
        _changeInCost = 0;
        _asAdditionalMove = asAdditionalMove;
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
                        Action moveCardAction =  _cardToMove.getBlueprint().getRegularMoveAction(_playerId, game, _cardToMove, _forFree, _changeInCost, true, _asAdditionalMove, _targetFilter);
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
