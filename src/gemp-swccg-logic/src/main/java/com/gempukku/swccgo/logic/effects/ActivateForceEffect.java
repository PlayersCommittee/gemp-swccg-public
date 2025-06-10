package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.*;
import com.gempukku.swccgo.logic.timing.results.ActivatedForceResult;


/**
 * An that causes the specified player to activate a specified amount of Force.
 */
public class ActivateForceEffect extends AbstractSubActionEffect {
    private String _playerId;
    private int _count;
    private boolean _fromForceGeneration;
    private int _amountActivated;

    /**
     * Creates an effect the specified player to activate a specified amount of Force.
     *
     * @param action the action performing this effect
     * @param playerId the player to activate Force
     * @param count the amount of Force to activate
     */
    public ActivateForceEffect(Action action, String playerId, int count) {
        this(action, playerId, count, false);
    }

    /**
     * Creates an effect the specified player to activate a specified amount of Force.
     *
     * @param action the action performing this effect
     * @param playerId the player to activate Force
     * @param count the amount of Force to activate
     * @param fromForceGeneration the Force activated is from Force generation
     */
    public ActivateForceEffect(Action action, String playerId, int count, boolean fromForceGeneration) {
        super(action);
        _playerId = playerId;
        _count = count;
        _fromForceGeneration = fromForceGeneration;
    }

    @Override
    public String getText(SwccgGame game) {
        return "Activate " + _count + " Force";
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return (game.getGameState().getReserveDeckSize(_playerId) >= _count) && !game.getModifiersQuerying().isActivatingForceProhibited(game.getGameState(), _playerId);
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        final SubAction subAction = new SubAction(_action);
        if (_count > 0) {
            // Activate each unit of Force
            subAction.appendEffect(getActivateNextForceEffect(subAction, game));
        }
        return subAction;
    }

    /**
     * Gets an effect to activate the next unit of Force.
     * @param subAction the subAction
     * @param game the game
     * @return the effect
     */
    private StandardEffect getActivateNextForceEffect(final SubAction subAction, SwccgGame game) {
        final GameState gameState = game.getGameState();
        final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

        return new PassthruEffect(subAction) {
            @Override
            protected void doPlayEffect(SwccgGame game) {
                if (modifiersQuerying.isActivatingForceProhibited(gameState, _playerId))
                    return;

                final int reserveDeckSize = gameState.getReserveDeckSize(_playerId);
                if (reserveDeckSize < 1)
                    return;

                // Activate the one unit of Force
                final SubAction activateOneForceSubAction = new SubAction(subAction);
                activateOneForceSubAction.appendEffect(
                        new PassthruEffect(activateOneForceSubAction) {
                            @Override
                            protected void doPlayEffect(SwccgGame game) {
                                activateOneForceSubAction.appendEffect(
                                        new ActivateOneForceEffect(activateOneForceSubAction, _playerId, _fromForceGeneration, _amountActivated == 0, reserveDeckSize == 1 || (_amountActivated == (_count - 1))));
                            }
                        }
                );
                subAction.stackSubAction(activateOneForceSubAction);

                // Check if activation of additional Force can continue
                subAction.appendEffect(
                        new PassthruEffect(subAction) {
                                @Override
                                protected void doPlayEffect(SwccgGame game) {
                                    if (activateOneForceSubAction.wasCarriedOut()) {

                                        // Check if there is more Force to activate
                                        // (If this is from Force generation also check if 'insert' card has been revealed)
                                        if ((_amountActivated < _count) && (!_fromForceGeneration || !gameState.isInsertCardFound())) {
                                            subAction.appendEffect(
                                                    getActivateNextForceEffect(subAction, game));
                                        }
                                    }
                                }
                            }
                );
            }
        };
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return _count > 0 && ((_fromForceGeneration && _amountActivated > 0) || (_count == _amountActivated));
    }

    /**
     * A private effect to activate one unit of Force.
     */
    private class ActivateOneForceEffect extends AbstractStandardEffect {
        private String _playerId;
        private boolean _fromForceGeneration;
        private boolean _firstActivated;
        private boolean _lastActivated;

        /**
         * Creates an effect to activate one unit of Force.
         * @param action the action performing this effect
         * @param playerId the player to activate Force
         * @param fromForceGeneration the Force activated is from Force generation
         * @param firstActivated true if first unit of Force activated during the ActivateForceEffect, otherwise false
         * @param lastActivated true if last unit of Force activated during the ActivateForceEffect, otherwise false
         */
        private ActivateOneForceEffect(Action action, String playerId, boolean fromForceGeneration, boolean firstActivated, boolean lastActivated) {
            super(action);
            _playerId = playerId;
            _fromForceGeneration = fromForceGeneration;
            _firstActivated = firstActivated;
            _lastActivated = lastActivated;
        }

        @Override
        public boolean isPlayableInFull(SwccgGame game) {
            return true;
        }

        @Override
        protected FullEffectResult playEffectReturningResult(SwccgGame game) {
            GameState gameState = game.getGameState();
            ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

            if (gameState.getReserveDeckSize(_playerId) < 1 || modifiersQuerying.isActivatingForceProhibited(gameState, _playerId)) {
                return new FullEffectResult(false);
            }

            PhysicalCard card = gameState.getTopOfReserveDeck(_playerId);
            gameState.playerActivatesForce(_playerId, _firstActivated, _lastActivated);
            modifiersQuerying.forceActivated(_playerId, _fromForceGeneration);
            _amountActivated++;

            String msgText = _playerId + " activated 1 Force";
            if (_fromForceGeneration) {
                int totalFromForceGeneration = modifiersQuerying.getForceActivatedThisTurn(_playerId, true);
                msgText += " (" + totalFromForceGeneration + " total Force activated from Force generation)";
            }
            else {
                msgText += " (" + _amountActivated + " total Force activated)";
            }
            gameState.sendMessage(msgText);

            // Emit effect result
            game.getActionsEnvironment().emitEffectResult(new ActivatedForceResult(card, _fromForceGeneration));
            return new FullEffectResult(true);
        }
    }
}
