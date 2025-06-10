package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.EpicEventState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.MovedUsingSectorMovementResult;
import com.gempukku.swccgo.logic.timing.results.MovingUsingSectorMovementResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * An effect to move a starship or vehicle using sector movement.
 */
public class MoveUsingSectorMovementEffect extends AbstractSubActionEffect implements MovingAsReactEffect, PreventableCardEffect {
    private String _playerId;
    private PhysicalCard _cardMoved;
    private PhysicalCard _movedFrom;
    private PhysicalCard _movedTo;
    private boolean _asReact;
    private boolean _asMoveAway;
    private List<PhysicalCard> _locationsAlongPath = new ArrayList<PhysicalCard>();
    private boolean _initialMove;
    private boolean _moveCompleted;
    private PhysicalCard _preventedCard;
    private MoveUsingSectorMovementEffect _that;

    /**
     * Creates an effect to move a starship or vehicle using sector movement.
     * @param action the action performing this effect
     * @param cardMoved the card to move
     * @param moveTo the location to move to
     * @param asReact true if moving as a 'react', otherwise false
     * @param asMoveAway true if moving as a move away, otherwise false
     */
    public MoveUsingSectorMovementEffect(Action action, PhysicalCard cardMoved, PhysicalCard moveTo, boolean asReact, boolean asMoveAway) {
        super(action);
        _playerId = action.getPerformingPlayer();
        _cardMoved = cardMoved;
        _movedFrom = cardMoved.getAtLocation();
        _movedTo = moveTo;
        _asReact = asReact;
        _asMoveAway = asMoveAway;
        _initialMove = true;
        _that = this;
    }

    @Override
    public PhysicalCard getMovingFrom() {
        return _movedFrom;
    }

    @Override
    public Collection<PhysicalCard> getCardsMoving() {
        return Collections.singletonList(_cardMoved);
    }

    /**
     * Prevents the specified card from being affected by the effect.
     * @param card the card
     */
    @Override
    public void preventEffectOnCard(PhysicalCard card) {
        _preventedCard = card;
    }

    /**
     * Determines if the specified card was prevented from being affected by the effect.
     * @param card the card
     * @return true or false
     */
    @Override
    public boolean isEffectOnCardPrevented(PhysicalCard card) {
        return card.equals(_preventedCard);
    }

    @Override
    public Type getType() {
        return _asReact ? Type.MOVING_AS_REACT_USING_SECTOR_MOVEMENT : null;
    }

    @Override
    public String getText(SwccgGame game) {
        StringBuilder msgText = new StringBuilder("Moving ");
        if (_asMoveAway) {
            msgText.append("away ");
        }
        msgText.append(GameUtils.getCardLink(_cardMoved))
                .append(" from ").append(GameUtils.getCardLink(_movedFrom)).append(" to ").append(GameUtils.getCardLink(_movedTo));
        if (_asReact) {
            msgText.append(" as a 'react'");
        }
        return msgText.toString();
    }

    public boolean isAsReact() {
        return _asReact;
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

        // Check if card is still in play and react was not canceled
        if (Filters.in_play.accepts(gameState, modifiersQuerying, _cardMoved)
                && (!_asReact || gameState.getMoveAsReactState().canContinue())) {

            subAction.appendEffect(
                    new PassthruEffect(subAction) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            _locationsAlongPath.add(_movedFrom);
                            List<PhysicalCard> sectorsBetween = modifiersQuerying.getSectorsBetween(gameState, _movedFrom, _movedTo);
                            if (sectorsBetween != null) {
                                _locationsAlongPath.addAll(sectorsBetween);
                            }
                            _locationsAlongPath.add(_movedTo);
                        }
                    });
            subAction.appendEffect(
                    new PassthruEffect(subAction) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            // Unless during escape from Death Star II record that regular move was performed
                            if (gameState.getEpicEventState() == null || gameState.getEpicEventState().getEpicEventType() != EpicEventState.Type.ATTEMPT_TO_BLOW_AWAY_DEATH_STAR_II) {
                                game.getModifiersQuerying().regularMovePerformed(_cardMoved);
                            }
                        }
                    }
            );
            subAction.appendEffect(
                    new MovingUsingSectorMovementSubEffect(subAction));
        }

        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return _moveCompleted;
    }

    /**
     * A private effect for moving a card to the next sector (or system) using sector movement.
     */
    private class MovingUsingSectorMovementSubEffect extends AbstractSubActionEffect {
        private SubAction _subAction;

        /**
         * Creates an effect for moving a card to the next sector (or system) using sector movement.
         * @param subAction the action
         */
        public MovingUsingSectorMovementSubEffect(SubAction subAction) {
            super(subAction);
            _subAction = subAction;
        }

        @Override
        public boolean isPlayableInFull(SwccgGame game) {
            return true;
        }

        @Override
        protected SubAction getSubAction(SwccgGame game) {
            final GameState gameState = game.getGameState();
            final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

            final SubAction subAction = new SubAction(_action);

            subAction.appendEffect(
                    new PassthruEffect(subAction) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            PhysicalCard currentLocation = _cardMoved.getAtLocation();
                            PhysicalCard nextLocation = _locationsAlongPath.get(_locationsAlongPath.indexOf(currentLocation) + 1);

                            // Check that card is still in play and may still move
                            if (currentLocation == null) {
                                return;
                            }
                            if (_cardMoved.equals(_preventedCard)) {
                                return;
                            }
                            if (modifiersQuerying.mayNotMove(gameState, _cardMoved)
                                    || modifiersQuerying.mayNotMoveFromLocationToLocationUsingSectorMovement(gameState, _cardMoved, currentLocation, nextLocation, _asReact)) {
                                return;
                            }
                            if (!modifiersQuerying.isPiloted(gameState, _cardMoved, false)) {
                                return;
                            }

                            // Emit effect result that card is beginning to move
                            subAction.appendEffect(
                                    new TriggeringResultEffect(subAction,
                                            new MovingUsingSectorMovementResult(_cardMoved, _playerId, currentLocation, nextLocation, _asReact, _asMoveAway, _that)));

                            subAction.appendEffect(
                                    new PassthruEffect(subAction) {
                                        @Override
                                        protected void doPlayEffect(SwccgGame game) {
                                            PhysicalCard currentLocation = _cardMoved.getAtLocation();
                                            PhysicalCard nextLocation = _locationsAlongPath.get(_locationsAlongPath.indexOf(currentLocation) + 1);

                                            // Check that card is still in play and may still move
                                            if (currentLocation == null) {
                                                return;
                                            }
                                            if (_cardMoved.equals(_preventedCard)) {
                                                return;
                                            }
                                            if (modifiersQuerying.mayNotMove(gameState, _cardMoved)
                                                    || modifiersQuerying.mayNotMoveFromLocationToLocationUsingSectorMovement(gameState, _cardMoved, currentLocation, nextLocation, _asReact)) {
                                                return;
                                            }
                                            if (!modifiersQuerying.isPiloted(gameState, _cardMoved, false)) {
                                                return;
                                            }

                                            _initialMove = currentLocation.getCardId() == _movedFrom.getCardId();
                                            _moveCompleted = nextLocation.getCardId() == _movedTo.getCardId();

                                            // Send message
                                            StringBuilder msgText = new StringBuilder(_playerId).append(" moves ");
                                            if (_asMoveAway) {
                                                msgText.append("away ");
                                            }
                                            msgText.append(GameUtils.getCardLink(_cardMoved))
                                                    .append(" from ").append(GameUtils.getCardLink(currentLocation)).append(" to ").append(GameUtils.getCardLink(nextLocation));
                                            if (!_moveCompleted) {
                                                msgText.append(" toward ").append(GameUtils.getCardLink(_movedTo));
                                            }
                                            if (_asReact) {
                                                msgText.append(" as a 'react'");
                                            }
                                            gameState.sendMessage(msgText.toString());

                                            // Move card to next sector (or system)
                                            gameState.moveCardToLocation(_cardMoved, nextLocation);

                                            // Emit effect result
                                            game.getActionsEnvironment().emitEffectResult(new MovedUsingSectorMovementResult(_cardMoved, _playerId, currentLocation, nextLocation, _asReact, _initialMove, _moveCompleted));

                                            // Move again if not complete
                                            if (!_moveCompleted) {
                                                _subAction.insertEffect(
                                                        new MovingUsingSectorMovementSubEffect(_subAction));
                                            }
                                        }
                                    }
                            );
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
}
