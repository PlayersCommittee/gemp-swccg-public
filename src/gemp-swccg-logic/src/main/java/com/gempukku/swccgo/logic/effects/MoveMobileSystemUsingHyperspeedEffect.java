package com.gempukku.swccgo.logic.effects;

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
import com.gempukku.swccgo.logic.timing.results.MovedUsingHyperspeedResult;
import com.gempukku.swccgo.logic.timing.results.MovingUsingHyperspeedResult;

import java.util.Collection;

/**
 * An effect to move a mobile system using hyperspeed.
 */
public class MoveMobileSystemUsingHyperspeedEffect extends AbstractSubActionEffect {
    private String _playerId;
    private PhysicalCard _cardMoved;
    private int _oldParsec;
    private PhysicalCard _oldOrbit;
    private int _newParsec;
    private PhysicalCard _newOrbit;
    private boolean _moveCompleted;

    /**
     * Creates an effect to move a mobile system using hyperspeed.
     * @param action the action performing this effect
     * @param cardMoved the mobile system to move
     * @param oldParsec the parsec number to move from
     * @param oldOrbit the system orbited before moving, or null if deep space
     * @param newParsec the parsec number to move to
     * @param newOrbit the system orbited after moving, or null if deep space
     */
    public MoveMobileSystemUsingHyperspeedEffect(Action action, PhysicalCard cardMoved, int oldParsec, PhysicalCard oldOrbit, int newParsec, PhysicalCard newOrbit) {
        super(action);
        _playerId = action.getPerformingPlayer();
        _cardMoved = cardMoved;
        _oldParsec = oldParsec;
        _oldOrbit = oldOrbit;
        _newParsec = newParsec;
        _newOrbit = newOrbit;
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
        if (Filters.in_play.accepts(game, _cardMoved)) {

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
                            new MovingUsingHyperspeedResult(_cardMoved, _playerId, null, null, false, false, null)));

            subAction.appendEffect(
                    new PassthruEffect(subAction) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {

                            // Check that card is still in play and may still move
                            if (!Filters.in_play.accepts(game, _cardMoved)) {
                                return;
                            }
                            if (modifiersQuerying.mayNotMove(gameState, _cardMoved)) {
                                return;
                            }

                            String oldOrbitInfo = (_oldOrbit != null) ? (" orbiting " + GameUtils.getCardLink(_oldOrbit)) : " deep space";
                            String newOrbitInfo = (_newOrbit != null) ? (" orbiting " + GameUtils.getCardLink(_newOrbit)) : " deep space";
                            String oldParsecInfo = " at parsec " + _oldParsec;
                            String newParsecInfo = " at parsec " + _newParsec;

                            gameState.sendMessage(_playerId + " moves " + GameUtils.getCardLink(_cardMoved) + " from" + oldOrbitInfo + oldParsecInfo + " to" + newOrbitInfo + newParsecInfo);
                            _cardMoved.setParsec(_newParsec);
                            // Also update the parsec number of any mobile systems orbiting this mobile system
                            Collection<PhysicalCard> orbitingMovedMobileSystem = Filters.filterTopLocationsOnTable(game, Filters.isOrbiting(_cardMoved.getTitle()));
                            for (PhysicalCard otherOrbiting : orbitingMovedMobileSystem) {
                                otherOrbiting.setParsec(_newParsec);
                            }
                            _cardMoved.setSystemOrbited(_newOrbit != null ? _newOrbit.getTitle() : null);

                            // Animation to indicate movement of mobile system and indicate the system it orbits
                            gameState.activatedCard(_playerId, _cardMoved);
                            if (_newOrbit != null) {
                                game.getGameState().cardAffectsCard(_playerId, _cardMoved, _newOrbit);
                            }
                            _moveCompleted = true;

                            // Emit effect result
                            game.getActionsEnvironment().emitEffectResult(new MovedUsingHyperspeedResult(_cardMoved, _playerId, null, null, false));
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
