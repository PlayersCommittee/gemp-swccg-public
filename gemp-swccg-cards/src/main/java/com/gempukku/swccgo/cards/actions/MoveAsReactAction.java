package com.gempukku.swccgo.cards.actions;

import com.gempukku.swccgo.cards.effects.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.ReactActionOption;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.game.state.MoveAsReactState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.AbstractGameTextAction;
import com.gempukku.swccgo.logic.effects.StackActionEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

/**
 * Performs the steps of moving a card as a 'react'.
 */
public class MoveAsReactAction extends AbstractGameTextAction {
    private PhysicalCard _cardToReact;
    private Effect.Type _movementType;
    private ReactActionOption _reactActionOption;
    private boolean _reactAway;
    private PhysicalCard _locationToMoveTo;
    private boolean _reactStarted;
    private boolean _useForceCostApplied;
    private boolean _addedReactSteps;
    private MoveAsReactAction _that;

    /**
     * Creates the action that performs the steps of moving a card as a 'react'.
     * @param playerId the player performing the action
     * @param reactingCard the card to move as a 'react'
     * @param reactActionOption a 'react' action option
     * @param movementType the movement type
     * @param moveTargetFilter the filter for locations to move to a 'react'
     */
    public MoveAsReactAction(final String playerId, final PhysicalCard reactingCard, final ReactActionOption reactActionOption, final Effect.Type movementType, final Filter moveTargetFilter) {
        super(reactingCard, playerId, reactingCard.getCardId());
        _cardToReact = reactingCard;
        _movementType = movementType;
        _reactActionOption = reactActionOption;
        _reactAway = reactActionOption.isReactAway();
        _that = this;

        appendTargeting(
                new ChooseCardOnTableEffect(_that, playerId, "Choose where to move " + GameUtils.getCardLink(reactingCard) + (_reactAway ? " away" : "") + " as a 'react'", moveTargetFilter) {
                    @Override
                    protected void cardSelected(PhysicalCard selectedCard) {
                        _locationToMoveTo = selectedCard;
                    }
                }
        );
    }

    @Override
    public String getText() {
        String awayText = _reactAway ? "away " : "";

        if (_movementType == Effect.Type.MOVING_AS_REACT_USING_LANDSPEED) {
            return "Move " + awayText + "using landspeed as a 'react'";
        }
        if (_movementType == Effect.Type.MOVING_AS_REACT_USING_HYPERSPEED) {
            return "Move " + awayText + "using hyperspeed as a 'react'";
        }
        if (_movementType == Effect.Type.MOVING_AS_REACT_WITHOUT_USING_HYPERSPEED) {
            return "Move " + awayText + "without using hyperspeed as a 'react'";
        }
        if (_movementType == Effect.Type.MOVING_AS_REACT_USING_SECTOR_MOVEMENT) {
            return "Move " + awayText + "using sector movement as a 'react'";
        }
        if (_movementType == Effect.Type.LANDING_AS_REACT) {
            return "Land " + awayText + "as a 'react'";
        }
        if (_movementType == Effect.Type.TAKING_OFF_AS_REACT) {
            return "Take off " + awayText + "as a 'react'";
        }
        return "Move as a 'react";
    }

    @Override
    public Effect nextEffect(SwccgGame game) {
        final GameState gameState = game.getGameState();

        // Verify no costs have failed
        if (!isAnyCostFailed()) {

            Effect cost = getNextCost();
            if (cost != null)
                return cost;

            // Check if react as started
            if (!_reactStarted) {
                _reactStarted = true;

                // Update game state that "move as react" action is in progress
                gameState.beginMoveAsReact(_cardToReact, _reactActionOption);
                gameState.sendMessage(getPerformingPlayer() + " will move " + GameUtils.getCardLink(_cardToReact) + (_reactAway ? " away" : "") + " to " + GameUtils.getCardLink(_locationToMoveTo) + " as a 'react'");
                gameState.activatedCard(getPerformingPlayer(), _cardToReact);
            }

            // Pay movement cost
            if (!_useForceCostApplied) {
                _useForceCostApplied = true;
                boolean forFree = _reactActionOption.isForFree() || (_reactActionOption.getForFreeCardFilter() != null && _reactActionOption.getForFreeCardFilter().accepts(game, _cardToReact));
                if (!forFree) {
                    float changeInCost = _reactActionOption.getChangeInCost();

                    if (_movementType == Effect.Type.MOVING_AS_REACT_USING_LANDSPEED) {
                        appendCost(new PayMoveUsingLandspeedCostEffect(_that, getPerformingPlayer(), _cardToReact, _locationToMoveTo, true, changeInCost));
                        return getNextCost();
                    }
                    if (_movementType == Effect.Type.MOVING_AS_REACT_USING_HYPERSPEED) {
                        appendCost(new PayMoveUsingHyperspeedCostEffect(_that, getPerformingPlayer(), _cardToReact, _locationToMoveTo, true, changeInCost));
                        return getNextCost();
                    }
                    if (_movementType == Effect.Type.MOVING_AS_REACT_WITHOUT_USING_HYPERSPEED) {
                        appendCost(new PayMoveWithoutUsingHyperspeedCostEffect(_that, getPerformingPlayer(), _cardToReact, _locationToMoveTo, true, changeInCost));
                        return getNextCost();
                    }
                    if (_movementType == Effect.Type.MOVING_AS_REACT_USING_SECTOR_MOVEMENT) {
                        appendCost(new PayMoveUsingSectorMovementCostEffect(_that, getPerformingPlayer(), _cardToReact, _locationToMoveTo, true, changeInCost));
                        return getNextCost();
                    }
                    if (_movementType == Effect.Type.LANDING_AS_REACT) {
                        appendCost(new PayLandingCostEffect(_that, getPerformingPlayer(), _cardToReact, _locationToMoveTo, true, changeInCost));
                        return getNextCost();
                    }
                    if (_movementType == Effect.Type.TAKING_OFF_AS_REACT) {
                        appendCost(new PayTakeOffCostEffect(_that, getPerformingPlayer(), _cardToReact, _locationToMoveTo, true, changeInCost));
                        return getNextCost();
                    }
                }
            }

            if (!_addedReactSteps) {
                _addedReactSteps = true;

                // Step 1: Other cards may embark on the reacting card
                // and the reacting card itself may disembark from other cards
                appendEffect(
                        new PassthruEffect(_that) {
                            @Override
                            protected void doPlayEffect(SwccgGame game) {
                                MoveAsReactState moveAsReactState = gameState.getMoveAsReactState();

                                if (moveAsReactState != null && moveAsReactState.canContinue()) {
                                    moveAsReactState.reachedPreMovements();
                                    insertEffect(
                                            new PerformMoveAsReactEmbarkingAndDisembarkingEffect(_that));
                                }
                            }
                        }
                );

                // Step 2: The reacting card make a regular move to the destination
                appendEffect(
                        new PassthruEffect(_that) {
                            @Override
                            protected void doPlayEffect(SwccgGame game) {
                                MoveAsReactState moveAsReactState = gameState.getMoveAsReactState();

                                if (moveAsReactState != null && moveAsReactState.canContinue()) {
                                    moveAsReactState.reachedRegularMovement();
                                    Action regularMoveAction = getMoveAsReactRegularMoveAction(game);
                                    if (regularMoveAction != null) {
                                        insertEffect(
                                                new StackActionEffect(_that, regularMoveAction));
                                    }
                                }
                            }
                        }
                );

                // Step 3: Other cards may only "disembark" from the reacting card
                // and the reacting card itself may embark and disembark from other cards
                appendEffect(
                        new PassthruEffect(_that) {
                            @Override
                            protected void doPlayEffect(SwccgGame game) {
                                MoveAsReactState moveAsReactState = gameState.getMoveAsReactState();

                                if (moveAsReactState != null && moveAsReactState.canContinue()) {
                                    moveAsReactState.reachedPostMovements();
                                    insertEffect(
                                            new PerformMoveAsReactEmbarkingAndDisembarkingEffect(_that));
                                }
                            }
                        }
                );
            }

            Effect effect = getNextEffect();
            if (effect != null)
                return effect;
        }

        // Update game state that "move as react" action is no longer in progress
        if (_reactStarted) {
            gameState.finishMoveAsReact();
        }

        return null;
    }

    /**
     * Gets the regular move action to be performed during this 'react' that is currently valid to perform. Since the
     * cost of performing the 'react' as already been paid, the checking here specifies free movement.
     * @param game the game
     * @return the regular move action
     */
    public Action getMoveAsReactRegularMoveAction(SwccgGame game) {
        // The card to 'react' must be at the location card (and not attached to anything else), so it will need to
        // disembark before the regular move can be performed.
        if (_cardToReact.getAtLocation() == null) {
            return null;
        }
        String playerId = getPerformingPlayer();
        Filter moveTargetFilter = Filters.sameCardId(_locationToMoveTo);

        if (_movementType == Effect.Type.MOVING_AS_REACT_USING_LANDSPEED) {
            return _cardToReact.getBlueprint().getMoveUsingLandspeedAction(playerId, game, _cardToReact, true, 0, true, _reactAway, true, false, null, moveTargetFilter);
        }
        if (_movementType == Effect.Type.MOVING_AS_REACT_USING_HYPERSPEED) {
            return _cardToReact.getBlueprint().getMoveUsingHyperspeedAction(playerId, game, _cardToReact, true, true, _reactAway, true, false, moveTargetFilter);
        }
        if (_movementType == Effect.Type.MOVING_AS_REACT_WITHOUT_USING_HYPERSPEED) {
            return _cardToReact.getBlueprint().getMoveWithoutUsingHyperspeedAction(playerId, game, _cardToReact, true, true, _reactAway, true, false, moveTargetFilter);
        }
        if (_movementType == Effect.Type.MOVING_AS_REACT_USING_SECTOR_MOVEMENT) {
            return _cardToReact.getBlueprint().getMoveUsingSectorMovementAction(playerId, game, _cardToReact, true, true, _reactAway, true, false, moveTargetFilter);
        }
        if (_movementType == Effect.Type.LANDING_AS_REACT) {
            return _cardToReact.getBlueprint().getLandAction(playerId, game, _cardToReact, true, true, true, false, moveTargetFilter);
        }
        if (_movementType == Effect.Type.TAKING_OFF_AS_REACT) {
            return _cardToReact.getBlueprint().getTakeOffAction(playerId, game, _cardToReact, true, true, true, false, moveTargetFilter);
        }
        return null;
    }

    @Override
    public Type getType() {
        return Type.GAME_TEXT_MOVE_AS_REACT;
    }
}
