package com.gempukku.swccgo.logic.timing.processes.pregame;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SystemQueueAction;
import com.gempukku.swccgo.logic.decisions.ArbitraryCardsSelectionDecision;
import com.gempukku.swccgo.logic.decisions.AwaitingDecision;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromOffTableEffect;
import com.gempukku.swccgo.logic.effects.StackActionEffect;
import com.gempukku.swccgo.logic.effects.TriggeringResultEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.processes.GameProcess;
import com.gempukku.swccgo.logic.timing.results.StartingLocationsAndObjectivesStepCompleteResult;

import java.util.*;

/**
 * The game process for playing starting locations and objectives.
 */
public class PlayStartingLocationsAndObjectivesGameProcess implements GameProcess {
    private String _darkPlayerId;
    private String _lightPlayerId;
    private GameProcess _followingGameProcess;
    private GameProcess _nextProcess;
    private boolean _checkedForObjectives;
    private PhysicalCard _darkSideObjective;
    private PhysicalCard _lightSideObjective;
    private PhysicalCard _darkSideLocation;
    private boolean _noValidDarkSideLocation;
    private PhysicalCard _lightSideLocation;
    private boolean _noValidLightSideLocation;
    private LinkedList<String> _previousLocations = new LinkedList<String>();
    private Collection<PhysicalCard> _darkSideChoices;
    private Collection<PhysicalCard> _lightSideChoices;
    private boolean _darkAllowsConversion;
    private boolean _lightAllowsConversion;
    private boolean _playLightLocation;
    private boolean _playDarkLocation;
    private boolean _playLightObjective;
    private boolean _playDarkObjective;
    private boolean _done;
    private List<Action> _actions = new ArrayList<Action>();

    /**
     * Creates the game process for playing starting locations and objectives
     * @param game the game
     */
    public PlayStartingLocationsAndObjectivesGameProcess(SwccgGame game) {
        _darkPlayerId = game.getDarkPlayer();
        _lightPlayerId = game.getLightPlayer();
        _followingGameProcess = new PlayStartingInterruptsGameProcess(game);
        _nextProcess = this;
    }

    @Override
    public void process(SwccgGame game) {
        // Check if the locations or objectives are to be played
        if (_done) {
            SystemQueueAction action = new SystemQueueAction();

            // If either player is playing an Objective, reveal any Objectives being
            // played so both players know what they are before the first player actually plays it
            // (since it may influence which Objective deployment actions each player wants to do).
            if (_darkSideObjective != null || _lightSideObjective != null) {
                action.appendEffect(
                        new PassthruEffect(action) {
                            @Override
                            protected void doPlayEffect(SwccgGame game) {
                                if (_darkSideObjective != null) {
                                    game.getGameState().sendMessage(_darkPlayerId + " reveals Objective " + GameUtils.getCardLink(_darkSideObjective));
                                    game.getGameState().showCardOnScreen(_darkSideObjective);
                                    game.getGameState().setObjectivePlayed(_darkPlayerId, _darkSideObjective);
                                }
                                if (_lightSideObjective != null) {
                                    game.getGameState().sendMessage(_lightPlayerId + " reveals Objective " + GameUtils.getCardLink(_lightSideObjective));
                                    game.getGameState().showCardOnScreen(_lightSideObjective);
                                    game.getGameState().setObjectivePlayed(_lightPlayerId, _lightSideObjective);
                                }
                            }
                        });
            }

            // Stack each of the actions to play locations or Objectives
            for (Action playCardAction : _actions) {
                action.appendEffect(new StackActionEffect(action, playCardAction));
            }

            // Emit an effect result that the deploy starting locations and Objectives process is complete
            action.appendEffect(
                    new TriggeringResultEffect(action, new StartingLocationsAndObjectivesStepCompleteResult()));

            // Place any cards that are not allowed in Reserve Deck that are still in Reserve Deck out of play
            action.appendEffect(
                    new PassthruEffect(action) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            // Check if any cards still in Reserve Deck that are not allowed in Reserve Deck during the game
                            checkForCardsNotAllowedInReserveDeck(game, _darkPlayerId);
                            checkForCardsNotAllowedInReserveDeck(game, _lightPlayerId);
                        }
                    }
            );

            game.getActionsEnvironment().addActionToStack(action);

            _nextProcess = _followingGameProcess;
            return;
        }

        // Add the actions in the correct order
        if (_playDarkObjective) {
            _playDarkObjective = false;

            // Deploy the dark side objective
            _actions.add(_darkSideObjective.getBlueprint().getPlayCardAction(_darkPlayerId, game, _darkSideObjective, _darkSideObjective, false, 0, null, null, null, null, null, false, 0, Filters.any, null));

            // Figure out the next step in the process
            if (_lightSideObjective != null)
                _playLightObjective = true;
            else
                _done = true;

            return;
        }
        else if (_playLightObjective) {
            _playLightObjective = false;

            // Deploy the light side objective
            _actions.add(_lightSideObjective.getBlueprint().getPlayCardAction(_lightPlayerId, game, _lightSideObjective, _lightSideObjective, false, 0, null, null, null, null, null, false, 0, Filters.any, null));

            // Figure out the next step in the process
            _done = true;

            return;
        }
        else if (_playDarkLocation) {
            _playDarkLocation = false;

            // Deploy the dark side starting location
            _darkSideLocation.startingLocation(true, game.getModifiersQuerying().isBattleground(game.getGameState(), _darkSideLocation, null));
            final SystemQueueAction action = new SystemQueueAction();
            action.appendEffect(
                    new PassthruEffect(action) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            // Convert location, or place out of play
                            Action playLocationAction = _darkSideLocation.getBlueprint().getPlayCardAction(_darkPlayerId, game, _darkSideLocation, _darkSideLocation, false, 0, null, null, null, null, null, false, 0, Filters.any, null);
                            if (playLocationAction != null) {
                                action.appendEffect(
                                        new StackActionEffect(action, playLocationAction));
                            }
                            else {
                                action.appendEffect(
                                        new PlaceCardOutOfPlayFromOffTableEffect(action, _darkSideLocation));
                            }
                        }});
            _actions.add(action);

            // Figure out the next step in the process
            if (_lightSideLocation != null && !_lightAllowsConversion)
                _playLightLocation = true;
            else if (_lightSideObjective != null)
                _playLightObjective = true;
            else
                _done = true;

            return;
        }
        else if (_playLightLocation) {
            _playLightLocation = false;

            // Deploy the light side starting location
            _lightSideLocation.startingLocation(true, game.getModifiersQuerying().isBattleground(game.getGameState(), _lightSideLocation, null));
            final SystemQueueAction action = new SystemQueueAction();
            action.appendEffect(
                    new PassthruEffect(action) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            // Convert location, or place out of play
                            Action playLocationAction = _lightSideLocation.getBlueprint().getPlayCardAction(_lightPlayerId, game, _lightSideLocation, _lightSideLocation, false, 0, null, null, null, null, null, false, 0, Filters.any, null);
                            if (playLocationAction != null) {
                                action.appendEffect(
                                        new StackActionEffect(action, playLocationAction));
                            }
                            else {
                                action.appendEffect(
                                        new PlaceCardOutOfPlayFromOffTableEffect(action, _lightSideLocation));
                            }
                        }});
            _actions.add(action);

            // Figure out the next step in the process
            if (_darkSideLocation != null && _lightAllowsConversion)
                _playDarkLocation = true;
            else if (_darkSideObjective != null)
                _playDarkObjective = true;
            else
                _done = true;

            return;
        }
        else if (_noValidDarkSideLocation) {

            // Figure out the next step in the process
            if (_lightSideLocation != null)
                _playLightLocation = true;
            else if (_lightSideObjective != null)
                _playLightObjective = true;
            else
                _done = true;

            return;
        }
        else if (_noValidLightSideLocation) {

            // Figure out the next step in the process
            if (_darkSideLocation != null)
                _playDarkLocation = true;
            else if (_darkSideObjective != null)
                _playDarkObjective = true;
            else
                _done = true;

            return;
        }

        // Figure out what Objectives (if any) each player has in their deck
        if (!_checkedForObjectives) {
            _checkedForObjectives = true;

            // Check for Objectives
            Collection<PhysicalCard> darkSideObjective = Filters.filterCount(game.getGameState().getReserveDeck(_darkPlayerId), game, 1, Filters.Objective);
            if (!darkSideObjective.isEmpty()) {
                _darkSideObjective = darkSideObjective.iterator().next();
            }

            Collection<PhysicalCard> lightSideObjective = Filters.filterCount(game.getGameState().getReserveDeck(_lightPlayerId), game, 1, Filters.Objective);
            if (!lightSideObjective.isEmpty()) {
                _lightSideObjective = lightSideObjective.iterator().next();
            }
        }

        // Check if both sides have an objective, in which case we don't need to look for starting locations.
        if (_darkSideObjective != null && _lightSideObjective != null) {
            _playDarkObjective = true;
            return;
        }

        // Check locations have not been selected yet, or if selections need to be remade
        if (_darkSideLocation == null && _lightSideLocation == null) {

            // Figure out the dark side locations to choose (unless player has Objective)
            if (_darkSideObjective == null) {
                if (_darkSideChoices == null) {
                    _darkSideChoices = Filters.filter(game.getGameState().getReserveDeck(_darkPlayerId), game, Filters.deployableAsStartingLocation);
                }
                else {
                    // Remove any locations that were previously selected
                    Iterator<PhysicalCard> iterator = _darkSideChoices.iterator();
                    while (iterator.hasNext()) {
                        if (_previousLocations.contains(iterator.next().getTitle())) {
                            iterator.remove();
                        }
                    }
                }

                if (_darkSideChoices.isEmpty()) {
                    _noValidDarkSideLocation = true;
                }
                else {
                    game.getUserFeedback().sendAwaitingDecision(_darkPlayerId, createChooseLocationDecision(game, _darkPlayerId, _darkSideChoices));
                }
            }

            // Figure out the light side locations to choose (unless player has Objective)
            if (_lightSideObjective == null) {
                if (_lightSideChoices == null) {
                    _lightSideChoices = Filters.filter(game.getGameState().getReserveDeck(_lightPlayerId), game, Filters.deployableAsStartingLocation);
                }
                else {
                    // Remove any locations that were previously selected
                    Iterator<PhysicalCard> iterator = _lightSideChoices.iterator();
                    while (iterator.hasNext()) {
                        if (_previousLocations.contains(iterator.next().getTitle())) {
                            iterator.remove();
                        }
                    }
                }

                if (_lightSideChoices.isEmpty()) {
                    _noValidLightSideLocation = true;
                }
                else {
                    game.getUserFeedback().sendAwaitingDecision(_lightPlayerId, createChooseLocationDecision(game, _lightPlayerId, _lightSideChoices));
                }
            }
        }
        // Check if Objectives / Locations can be deployed
        else if (_darkSideLocation == null || _lightSideLocation == null
                || !Filters.unique.accepts(game, _darkSideLocation) || !Filters.unique.accepts(game, _lightSideLocation)
                || !_darkSideLocation.getTitle().equals(_lightSideLocation.getTitle())
                || _darkAllowsConversion || _lightAllowsConversion) {

            // Figure out the next step in the process
            if (_darkSideLocation != null && !_lightAllowsConversion)
                _playDarkLocation = true;
            else if (_lightSideLocation != null)
                _playLightLocation = true;
            else if (_darkSideObjective != null)
                _playDarkObjective = true;
            else if (_lightSideObjective != null)
                _playLightObjective = true;
            else
                _done = true;
        }
        // Ask dark side player to willing to allow conversion
        else {
            _previousLocations.add(_darkSideLocation.getTitle());
            game.getUserFeedback().sendAwaitingDecision(_darkPlayerId, createAllowConversionDecision(game, _darkPlayerId, _darkSideLocation, _lightSideLocation));
        }
    }

    private AwaitingDecision createChooseLocationDecision(final SwccgGame game, final String playerId, final Collection<PhysicalCard> possibleCharacters) {
        return new ArbitraryCardsSelectionDecision("Choose starting location",
                new LinkedList<PhysicalCard>(possibleCharacters), 1, 1) {
            @Override
            public void decisionMade(String result) throws DecisionResultInvalidException {
                List<PhysicalCard> selectedLocations = getSelectedCardsByResponse(result);
                PhysicalCard selectedPhysicalCard = selectedLocations.get(0);

                if (playerId.equals(_darkPlayerId))
                    _darkSideLocation = selectedPhysicalCard;
                else
                    _lightSideLocation = selectedPhysicalCard;
            }
        };
    }

    private AwaitingDecision createAllowConversionDecision(final SwccgGame game, final String playerId, final PhysicalCard playerLocation, final PhysicalCard opponentLocation) {
        return new MultipleChoiceAwaitingDecision("Both players have chosen the same starting location. Will you allow the other player to convert " + GameUtils.getCardLink(playerLocation) + " with " + GameUtils.getCardLink(opponentLocation), new String[]{"Yes", "No"}) {
                    @Override
                    protected void validDecisionMade(int index, String result) {
                        if ("Yes".equals(result)) {
                            if (playerId.equals(_darkPlayerId))
                                _darkAllowsConversion = true;
                            else
                                _lightAllowsConversion = true;
                        }
                        else if (playerId.equals(_darkPlayerId)) {
                            // Check if light side player is willing to have location converted
                            game.getUserFeedback().sendAwaitingDecision(_lightPlayerId, createAllowConversionDecision(game, _lightPlayerId, _lightSideLocation, _darkSideLocation));
                        }
                        else {
                            // Neither player was willing to allow their location to be converted
                            _darkSideLocation = null;
                            _lightSideLocation = null;
                        }
                    }
        };
    }

    /**
     * Check if any cards still in player's Reserve Deck that are not allowed in Reserve Deck during the game. If any are
     * found, place them out of play.
     * @param game the game
     * @param playerId the player
     */
    private void checkForCardsNotAllowedInReserveDeck(SwccgGame game, String playerId) {
        GameState gameState = game.getGameState();
        Collection<PhysicalCard> invalidCards = Filters.filter(gameState.getReserveDeck(playerId), game, Filters.mayNotBePlacedInReserveDeck);
        if (!invalidCards.isEmpty()) {
            gameState.sendMessage(GameUtils.getAppendedNames(invalidCards) + " " + GameUtils.be(invalidCards) + " placed out of play due to not being allowed to be placed in Reserve Deck");
            gameState.removeCardsFromZone(invalidCards);
            for (PhysicalCard card : invalidCards) {
                gameState.addCardToZone(card, Zone.OUT_OF_PLAY, card.getOwner());
            }
        }
    }

    @Override
    public GameProcess getNextProcess() {
        return _nextProcess;
    }
}
