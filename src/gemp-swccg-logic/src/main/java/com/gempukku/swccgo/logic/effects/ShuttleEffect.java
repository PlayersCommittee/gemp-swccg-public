package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.ShuttledResult;
import com.gempukku.swccgo.logic.timing.results.ShuttlingResult;

import java.util.Collections;

/**
 * An effect to move to a capital starship from an exterior site on the related system (or vice versa).
 */
public class ShuttleEffect extends AbstractSubActionEffect {
    private String _playerId;
    private PhysicalCard _cardMoved;
    private PhysicalCard _movedFrom;
    private PhysicalCard _movedTo;
    private boolean _moveAsPilot;
    private boolean _moveAsVehicle;
    private boolean _moveCompleted;

    /**
     * Creates an effect to move to a capital starship from an exterior site on the related system (or vice versa).
     * @param action the action performing this effect
     * @param cardMoved the card to move
     * @param moveTo the location or starship to move to
     * @param moveAsPilot true if moving to pilot capacity slot, otherwise false
     * @param moveAsVehicle true if moving to vehicle capacity slot, otherwise false
     */
    public ShuttleEffect(Action action, PhysicalCard cardMoved, PhysicalCard moveTo, boolean moveAsPilot, boolean moveAsVehicle) {
        super(action);
        _playerId = action.getPerformingPlayer();
        _cardMoved = cardMoved;

        if (cardMoved.getZone() == Zone.AT_LOCATION)
            _movedFrom = cardMoved.getAtLocation();
        else
            _movedFrom = cardMoved.getAttachedTo();

        _movedTo = moveTo;
        _moveAsPilot = moveAsPilot;
        _moveAsVehicle = moveAsVehicle;
    }

    @Override
    public String getText(SwccgGame game) {
        StringBuilder msgText = new StringBuilder("Shuttling ").append(GameUtils.getCardLink(_cardMoved))
                .append(" from ").append(GameUtils.getCardLink(_movedFrom)).append(" to ").append(GameUtils.getCardLink(_movedTo));
        return msgText.toString();
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
                            // Record that regular move was performed
                            game.getModifiersQuerying().regularMovePerformed(_cardMoved);
                        }
                    }
            );

            // Emit effect result that card is beginning to move
            subAction.appendEffect(
                    new TriggeringResultEffect(subAction,
                            new ShuttlingResult(_cardMoved, _playerId, _movedFrom, _movedTo)));

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
                                if (_cardMoved.getBlueprint().getCardCategory() == CardCategory.CHARACTER || _cardMoved.getBlueprint().isMovesLikeCharacter())
                                    gameState.sendMessage(_playerId + " shuttles " + GameUtils.getCardLink(_cardMoved) + " from " + GameUtils.getCardLink(_movedFrom) + " to " + GameUtils.getCardLink(_movedTo));
                                else
                                    gameState.sendMessage(_playerId + " shuttles " + GameUtils.getCardLink(_cardMoved) + " from cargo hold of " + GameUtils.getCardLink(_movedFrom) + " to " + GameUtils.getCardLink(_movedTo));

                                gameState.moveCardToLocation(_cardMoved, _movedTo);
                            }
                            else if (_cardMoved.getBlueprint().getCardCategory() == CardCategory.CHARACTER || _cardMoved.getBlueprint().isMovesLikeCharacter()) {
                                if (_moveAsPilot) {
                                    gameState.sendMessage(_playerId + " shuttles " + GameUtils.getCardLink(_cardMoved) + " from " + GameUtils.getCardLink(_movedFrom) + " to " + GameUtils.getCardLink(_movedTo) + " as pilot");
                                    gameState.moveCardToAttachedInPilotCapacitySlot(_cardMoved, _movedTo);
                                }
                                else {
                                    gameState.sendMessage(_playerId + " shuttles " + GameUtils.getCardLink(_cardMoved) + " from " + GameUtils.getCardLink(_movedFrom) + " to " + GameUtils.getCardLink(_movedTo) + " as passenger");
                                    gameState.moveCardToAttachedInPassengerCapacitySlot(_cardMoved, _movedTo);
                                }
                            }
                            else {
                                gameState.sendMessage(_playerId + " shuttles " + GameUtils.getCardLink(_cardMoved) + " from " + GameUtils.getCardLink(_movedFrom) + " into cargo hold of " + GameUtils.getCardLink(_movedTo));
                                if (_moveAsVehicle)
                                    gameState.moveCardToAttachedInVehicleCapacitySlot(_cardMoved, _movedTo);
                                else if (Filters.capital_starship.accepts(game, _cardMoved))
                                    gameState.moveCardToAttachedInCapitalStarshipCapacitySlot(_cardMoved, _movedTo);
                                else
                                    gameState.moveCardToAttachedInStarfighterOrTIECapacitySlot(_cardMoved, _movedTo);
                            }
                            _moveCompleted = true;

                            // Emit effect result
                            game.getActionsEnvironment().emitEffectResult(new ShuttledResult(Collections.singletonList(_cardMoved), _playerId, _movedFrom, _movedTo));
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
