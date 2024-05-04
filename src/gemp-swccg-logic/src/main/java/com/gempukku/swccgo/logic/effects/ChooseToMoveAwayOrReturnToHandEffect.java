package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that causes the specified player to choose to either move away a card (if possible) or have it be returned to hand.
 */
public class ChooseToMoveAwayOrReturnToHandEffect extends AbstractSubActionEffect {
    private final String _playerToChoose;
    private PhysicalCard _cardToMoveAway;
    private boolean _forFree;
    private Filter _validLocationToMoveAwayTo;

    /**
     * Creates an effect that causes the specified player to choose to either move away a card (if possible) or have it be returned to hand.
     * @param action the action performing this effect
     * @param playerToChoose the player to choose
     * @param cardToMoveAway the card to be moved away or lost
     * @param forFree true if moves away for free, otherwise false
     */
    public ChooseToMoveAwayOrReturnToHandEffect(Action action, String playerToChoose, PhysicalCard cardToMoveAway, boolean forFree) {
        this(action, playerToChoose, cardToMoveAway, forFree, Filters.any);
    }

    /**
     * Creates an effect that causes the specified player to choose to either move away a card (if possible) or have it be returned to hand.
     * @param action the action performing this effect
     * @param playerToChoose the player to choose
     * @param cardToMoveAway the card to be moved away or lost
     * @param forFree true if moves away for free, otherwise false
     */
    public ChooseToMoveAwayOrReturnToHandEffect(Action action, String playerToChoose, PhysicalCard cardToMoveAway, boolean forFree, Filter validLocationToMoveTo) {
        super(action);
        _playerToChoose = playerToChoose;
        _cardToMoveAway = cardToMoveAway;
        _forFree = forFree;
        _validLocationToMoveAwayTo = validLocationToMoveTo;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        final GameState gameState = game.getGameState();

        final SubAction subAction = new SubAction(_action);

        // Check if there is a valid move away action
        final Action moveAwayAction = _cardToMoveAway.getBlueprint().getMoveAwayAction(_cardToMoveAway.getOwner(), game, _cardToMoveAway, _forFree, 0, false, _validLocationToMoveAwayTo);
        if (moveAwayAction != null) {
            // There is valid move away action, so ask player to choose
            subAction.appendEffect(
                    new PlayoutDecisionEffect(subAction, _playerToChoose,
                            new YesNoDecision("Do you want to have " + GameUtils.getCardLink(_cardToMoveAway) + " move away?") {
                                @Override
                                protected void yes() {
                                    gameState.sendMessage(_playerToChoose + " chooses to move away " + GameUtils.getCardLink(_cardToMoveAway));
                                    subAction.appendEffect(
                                            new StackActionEffect(subAction, moveAwayAction));
                                }

                                @Override
                                protected void no() {
                                    gameState.sendMessage(_playerToChoose + " chooses to return " + GameUtils.getCardLink(_cardToMoveAway) + " to hand");
                                    subAction.appendEffect(
                                            new ReturnCardToHandFromTableEffect(subAction, _cardToMoveAway));
                                }
                            }));
        }
        else {
            // No valid move away action, so card is returned to hand
            subAction.appendEffect(
                    new ReturnCardToHandFromTableEffect(subAction, _cardToMoveAway));
        }

        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }
}
