package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.game.state.MoveAsReactState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.results.MovingUsingLandspeedResult;

import java.util.Collection;

/**
 * An effect to add cards to move simultaneously with the card(s) moving using landspeed.
 */
public class AddCardsToMoveUsingLandspeedSimultaneouslyEffect extends AbstractSuccessfulEffect {
    private Collection<PhysicalCard> _cardsToMove;
    private MovingUsingLandspeedResult _movingUsingLandspeedResult;

    /**
     * Creates an effect to add cards to move simultaneously with the card(s) moving using landspeed.
     * @param action the action performing this effect
     * @param cardsToMove the cards to add as moving using landspeed
     * @param effectResult the moving using landspeed result
     */
    public AddCardsToMoveUsingLandspeedSimultaneouslyEffect(Action action, Collection<PhysicalCard> cardsToMove, MovingUsingLandspeedResult effectResult) {
        super(action);
        _cardsToMove = cardsToMove;
        _movingUsingLandspeedResult = effectResult;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();
        MoveUsingLandspeedEffect moveUsingLandspeedEffect = (MoveUsingLandspeedEffect) _movingUsingLandspeedResult.getPreventableCardEffect();
        MoveAsReactState moveAsReactState = gameState.getMoveAsReactState();

        gameState.sendMessage(_action.getPerformingPlayer() + " adds " + GameUtils.getAppendedNames(_cardsToMove) + " as moving simultaneously with " + GameUtils.getAppendedNames(moveUsingLandspeedEffect.getCardsMoving()) + " using landspeed");
        moveUsingLandspeedEffect.addCardsToMove(_cardsToMove);
        for (PhysicalCard cardToMove : _cardsToMove) {
            if (moveAsReactState != null) {
                moveAsReactState.addCardParticipatingInReact(cardToMove);
            }
            game.getActionsEnvironment().emitEffectResult(new MovingUsingLandspeedResult(cardToMove, _movingUsingLandspeedResult.getPerformingPlayerId(), _movingUsingLandspeedResult.getMovingFrom(), _movingUsingLandspeedResult.getMovingTo(),
                    _movingUsingLandspeedResult.isReact(), _movingUsingLandspeedResult.isMoveAway(), _movingUsingLandspeedResult.getPreventableCardEffect()));
        }
    }
}
