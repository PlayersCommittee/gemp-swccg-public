package com.gempukku.swccgo.logic.timing.processes.pregame;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SystemQueueAction;
import com.gempukku.swccgo.logic.decisions.ArbitraryCardsSelectionDecision;
import com.gempukku.swccgo.logic.decisions.AwaitingDecision;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.modifiers.ModifierFlag;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.processes.GameProcess;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * The game process for playing starting interrupts.
 */
public class PlayStartingInterruptsGameProcess implements GameProcess {
    private String _darkPlayerId;
    private String _lightPlayerId;
    private boolean _determinedOrder;
    private boolean _lightSideGoesFirst;
    private GameProcess _followingGameProcess;
    private GameProcess _nextProcess;
    private PhysicalCard _darkSideStartingInterrupt;
    private PhysicalCard _lightSideStartingInterrupt;
    private boolean _choicesMade;

    /**
     * Creates the game process for playing starting interrupts.
     * @param game the game
     */
    public PlayStartingInterruptsGameProcess(SwccgGame game) {
        _darkPlayerId = game.getDarkPlayer();
        _lightPlayerId = game.getLightPlayer();
        _followingGameProcess = new PlayersShuffleAndDrawStartingHandGameProcess(game);
        _nextProcess = this;
    }

    @Override
    public void process(SwccgGame game) {
        if (!_determinedOrder) {
            _determinedOrder = true;
            if (game.getModifiersQuerying().hasFlagActive(game.getGameState(), ModifierFlag.LIGHT_SIDE_GOES_FIRST)) {
                _lightSideGoesFirst = true;
            }
        }

        if (!_choicesMade) {
            // Figure out the dark side starting interrupts to choose
            Collection<PhysicalCard> darkSideChoices = Filters.filter(game.getGameState().getReserveDeck(_darkPlayerId), game, Filters.playableAsStartingInterrupt);
            if (!darkSideChoices.isEmpty()) {
                game.getUserFeedback().sendAwaitingDecision(_darkPlayerId, createChooseStartingInterruptDecision(game, _darkPlayerId, darkSideChoices));
            }

            // Figure out the light side starting interrupts to choose
            Collection<PhysicalCard> lightSideChoices = Filters.filter(game.getGameState().getReserveDeck(_lightPlayerId), game, Filters.playableAsStartingInterrupt);
            if (!lightSideChoices.isEmpty()) {
                game.getUserFeedback().sendAwaitingDecision(_lightPlayerId, createChooseStartingInterruptDecision(game, _lightPlayerId, lightSideChoices));
            }

            _choicesMade = true;
            return;
        }

        // Starting Interrupts (if any) have been selected. Play them based on the player order.
        // Note: They are added to the stack in reverse order.
        if (!_lightSideGoesFirst && _lightSideStartingInterrupt!=null) {
            game.getActionsEnvironment().addActionToStack(_lightSideStartingInterrupt.getBlueprint().getStartingInterruptAction(_lightPlayerId, game, _lightSideStartingInterrupt));
        }
        if (_darkSideStartingInterrupt != null) {
            game.getActionsEnvironment().addActionToStack(_darkSideStartingInterrupt.getBlueprint().getStartingInterruptAction(_darkPlayerId, game, _darkSideStartingInterrupt));
        }
        if (_lightSideGoesFirst && _lightSideStartingInterrupt!=null) {
            game.getActionsEnvironment().addActionToStack(_lightSideStartingInterrupt.getBlueprint().getStartingInterruptAction(_lightPlayerId, game, _lightSideStartingInterrupt));
        }

        // If either player is playing a starting interrupt, reveal any Starting Interrupts being
        // played so both players know what they are before the first player actually plays it
        // (since it may influence which Starting Interrupt actions each player wants to do).
        if (_darkSideStartingInterrupt != null || _lightSideStartingInterrupt != null) {
            SystemQueueAction action = new SystemQueueAction();
            action.appendEffect(
                    new PassthruEffect(action) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            if (_darkSideStartingInterrupt != null) {
                                game.getGameState().sendMessage(_darkPlayerId + " reveals Starting Interrupt " + GameUtils.getCardLink(_darkSideStartingInterrupt));
                                game.getGameState().showCardOnScreen(_darkSideStartingInterrupt);
                                game.getGameState().setStartingInterruptPlayed(_darkPlayerId, _darkSideStartingInterrupt);
                            }
                            if (_lightSideStartingInterrupt != null) {
                                game.getGameState().sendMessage(_lightPlayerId + " reveals Starting Interrupt " + GameUtils.getCardLink(_lightSideStartingInterrupt));
                                game.getGameState().showCardOnScreen(_lightSideStartingInterrupt);
                                game.getGameState().setStartingInterruptPlayed(_lightPlayerId, _lightSideStartingInterrupt);
                            }
                        }
                    });
            game.getActionsEnvironment().addActionToStack(action);
        }

        _nextProcess = _followingGameProcess;
    }

    private AwaitingDecision createChooseStartingInterruptDecision(final SwccgGame game, final String playerId, final Collection<PhysicalCard> possibleStartingInterrupts) {
        return new ArbitraryCardsSelectionDecision("Choose starting interrupt", new LinkedList<PhysicalCard>(possibleStartingInterrupts), 0, 1) {
            @Override
            public void decisionMade(String result) throws DecisionResultInvalidException {
                List<PhysicalCard> selectedInterrupts = getSelectedCardsByResponse(result);
                if (selectedInterrupts.isEmpty())
                    return;

                PhysicalCard selectedPhysicalCard = selectedInterrupts.get(0);

                if (playerId.equals(_darkPlayerId))
                    _darkSideStartingInterrupt = selectedPhysicalCard;
                else
                    _lightSideStartingInterrupt = selectedPhysicalCard;
            }
        };
    }

    @Override
    public GameProcess getNextProcess() {
        return _nextProcess;
    }
}
