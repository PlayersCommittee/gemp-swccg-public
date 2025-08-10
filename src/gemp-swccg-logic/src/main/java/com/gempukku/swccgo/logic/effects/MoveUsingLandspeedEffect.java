package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.MovedUsingLandspeedResult;
import com.gempukku.swccgo.logic.timing.results.MovingUsingLandspeedResult;

import java.util.*;

/**
 * An effect to move a card using landspeed.
 */
public class MoveUsingLandspeedEffect extends AbstractSubActionEffect implements MovingAsReactEffect, PreventableCardEffect {
    private String _playerId;
    private List<PhysicalCard> _movedCards = new ArrayList<PhysicalCard>();
    private PhysicalCard _movedFrom;
    private PhysicalCard _movedTo;
    private boolean _asReact;
    private boolean _asMoveAway;
    private List<PhysicalCard> _locationsAlongPath = new ArrayList<PhysicalCard>();
    private boolean _initialMove;
    private boolean _moveCompleted;
    private Set<PhysicalCard> _preventedCards = new HashSet<PhysicalCard>();
    private MoveUsingLandspeedEffect _that;

    /**
     * Creates an effect to move a card using landspeed.
     * @param action the action performing this effect
     * @param cardMoved the card to move
     * @param moveFrom the site to move from
     * @param moveTo the site to move to
     * @param asReact true if moving as a 'react', otherwise false
     * @param asMoveAway true if moving as a move away, otherwise false
     */
    public MoveUsingLandspeedEffect(Action action, PhysicalCard cardMoved, PhysicalCard moveFrom, PhysicalCard moveTo, boolean asReact, boolean asMoveAway) {
        super(action);
        _playerId = action.getPerformingPlayer();
        _movedCards.add(cardMoved);
        _movedFrom = moveFrom;
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
        return _movedCards;
    }

    /**
     * Adds additional cards to move simultaneously.
     * @param cardsToMove additional cards to move
     */
    public void addCardsToMove(Collection<PhysicalCard> cardsToMove) {
        _movedCards.addAll(cardsToMove);
    }

    /**
     * Prevents the specified card from being affected by the effect.
     * @param card the card
     */
    @Override
    public void preventEffectOnCard(PhysicalCard card) {
        _preventedCards.add(card);
    }

    /**
     * Determines if the specified card was prevented from being affected by the effect.
     * @param card the card
     * @return true or false
     */
    @Override
    public boolean isEffectOnCardPrevented(PhysicalCard card) {
        return _preventedCards.contains(card);
    }

    @Override
    public Effect.Type getType() {
        return _asReact ? Type.MOVING_AS_REACT_USING_LANDSPEED : null;
    }

    @Override
    public String getText(SwccgGame game) {
        StringBuilder msgText = new StringBuilder("Moving ");
        if (_asMoveAway) {
            msgText.append("away ");
        }
        msgText.append(GameUtils.getAppendedNames(_movedCards))
                .append(" from ").append(GameUtils.getCardLink(_movedFrom)).append(" to ").append(GameUtils.getCardLink(_movedTo))
                .append(" using landspeed");
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
        if (Filters.in_play.accepts(gameState, modifiersQuerying, _movedCards.get(0))
                && (!_asReact || gameState.getMoveAsReactState().canContinue())) {

            subAction.appendEffect(
                    new PassthruEffect(subAction) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            _locationsAlongPath.add(_movedFrom);
                            List<PhysicalCard> sitesBetween = modifiersQuerying.getSitesBetween(gameState, _movedFrom, _movedTo);
                            if (sitesBetween != null) {
                                _locationsAlongPath.addAll(sitesBetween);
                            }
                            _locationsAlongPath.add(_movedTo);
                        }
                    });
            subAction.appendEffect(
                    new PassthruEffect(subAction) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            // Record that regular move was performed
                            for (PhysicalCard movedCard : _movedCards) {
                                game.getModifiersQuerying().regularMovePerformed(movedCard);
                            }
                        }
                    }
            );
            subAction.appendEffect(
                    new MoveUsingLandspeedSubEffect(subAction));
        }

        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return _moveCompleted;
    }

    /**
     * A private effect for moving a card to the next site using landspeed.
     */
    private class MoveUsingLandspeedSubEffect extends AbstractSubActionEffect {
        private SubAction _subAction;

        /**
         * Creates an effect for moving a card to the next site using landspeed.
         * @param subAction the action
         */
        public MoveUsingLandspeedSubEffect(SubAction subAction) {
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

            // Emit effect result that card is beginning to move
            subAction.appendEffect(
                    new PassthruEffect(subAction) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            PhysicalCard currentLocation = _movedCards.get(0).getAtLocation();
                            PhysicalCard nextLocation = _locationsAlongPath.get(_locationsAlongPath.indexOf(currentLocation) + 1);

                            // Check that cards are still in play and may still move
                            for (PhysicalCard cardMoving : _movedCards) {
                                if (cardMoving.getAtLocation() == null) {
                                    return;
                                }
                                if (!_preventedCards.isEmpty()) {
                                    return;
                                }
                                if (modifiersQuerying.mayNotMove(gameState, cardMoving)
                                        || modifiersQuerying.mayNotMoveFromLocationToLocationUsingLandspeed(gameState, cardMoving, currentLocation, nextLocation, _asReact)) {
                                    return;
                                }
                                if (cardMoving.getBlueprint().getCardCategory() == CardCategory.VEHICLE
                                        && !modifiersQuerying.isPiloted(gameState, cardMoving, false)) {
                                    return;
                                }
                            }

                            _initialMove = currentLocation.getCardId() == _movedFrom.getCardId();
                            _moveCompleted = nextLocation.getCardId() == _movedTo.getCardId();

                            if (_initialMove) {
                                // Emit effect results when moving from initial location
                                for (PhysicalCard cardMoving : _movedCards) {
                                    game.getActionsEnvironment().emitEffectResult(new MovingUsingLandspeedResult(cardMoving, _playerId, currentLocation, nextLocation, _asReact, _asMoveAway, _that));
                                }
                            }

                            subAction.appendEffect(
                                    new PassthruEffect(subAction) {
                                        @Override
                                        protected void doPlayEffect(SwccgGame game) {
                                            PhysicalCard currentLocation = _movedCards.get(0).getAtLocation();
                                            _initialMove = currentLocation.getCardId() == _movedFrom.getCardId();

                                            PhysicalCard nextLocation = _locationsAlongPath.get(_locationsAlongPath.indexOf(currentLocation) + 1);
                                            _moveCompleted = nextLocation.getCardId() == _movedTo.getCardId();

                                            // Check that cards are still in play and may still move
                                            for (PhysicalCard cardMoving : _movedCards) {
                                                if (cardMoving.getAtLocation() == null) {
                                                    return;
                                                }

                                                // Record that regular move was performed (in case it was added to move later)
                                                modifiersQuerying.regularMovePerformed(cardMoving);

                                                if (!_preventedCards.isEmpty()) {
                                                    return;
                                                }
                                                if (modifiersQuerying.mayNotMove(gameState, cardMoving)
                                                        || modifiersQuerying.mayNotMoveFromLocationToLocationUsingLandspeed(gameState, cardMoving, currentLocation, nextLocation, _asReact)) {
                                                    return;
                                                }
                                                if (cardMoving.getBlueprint().getCardCategory() == CardCategory.VEHICLE
                                                        && !modifiersQuerying.isPiloted(gameState, cardMoving, false)) {
                                                    return;
                                                }
                                            }

                                            // Send message
                                            StringBuilder msgText = new StringBuilder(_playerId).append(" moves ");
                                            if (_asMoveAway) {
                                                msgText.append("away ");
                                            }
                                            msgText.append(GameUtils.getAppendedNames(_movedCards))
                                                    .append(" from ").append(GameUtils.getCardLink(currentLocation)).append(" to ").append(GameUtils.getCardLink(nextLocation))
                                                    .append(" using landspeed");
                                            if (!_moveCompleted) {
                                                msgText.append(" toward ").append(GameUtils.getCardLink(_movedTo));
                                            }
                                            if (_asReact) {
                                                msgText.append(" as a 'react'");
                                            }
                                            gameState.sendMessage(msgText.toString());

                                            // Move card to next site
                                            for (PhysicalCard cardMoved : _movedCards) {
                                                gameState.moveCardToLocation(cardMoved, nextLocation);

                                                // Emit effect result
                                                game.getActionsEnvironment().emitEffectResult(new MovedUsingLandspeedResult(cardMoved, _playerId, currentLocation, nextLocation, _locationsAlongPath, _asReact, _initialMove, _moveCompleted));
                                            }

                                            // Move again if not complete
                                            if (!_moveCompleted) {
                                                _subAction.insertEffect(
                                                        new MoveUsingLandspeedSubEffect(_subAction));
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
