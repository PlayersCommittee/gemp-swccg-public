package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.game.state.MoveAsReactState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.DisembarkedResult;
import com.gempukku.swccgo.logic.timing.results.DisembarkingResult;

/**
 * An effect to disembark off of a card (or from a location).
 */
public class DisembarkEffect extends AbstractSubActionEffect {
    private String _playerId;
    private PhysicalCard _cardMoved;
    private PhysicalCard _movedFrom;
    private PhysicalCard _movedTo;
    private boolean _moveAsPilot;
    private boolean _moveAsVehicle;
    private boolean _moveCompleted;

    /**
     * Creates an effect to disembark off of a card (or from a location).
     * @param action the action performing this effect
     * @param cardMoved the card to move
     * @param moveTo the card to disembark to
     * @param moveAsPilot true if moving to pilot capacity slot, otherwise false
     * @param moveAsVehicle true if moving to vehicle capacity slot, otherwise false
     */
    public DisembarkEffect(Action action, PhysicalCard cardMoved, PhysicalCard moveTo, boolean moveAsPilot, boolean moveAsVehicle) {
        super(action);
        _playerId = action.getPerformingPlayer();
        _cardMoved = cardMoved;
        _movedFrom = cardMoved.getAttachedTo() != null ? cardMoved.getAttachedTo() : cardMoved.getAtLocation();
        _movedTo = moveTo;
        _moveAsPilot = moveAsPilot;
        _moveAsVehicle = moveAsVehicle;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        final GameState gameState = game.getGameState();
        final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

        SubAction subAction = new SubAction(_action);

        // Check if card is still in play
        if (_cardMoved.getAtLocation() != null || _cardMoved.getAttachedTo() != null) {

            subAction.appendEffect(
                    new PassthruEffect(subAction) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            MoveAsReactState moveAsReactState = gameState.getMoveAsReactState();
                            if (moveAsReactState != null) {
                                moveAsReactState.addCardParticipatingInReact(_cardMoved);
                            }
                        }
                    }
            );

            // Emit effect result that card is beginning to move
            subAction.appendEffect(
                    new TriggeringResultEffect(subAction,
                            new DisembarkingResult(_cardMoved, _playerId, _movedFrom, _movedTo)));

            subAction.appendEffect(
                    new PassthruEffect(subAction) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {

                            // Check that card is in play and may still move
                            if (_cardMoved.getAtLocation() == null && _cardMoved.getAttachedTo() == null) {
                                return;
                            }
                            if (modifiersQuerying.mayNotMove(gameState, _cardMoved)) {
                                return;
                            }

                            if (_movedTo.getBlueprint().getCardCategory() == CardCategory.LOCATION) {
                                gameState.sendMessage(_cardMoved.getOwner() + " disembarks " + GameUtils.getCardLink(_cardMoved) + " from " + GameUtils.getCardLink(_movedFrom) + " to " + GameUtils.getCardLink(_movedTo));
                                gameState.moveCardToLocation(_cardMoved, _movedTo);
                            }
                            else if (_cardMoved.getBlueprint().getCardCategory()== CardCategory.CHARACTER || _cardMoved.getBlueprint().isMovesLikeCharacter()) {
                                if (_moveAsPilot) {
                                    if (_movedTo.getBlueprint().getCardSubtype()== CardSubtype.TRANSPORT)
                                        gameState.sendMessage(_cardMoved.getOwner() + " disembarks " + GameUtils.getCardLink(_cardMoved) + " from " + GameUtils.getCardLink(_movedFrom) + " to " + GameUtils.getCardLink(_movedTo) + " as driver");
                                    else
                                        gameState.sendMessage(_cardMoved.getOwner() + " disembarks " + GameUtils.getCardLink(_cardMoved) + " from " + GameUtils.getCardLink(_movedFrom) + " to " + GameUtils.getCardLink(_movedTo) + " as pilot");
                                    game.getGameState().moveCardToAttachedInPilotCapacitySlot(_cardMoved, _movedTo);
                                }
                                else {
                                    gameState.sendMessage(_cardMoved.getOwner() + " disembarks " + GameUtils.getCardLink(_cardMoved) + " from " + GameUtils.getCardLink(_movedFrom) + " to " + GameUtils.getCardLink(_movedTo) + " as passenger");
                                    gameState.moveCardToAttachedInPassengerCapacitySlot(_cardMoved, _movedTo);
                                }
                            }
                            else {
                                if (_moveAsVehicle)
                                    gameState.moveCardToAttachedInVehicleCapacitySlot(_cardMoved, _movedTo);
                                else if (Filters.capital_starship.accepts(game, _cardMoved))
                                    gameState.moveCardToAttachedInCapitalStarshipCapacitySlot(_cardMoved, _movedTo);
                                else
                                    gameState.moveCardToAttachedInStarfighterOrTIECapacitySlot(_cardMoved, _movedTo);
                                gameState.sendMessage(_cardMoved.getOwner() + " disembarks " + GameUtils.getCardLink(_cardMoved) + " from " + GameUtils.getCardLink(_movedFrom) + " into cargo hold of " + GameUtils.getCardLink(_movedTo));
                            }
                            _moveCompleted = true;

                            // Emit effect result
                            game.getActionsEnvironment().emitEffectResult(new DisembarkedResult(_cardMoved, _playerId, _movedFrom, _movedTo));
                        }
                    }
            );
        }

        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return _moveCompleted;
    }
}
