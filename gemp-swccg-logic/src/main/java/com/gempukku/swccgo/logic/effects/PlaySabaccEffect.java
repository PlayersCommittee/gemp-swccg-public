package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.game.state.SabaccState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.decisions.IntegerAwaitingDecision;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardsFromSabaccHandEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardsFromSabaccHandOrFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.ModifierFlag;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.*;
import com.gempukku.swccgo.logic.timing.results.SabaccTotalCalculatedResult;
import com.gempukku.swccgo.logic.timing.results.SabaccWinnerDeterminedResult;

import java.util.*;


/**
 * An effect that causes a game of sabacc to be played.
 */
public class PlaySabaccEffect extends AbstractSubActionEffect {
    private PhysicalCard _sabaccInterrupt;
    private String _playerId;
    private String _opponent;
    private PhysicalCard _sabaccPlayer;
    private PhysicalCard _opponentSabaccPlayer;
    private Filterable _wildCardsForPlayer;
    private Filterable _wildCardsForOpponent;
    private int _wildCardLowestValue;
    private int _wildCardHighestValue;
    private Map<Filterable, Filterable> _wildCardPerks;
    private Filterable _cloneCards;
    private Map<Filterable, Integer> _cloneCardPerks;
    private Filterable _stakes;
    private boolean _sabaccInitiated;

    /**
     * Creates an effect that causes a game of sabacc to be played.
     * @param action the action performing this effect
     * @param sabaccPlayer the sabacc player
     * @param opponentSabaccPlayer the opponent sabacc player, or null
     * @param wildCards the wildcard filter
     * @param wildCardLowestValue the lowest wildcard value
     * @param wildCardHighestValue the highest wildcard value
     * @param cloneCards the clone cards filter
     * @param stakes the stakes filter
     */
    public PlaySabaccEffect(Action action, PhysicalCard sabaccPlayer, PhysicalCard opponentSabaccPlayer, Filterable wildCards, int wildCardLowestValue, int wildCardHighestValue, Filterable cloneCards, Filterable stakes) {
        this(action, sabaccPlayer, opponentSabaccPlayer, wildCards, wildCardLowestValue, wildCardHighestValue, null, cloneCards, null, stakes);
    }

    /**
     * Creates an effect that causes a game of sabacc to be played.
     * @param action the action performing this effect
     * @param sabaccPlayer the sabacc player
     * @param opponentSabaccPlayer the opponent sabacc player, or null
     * @param wildCards the wildcard filter
     * @param wildCardLowestValue the lowest wildcard value
     * @param wildCardHighestValue the highest wildcard value
     * @param wildCardPerks the map of sabacc player filter to cards treated as wild cards filter
     * @param cloneCards the clone cards filter
     * @param stakes the stakes filter
     */
    public PlaySabaccEffect(Action action, PhysicalCard sabaccPlayer, PhysicalCard opponentSabaccPlayer, Filterable wildCards, int wildCardLowestValue, int wildCardHighestValue, Map<Filterable, Filterable> wildCardPerks, Filterable cloneCards, Filterable stakes) {
        this(action, sabaccPlayer, opponentSabaccPlayer, wildCards, wildCardLowestValue, wildCardHighestValue, wildCardPerks, cloneCards, null, stakes);
    }

    /**
     * Creates an effect that causes a game of sabacc to be played.
     * @param action the action performing this effect
     * @param sabaccPlayer the sabacc player
     * @param opponentSabaccPlayer the opponent sabacc player, or null
     * @param wildCards the wildcard filter
     * @param wildCardLowestValue the lowest wildcard value
     * @param wildCardHighestValue the highest wildcard value
     * @param cloneCards the clone cards filter
     * @param cloneCardPerks the map of sabacc player filter to the optional value that clone cards can take on
     * @param stakes the stakes filter
     */
    public PlaySabaccEffect(Action action, PhysicalCard sabaccPlayer, PhysicalCard opponentSabaccPlayer, Filterable wildCards, int wildCardLowestValue, int wildCardHighestValue, Filterable cloneCards, Map<Filterable, Integer> cloneCardPerks, Filterable stakes) {
        this(action, sabaccPlayer, opponentSabaccPlayer, wildCards, wildCardLowestValue, wildCardHighestValue, null, cloneCards, cloneCardPerks, stakes);
    }

    /**
     * Creates an effect that causes a game of sabacc to be played.
     * @param action the action performing this effect
     * @param sabaccPlayer the sabacc player
     * @param opponentSabaccPlayer the opponent sabacc player, or null
     * @param wildCards the wildcard filter
     * @param wildCardLowestValue the lowest wildcard value
     * @param wildCardHighestValue the highest wildcard value
     * @param wildCardPerks the map of sabacc player filter to cards treated as wild cards filter
     * @param cloneCards the clone cards filter
     * @param cloneCardPerks the map of sabacc player filter to the optional value that clone cards can take on
     * @param stakes the stakes filter
     */
    private PlaySabaccEffect(Action action, PhysicalCard sabaccPlayer, PhysicalCard opponentSabaccPlayer, Filterable wildCards, int wildCardLowestValue, int wildCardHighestValue, Map<Filterable, Filterable> wildCardPerks, Filterable cloneCards, Map<Filterable, Integer> cloneCardPerks, Filterable stakes) {
        super(action);
        _sabaccInterrupt = action.getActionSource();
        _sabaccPlayer = sabaccPlayer;
        _opponentSabaccPlayer = opponentSabaccPlayer;
        _wildCardsForPlayer = wildCards;
        _wildCardsForOpponent = wildCards;
        _wildCardLowestValue = wildCardLowestValue;
        _wildCardHighestValue = wildCardHighestValue;
        if (wildCardPerks != null)
            _wildCardPerks = Collections.unmodifiableMap(wildCardPerks);
        _cloneCards = cloneCards;
        if (cloneCardPerks != null)
            _cloneCardPerks = Collections.unmodifiableMap(cloneCardPerks);
        _stakes = stakes;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(final SwccgGame game) {
        final GameState gameState = game.getGameState();
        final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
        _playerId = _action.getPerformingPlayer();
        _opponent = game.getOpponent(_playerId);
        final List<StandardEffect> drawSabaccHandsEffects = new ArrayList<StandardEffect>();

        final SubAction subAction = new SubAction(_action);

        // 1) Verify that both players still have 2 cards in Reserve Deck, otherwise sabacc ends immediately.
        if (!canSabaccBeInitiated(game)) {
            subAction.appendEffect(
                    new SendMessageEffect(subAction, "Unable to initiate sabacc since both players cannot draw two cards from Reserve Deck"));
            return subAction;
        }

        // 2) Start sabacc and each player draws sabacc hand
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        // Start sabacc game state
                        gameState.beginSabacc(_sabaccInterrupt, _sabaccPlayer, _opponentSabaccPlayer);
                        gameState.sendMessage(GameUtils.getFullName(_sabaccInterrupt) + " game begins");

                        // Check which players are allowed to use wild cards
                        if (game.getModifiersQuerying().hasFlagActive(game.getGameState(), ModifierFlag.MAY_NOT_USE_WILD_CARDS_IN_SABACC, _playerId)) {
                            _wildCardsForPlayer = Filters.none;
                        }
                        if (game.getModifiersQuerying().hasFlagActive(game.getGameState(), ModifierFlag.MAY_NOT_USE_WILD_CARDS_IN_SABACC, _opponent)) {
                            _wildCardsForOpponent = Filters.none;
                        }

                        // Players draw sabacc hands
                        final SubAction drawHandsAction = new SubAction(subAction);
                        drawSabaccHandsEffects.add(new DrawSabaccCardsEffect(subAction, _playerId, 2) {
                            @Override
                            public String getText(SwccgGame game) {
                                return "You";
                            }
                        });
                        drawSabaccHandsEffects.add(new DrawSabaccCardsEffect(subAction, _opponent, 2) {
                            @Override
                            public String getText(SwccgGame game) {
                                return "Opponent";
                            }
                        });
                        drawHandsAction.appendEffect(
                                new ChooseEffectOrderEffect(drawHandsAction, drawSabaccHandsEffects, true) {
                                    @Override
                                    protected String getChoiceText() {
                                        return "Choose player to draw sabacc hand first";
                                    }
                                });
                        subAction.stackSubAction(drawHandsAction);
                    }
                }
        );

        // 3) Verify that both players were able to draw 2 cards
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        boolean bothPlayersDrewTwoCards = !drawSabaccHandsEffects.isEmpty();
                        for (StandardEffect drawSabaccCardsEffect : drawSabaccHandsEffects) {
                            if (!drawSabaccCardsEffect.wasCarriedOut()) {
                                bothPlayersDrewTwoCards = false;
                                game.getGameState().sendMessage("Unable to initiate sabacc since both players cannot draw two cards from Reserve Deck");
                                break;
                            }
                        }
                        // Skip to end if both players did not draw two cards
                        gameState.getSabaccState().setInitialCardsDrawn(bothPlayersDrewTwoCards);
                        gameState.getSabaccState().setSkipToEnd(!bothPlayersDrewTwoCards);
                        _sabaccInitiated = bothPlayersDrewTwoCards;
                    }
                }
        );

        // 4) Check for perfect sabacc
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        final SabaccState sabaccState = gameState.getSabaccState();
                        if (sabaccState.isSkipToEnd())
                            return;

                        final SubAction checkForPerfectAction = new SubAction(subAction);
                        final Collection<PhysicalCard> playersInitialSabaccHand = gameState.getSabaccHand(_playerId);
                        // Determine sabacc value of each card in sabacc hand
                        if (!Filters.canSpot(playersInitialSabaccHand, game, Filters.or(_wildCardsForPlayer, _cloneCards))) {
                            checkForPerfectAction.appendEffect(
                                    new RefreshPrintedDestinyValuesEffect(checkForPerfectAction, playersInitialSabaccHand) {
                                        @Override
                                        protected void refreshedPrintedDestinyValues() {
                                            // Set the sabacc value for each card
                                            for (PhysicalCard sabaccCard : playersInitialSabaccHand) {
                                                sabaccCard.setSabaccValue(modifiersQuerying.getDestiny(gameState, sabaccCard));
                                            }
                                        }
                                    }
                            );
                        }
                        final Collection<PhysicalCard> opponentsInitialSabaccHand = gameState.getSabaccHand(_opponent);
                        // Determine sabacc value of each card in sabacc hand
                        if (!Filters.canSpot(opponentsInitialSabaccHand, game, Filters.or(_wildCardsForOpponent, _cloneCards))) {
                            checkForPerfectAction.appendEffect(
                                    new RefreshPrintedDestinyValuesEffect(checkForPerfectAction, opponentsInitialSabaccHand) {
                                        @Override
                                        protected void refreshedPrintedDestinyValues() {
                                            // Set the sabacc value for each card
                                            for (PhysicalCard sabaccCard : opponentsInitialSabaccHand) {
                                                sabaccCard.setSabaccValue(modifiersQuerying.getDestiny(gameState, sabaccCard));
                                            }
                                        }
                                    }
                            );
                        }
                        checkForPerfectAction.appendEffect(
                                new PassthruEffect(checkForPerfectAction) {
                                    @Override
                                    protected void doPlayEffect(SwccgGame game) {
                                        // Check if player has perfect sabacc
                                        boolean playerHasPerfectSabacc = false;
                                        // May not have any wild cards (not counting optional wild cards) or clone cards
                                        if (!Filters.canSpot(playersInitialSabaccHand, game, Filters.or(_wildCardsForPlayer, _cloneCards))) {
                                            float playersSabaccTotal = modifiersQuerying.getSabaccTotal(gameState, _playerId);
                                            playerHasPerfectSabacc = (playersSabaccTotal == 11);
                                        }
                                        // Check if opponent has perfect sabacc
                                        boolean opponentHasPerfectSabacc = false;
                                        // May not have any wild cards (not counting optional wild cards) or clone cards
                                        if (!Filters.canSpot(opponentsInitialSabaccHand, game, Filters.or(_wildCardsForOpponent, _cloneCards))) {
                                            float opponentsSabaccTotal = modifiersQuerying.getSabaccTotal(gameState, _opponent);
                                            opponentHasPerfectSabacc = (opponentsSabaccTotal == 11);
                                        }

                                        // If either player has a perfect sabacc, then reveal sabacc hands immediately
                                        if (playerHasPerfectSabacc || opponentHasPerfectSabacc) {

                                            gameState.revealSabaccHands();
                                            sabaccState.setFinalSabaccTotal(_playerId, playerHasPerfectSabacc ? 11 : -1);
                                            sabaccState.setFinalSabaccTotal(_opponent, opponentHasPerfectSabacc ? 11 : -1);

                                            // If both players have perfect sabacc, then game is a draw
                                            if (playerHasPerfectSabacc && opponentHasPerfectSabacc) {
                                                gameState.sendMessage("Both players have perfect sabacc, so sabacc game is a draw");
                                            }
                                            // If one player has perfect sabacc, that player wins double.
                                            else {
                                                String winner = playerHasPerfectSabacc ? _playerId : _opponent;
                                                PhysicalCard winningCharacter = playerHasPerfectSabacc ? _sabaccPlayer : _opponentSabaccPlayer;
                                                gameState.sendMessage(winner + " wins with a perfect sabacc!");

                                                checkForPerfectAction.appendEffect(
                                                        new SabaccVictoryEffect(checkForPerfectAction, winner, winningCharacter, true));
                                            }
                                            sabaccState.setSkipToEnd(true);
                                        }
                                        else {
                                            gameState.sendMessage("Neither player has a perfect sabacc");
                                        }
                                    }
                                }
                        );
                        subAction.stackSubAction(checkForPerfectAction);
                    }
                }
        );

        // 5) Players may draw more sabacc cards
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        final SabaccState sabaccState = gameState.getSabaccState();
                        if (sabaccState.isSkipToEnd())
                            return;

                        // Reset the sabacc value for each card
                        final Collection<PhysicalCard> playersSabaccHand = gameState.getSabaccHand(_playerId);
                        for (PhysicalCard sabaccCard : playersSabaccHand) {
                            sabaccCard.setSabaccValue(-1);
                        }
                        final Collection<PhysicalCard> playersCardsWithFixedValue = Filters.filter(playersSabaccHand, game, Filters.not(Filters.or(Filters.multipleDestinyValues, _wildCardsForPlayer, _cloneCards, getWildCardsFilterAsPerk(game, _playerId))));
                        for (PhysicalCard sabaccCard : playersCardsWithFixedValue) {
                            sabaccCard.setSabaccValue(modifiersQuerying.getDestiny(gameState, sabaccCard));
                        }

                        final Collection<PhysicalCard> opponentsSabaccHand = gameState.getSabaccHand(_opponent);
                        for (PhysicalCard sabaccCard : opponentsSabaccHand) {
                            sabaccCard.setSabaccValue(-1);
                        }
                        final Collection<PhysicalCard> opponentsCardsWithFixedValue = Filters.filter(opponentsSabaccHand, game, Filters.not(Filters.or(Filters.multipleDestinyValues, _wildCardsForOpponent, _cloneCards, getWildCardsFilterAsPerk(game, _opponent))));
                        for (PhysicalCard sabaccCard : opponentsCardsWithFixedValue) {
                            sabaccCard.setSabaccValue(modifiersQuerying.getDestiny(gameState, sabaccCard));
                        }

                        // Take turns (starting with opponent) drawing additional sabacc cards
                        final SubAction drawAdditionCardsAction = new SubAction(subAction);
                        drawAdditionCardsAction.appendEffect(
                                new TakeTurnsDrawingCardsEffect(drawAdditionCardsAction, _opponent, false));
                        subAction.stackSubAction(drawAdditionCardsAction);
                    }
                }
        );

        // 5) Players choose values for any wild cards and clone cards
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        final SabaccState sabaccState = gameState.getSabaccState();
                        if (sabaccState.isSkipToEnd())
                            return;

                        final SubAction chooseCardValuesAction = new SubAction(subAction);
                        chooseCardValuesAction.appendEffect(
                                new PassthruEffect(chooseCardValuesAction) {
                                    @Override
                                    protected void doPlayEffect(SwccgGame game) {
                                        final Collection<PhysicalCard> playersSabaccHand = gameState.getSabaccHand(_playerId);
                                        gameState.sendMessage(_playerId + " examines sabacc hand to determine sabacc card values");

                                        // Check if all cards are clone cards (if so, then all cards are 0)
                                        if (!Filters.canSpot(playersSabaccHand, game, Filters.not(Filters.and(_cloneCards)))) {
                                            for (PhysicalCard sabaccCard : playersSabaccHand) {
                                                sabaccCard.setSabaccValue(0);
                                            }
                                        }
                                        else {
                                            final Collection<PhysicalCard> playersCardsWithFixedValue = Filters.filter(playersSabaccHand, game, Filters.not(Filters.or(Filters.multipleDestinyValues, _wildCardsForPlayer, _cloneCards, getWildCardsFilterAsPerk(game, _playerId))));
                                            for (PhysicalCard sabaccCard : playersCardsWithFixedValue) {
                                                sabaccCard.setSabaccValue(modifiersQuerying.getDestiny(gameState, sabaccCard));
                                            }
                                            final Collection<PhysicalCard> playersCardsWithVariableValue = Filters.filter(playersSabaccHand, game, Filters.not(Filters.in(playersCardsWithFixedValue)));

                                            final SubAction playerChooseCardValuesAction = new SubAction(chooseCardValuesAction);
                                            playerChooseCardValuesAction.appendEffect(
                                                    new ChooseSabaccValuesEffect(playerChooseCardValuesAction, _playerId, playersCardsWithVariableValue, false));
                                            chooseCardValuesAction.stackSubAction(playerChooseCardValuesAction);
                                        }
                                    }
                                }
                        );
                        chooseCardValuesAction.appendEffect(
                                new PassthruEffect(chooseCardValuesAction) {
                                    @Override
                                    protected void doPlayEffect(SwccgGame game) {
                                        final Collection<PhysicalCard> opponentsSabaccHand = gameState.getSabaccHand(_opponent);
                                        gameState.sendMessage(_opponent + " examines sabacc hand to determine sabacc card values");

                                        // Check if all cards are clone cards (if so, then all cards are 0)
                                        if (!Filters.canSpot(opponentsSabaccHand, game, Filters.not(Filters.and(_cloneCards)))) {
                                            for (PhysicalCard sabaccCard : opponentsSabaccHand) {
                                                sabaccCard.setSabaccValue(0);
                                            }
                                        }
                                        else {
                                            final Collection<PhysicalCard> opponentsCardsWithFixedValue = Filters.filter(opponentsSabaccHand, game, Filters.not(Filters.or(Filters.multipleDestinyValues, _wildCardsForOpponent, _cloneCards, getWildCardsFilterAsPerk(game, _opponent))));
                                            for (PhysicalCard sabaccCard : opponentsCardsWithFixedValue) {
                                                sabaccCard.setSabaccValue(modifiersQuerying.getDestiny(gameState, sabaccCard));
                                            }
                                            final Collection<PhysicalCard> opponentsCardsWithVariableValue = Filters.filter(opponentsSabaccHand, game, Filters.not(Filters.in(opponentsCardsWithFixedValue)));

                                            final SubAction opponentChooseCardValuesAction = new SubAction(chooseCardValuesAction);
                                            opponentChooseCardValuesAction.appendEffect(
                                                    new ChooseSabaccValuesEffect(opponentChooseCardValuesAction, _opponent, opponentsCardsWithVariableValue, false));
                                            chooseCardValuesAction.stackSubAction(opponentChooseCardValuesAction);
                                        }
                                    }
                                }
                        );
                        subAction.stackSubAction(chooseCardValuesAction);
                    }
                }
        );

        // 6) Players reveal sabacc hands and trigger to perform any adjustments to sabacc totals.
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        final SabaccState sabaccState = gameState.getSabaccState();
                        if (sabaccState.isSkipToEnd())
                            return;

                        // Reveal sabacc hands
                        gameState.revealSabaccHands();

                        // Trigger to perform any adjustments to sabacc totals.
                        game.getActionsEnvironment().emitEffectResult(new SabaccTotalCalculatedResult(subAction));
                    }
                }
        );

        // 7) Determine winner
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        final SabaccState sabaccState = gameState.getSabaccState();
                        if (sabaccState.isSkipToEnd())
                            return;

                        // Determine player totals
                        float playersTotal = modifiersQuerying.getSabaccTotal(gameState, _playerId);
                        int playersHandSize = gameState.getSabaccHand(_playerId).size();
                        float opponentsTotal = modifiersQuerying.getSabaccTotal(gameState, _opponent);
                        int opponentsHandSize = gameState.getSabaccHand(_opponent).size();
                        String winningPlayer = null;

                        sabaccState.setFinalSabaccTotal(_playerId, playersTotal);
                        sabaccState.setFinalSabaccTotal(_opponent, opponentsTotal);

                        // Check if one player is closer to 11, without going over
                        if (playersTotal <= 11 && (opponentsTotal > 11 || opponentsTotal < playersTotal)) {
                            winningPlayer = _playerId;
                        }
                        else if (opponentsTotal <= 11 && (playersTotal > 11 || playersTotal < opponentsTotal)) {
                            winningPlayer = _opponent;
                        }
                        // Check if both players are over 11, but one player is closer to 11
                        else if (playersTotal > 11 && opponentsTotal > 11 && playersTotal < opponentsTotal) {
                            winningPlayer = _playerId;
                        }
                        else if (opponentsTotal > 11 && playersTotal > 11 && opponentsTotal < playersTotal) {
                            winningPlayer = _opponent;
                        }
                        // Check if one player has fewer sabacc cards in hand
                        else if (playersHandSize < opponentsHandSize) {
                            winningPlayer = _playerId;
                        }
                        else if (opponentsHandSize < playersHandSize) {
                            winningPlayer = _opponent;
                        }
                        // Otherwise sabacc game ends in tie

                        if (winningPlayer != null) {
                            if (winningPlayer.equals(_playerId)) {

                                gameState.sendMessage(_playerId + " wins sabacc game with " + playersHandSize + " card" + GameUtils.s(playersHandSize) + " totaling "
                                        + GuiUtils.formatAsString(playersTotal) + " against " + _opponent + "'s " + opponentsHandSize + " card" + GameUtils.s(opponentsHandSize) + " totaling " + GuiUtils.formatAsString(opponentsTotal));
                            }
                            else {
                                gameState.sendMessage(_opponent + " wins sabacc game with " + opponentsHandSize + " card" + GameUtils.s(opponentsHandSize) + " totaling "
                                        + GuiUtils.formatAsString(opponentsTotal) + " against " + _playerId + "'s " + playersHandSize + " card" + GameUtils.s(playersHandSize) + " totaling " + GuiUtils.formatAsString(playersTotal));
                            }
                            PhysicalCard winningCharacter = winningPlayer.equals(_playerId) ? _sabaccPlayer : _opponentSabaccPlayer;

                            final SubAction determineWinnerAction = new SubAction(subAction);
                            determineWinnerAction.appendEffect(
                                    new SabaccVictoryEffect(determineWinnerAction, winningPlayer, winningCharacter, false));
                            subAction.stackSubAction(determineWinnerAction);
                        }
                        else {
                            gameState.sendMessage("Both players have same sabacc total, " + GuiUtils.formatAsString(playersTotal) + ", and hand size, " + playersHandSize + ", so sabacc game is a draw");
                        }
                    }
                }
        );

        // 8) Put sabacc hands back in Used Piles and finish sabacc game.
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {

                        // Place sabacc hands in Used Piles
                        final SubAction endSabaccAction = new SubAction(subAction);
                        endSabaccAction.appendEffect(new PlaceSabaccHandInUsedPileEffect(endSabaccAction, _opponent, game));
                        endSabaccAction.appendEffect(new PlaceSabaccHandInUsedPileEffect(endSabaccAction, _playerId, game));

                        // Finish game
                        endSabaccAction.appendEffect(
                                new PassthruEffect(endSabaccAction) {
                                    @Override
                                    protected void doPlayEffect(SwccgGame game) {
                                        gameState.sendMessage(GameUtils.getFullName(_sabaccInterrupt) + " game ends");
                                        gameState.endSabacc();
                                    }
                                }
                        );
                        subAction.stackSubAction(endSabaccAction);
                    }
                }
        );

        return subAction;
    }


    /**
     * A private effect that causes players to take turns drawing sabacc cards until both have 6 cards or both have passed.
     */
    private class TakeTurnsDrawingCardsEffect extends AbstractSuccessfulEffect {
        private SubAction _subAction;
        private String _currentPlayerToDraw;
        private boolean _otherPlayerDone;

        /**
         * Creates an effect that causes players to take turns drawing sabacc cards until both have 6 cards or both have passed.
         * @param subAction the action performing this effect
         * @param currentPlayerToDraw the current player to draw
         * @param otherPlayerDone true if other player has passed or cannot draw more cards
         */
        public TakeTurnsDrawingCardsEffect(SubAction subAction, String currentPlayerToDraw, boolean otherPlayerDone) {
            super(subAction);
            _subAction = subAction;
            _currentPlayerToDraw = currentPlayerToDraw;
            _otherPlayerDone = otherPlayerDone;
        }

        @Override
        protected void doPlayEffect(final SwccgGame game) {
            // Check if the specified player can draw a card (limit 6 cards in sabacc hand)
            List<PhysicalCard> sabaccHand = game.getGameState().getSabaccHand(_currentPlayerToDraw);
            boolean canDrawCard = !game.getGameState().getReserveDeck(_currentPlayerToDraw).isEmpty() && sabaccHand.size() < 6;

            if (canDrawCard) {
                // SubAction to carry out drawing a card
                final SubAction chooseToDrawCardAction = new SubAction(_subAction);
                chooseToDrawCardAction.appendEffect(
                        new PlayoutDecisionEffect(chooseToDrawCardAction, _currentPlayerToDraw,
                                new YesNoDecision("Do you want to draw another sabacc card?") {
                                    @Override
                                    protected void yes() {
                                        chooseToDrawCardAction.appendEffect(
                                                new DrawSabaccCardsEffect(chooseToDrawCardAction, _currentPlayerToDraw, 1));
                                        chooseToDrawCardAction.appendEffect(
                                                new PassthruEffect(chooseToDrawCardAction) {
                                                    @Override
                                                    protected void doPlayEffect(SwccgGame game) {
                                                        Filterable wildCards = _currentPlayerToDraw.equals(_playerId) ? _wildCardsForPlayer : _wildCardsForOpponent;

                                                        // Update the sabacc value for each card with fixed value
                                                        final Collection<PhysicalCard> playersCardsWithFixedValue = Filters.filter(game.getGameState().getSabaccHand(_currentPlayerToDraw), game,
                                                                Filters.not(Filters.or(Filters.multipleDestinyValues, wildCards, _cloneCards, getWildCardsFilterAsPerk(game, _currentPlayerToDraw))));
                                                        for (PhysicalCard sabaccCard : playersCardsWithFixedValue) {
                                                            sabaccCard.setSabaccValue(game.getModifiersQuerying().getDestiny(game.getGameState(), sabaccCard));
                                                        }
                                                    }
                                                }
                                        );

                                        if (!_otherPlayerDone) {
                                            _subAction.appendEffect(
                                                    new TakeTurnsDrawingCardsEffect(_subAction, game.getOpponent(_currentPlayerToDraw), false));
                                        }
                                        else {
                                            _subAction.appendEffect(
                                                    new TakeTurnsDrawingCardsEffect(_subAction, _currentPlayerToDraw, true));
                                        }
                                    }
                                    @Override
                                    protected void no() {
                                        game.getGameState().sendMessage(_currentPlayerToDraw + " chooses to 'pass' on drawing another sabacc card");
                                        if (!_otherPlayerDone) {
                                            _subAction.appendEffect(
                                                    new TakeTurnsDrawingCardsEffect(_subAction, game.getOpponent(_currentPlayerToDraw), true));
                                        }
                                    }
                                }
                        ));
                _subAction.stackSubAction(chooseToDrawCardAction);
            }
            else if (!_otherPlayerDone) {
                _subAction.appendEffect(
                        new TakeTurnsDrawingCardsEffect(_subAction, game.getOpponent(_currentPlayerToDraw), true));
            }
        }
    }

    /**
     * A private effect that causes the player to choose the values of each sabacc card that does not have a fixed value.
     */
    private class ChooseSabaccValuesEffect extends ChooseCardsFromSabaccHandEffect {
        private SubAction _subAction;
        private String _currentPlayerId;
        private Collection<PhysicalCard> _cardsWithVariableValue;

        /**
         * Creates an effect that causes the player to choose the values of each sabacc card that does not have a fixed value.
         *
         * @param subAction the action
         * @param playerId the sabacc hand owner
         * @param cardsWithVariableValue the sabacc cards with variable values
         */
        public ChooseSabaccValuesEffect(SubAction subAction, String playerId, Collection<PhysicalCard> cardsWithVariableValue, boolean consideredComplete) {
            super(subAction, playerId, playerId, consideredComplete ? 0 : 1, 1, false, Filters.in(cardsWithVariableValue));
            _subAction = subAction;
            _currentPlayerId = playerId;
            _cardsWithVariableValue = cardsWithVariableValue;
        }

        @Override
        public String getChoiceText(int numCardsToChoose) {
            return "Choose card to set sabacc value";
        }

        @Override
        protected void cardsSelected(final SwccgGame game, final Collection<PhysicalCard> selectedCards) {
            for (final PhysicalCard selectedCard : selectedCards) {

                // SubAction to carry out updating the value
                final SubAction chooseValueSubAction = new SubAction(_subAction);

                Filterable wildCards = _currentPlayerId.equals(_playerId) ? _wildCardsForPlayer : _wildCardsForOpponent;

                // Check if the card selected is an optional wildcard
                if (Filters.and(getWildCardsFilterAsPerk(game, _currentPlayerId)).accepts(game.getGameState(), game.getModifiersQuerying(), selectedCard)) {
                    // Ask if player wants to use card as wildcard
                    chooseValueSubAction.appendEffect(
                            new PlayoutDecisionEffect(chooseValueSubAction, _currentPlayerId,
                                    new YesNoDecision("Do you want use " + GameUtils.getCardLink(selectedCard) + " as a wild card?") {
                                        @Override
                                        protected void yes() {
                                            // Ask player to choose wildcard value
                                            int defaultValue = Math.min(_wildCardHighestValue, Math.max(_wildCardLowestValue, (int) selectedCard.getSabaccValue()));
                                            chooseValueSubAction.appendEffect(
                                                    new PlayoutDecisionEffect(chooseValueSubAction, _currentPlayerId,
                                                            new IntegerAwaitingDecision("Choose wild card value ", _wildCardLowestValue, _wildCardHighestValue, defaultValue) {
                                                                @Override
                                                                public void decisionMade(int result) throws DecisionResultInvalidException {
                                                                    selectedCard.setSabaccValue(result);

                                                                    // Choose next card
                                                                    _subAction.appendEffect(
                                                                            new ChooseSabaccValuesEffect(_subAction, _currentPlayerId, _cardsWithVariableValue, isAllCardsHaveSabaccValueSet()));
                                                                }
                                                            }
                                                    ));
                                        }
                                        @Override
                                        protected void no() {
                                            // Refresh the destiny value and use that
                                            chooseValueSubAction.appendEffect(
                                                    new RefreshPrintedDestinyValuesEffect(chooseValueSubAction, selectedCards) {
                                                        @Override
                                                        protected void refreshedPrintedDestinyValues() {
                                                            selectedCard.setSabaccValue(game.getModifiersQuerying().getDestiny(game.getGameState(), selectedCard));

                                                            // Choose next card
                                                            _subAction.appendEffect(
                                                                    new ChooseSabaccValuesEffect(_subAction, _currentPlayerId, _cardsWithVariableValue, isAllCardsHaveSabaccValueSet()));

                                                        }
                                                    }
                                            );
                                        }
                                    }
                            ));
                }
                // Check if the card selected is a wildcard
                else if (Filters.and(wildCards).accepts(game.getGameState(), game.getModifiersQuerying(), selectedCard)) {
                    // Ask player to choose wildcard value
                    int defaultValue = Math.min(_wildCardHighestValue, Math.max(_wildCardLowestValue, (int) selectedCard.getSabaccValue()));
                    chooseValueSubAction.appendEffect(
                            new PlayoutDecisionEffect(chooseValueSubAction, _currentPlayerId,
                                    new IntegerAwaitingDecision("Choose wild card value ", _wildCardLowestValue, _wildCardHighestValue, defaultValue) {
                                        @Override
                                        public void decisionMade(int result) throws DecisionResultInvalidException {
                                            selectedCard.setSabaccValue(result);

                                            // Choose next card
                                            _subAction.appendEffect(
                                                    new ChooseSabaccValuesEffect(_subAction, _currentPlayerId, _cardsWithVariableValue, isAllCardsHaveSabaccValueSet()));
                                        }
                                    }
                            ));
                }
                // Check if the card selected if a clone card
                else if (Filters.and(_cloneCards).accepts(game.getGameState(), game.getModifiersQuerying(), selectedCard)) {
                    final Integer optionalCloneValue = getCloneCardValueAsPerk(game, _currentPlayerId);
                    if (optionalCloneValue != null) {
                        // Ask if player wants to use the optional clone card value
                        chooseValueSubAction.appendEffect(
                                new PlayoutDecisionEffect(chooseValueSubAction, _currentPlayerId,
                                        new YesNoDecision("Do you want use " + optionalCloneValue + " as the clone card value?") {
                                            @Override
                                            protected void yes() {
                                                selectedCard.setSabaccValue(optionalCloneValue);

                                                // Choose next card
                                                _subAction.appendEffect(
                                                        new ChooseSabaccValuesEffect(_subAction, _currentPlayerId, _cardsWithVariableValue, isAllCardsHaveSabaccValueSet()));
                                            }
                                            @Override
                                            protected void no() {
                                                // Ask player to choose another card to clone its value
                                                chooseValueSubAction.appendEffect(
                                                        new ChooseCardsFromSabaccHandOrFromTableEffect(chooseValueSubAction, _currentPlayerId, _currentPlayerId, 1, 1, false,
                                                                Filters.or(Filters.and(Filters.inSabaccHand(_currentPlayerId), Filters.not(Filters.and(_cloneCards))),
                                                                           Filters.and(Filters.in_play, Filters.mayBeClonedInSabacc(_currentPlayerId)))) {
                                                            @Override
                                                            protected void cardsSelected(final SwccgGame game, Collection<PhysicalCard> nonCloneSelectedCards) {
                                                                PhysicalCard selectedNonClone = nonCloneSelectedCards.iterator().next();
                                                                selectedCard.setSabaccCardCloned(selectedNonClone);

                                                                // Choose next card
                                                                _subAction.appendEffect(
                                                                        new ChooseSabaccValuesEffect(_subAction, _currentPlayerId, _cardsWithVariableValue, isAllCardsHaveSabaccValueSet()));
                                                            }
                                                            @Override
                                                            public String getChoiceText(int numCardsToChoose) {
                                                                return "Choose card to clone value";
                                                            }
                                                        }
                                                );
                                            }
                                        }
                                ));
                    }
                    else {
                        // Ask player to choose another card to clone its value
                        chooseValueSubAction.appendEffect(
                                new ChooseCardsFromSabaccHandOrFromTableEffect(chooseValueSubAction, _currentPlayerId, _currentPlayerId, 1, 1, false,
                                        Filters.or(Filters.and(Filters.inSabaccHand(_currentPlayerId), Filters.not(Filters.and(_cloneCards))),
                                                   Filters.and(Filters.in_play, Filters.mayBeClonedInSabacc(_currentPlayerId)))) {
                                    @Override
                                    protected void cardsSelected(final SwccgGame game, Collection<PhysicalCard> nonCloneSelectedCards) {
                                        PhysicalCard selectedNonClone = nonCloneSelectedCards.iterator().next();
                                        selectedCard.setSabaccCardCloned(selectedNonClone);

                                        // Choose next card
                                        _subAction.appendEffect(
                                                new ChooseSabaccValuesEffect(_subAction, _currentPlayerId, _cardsWithVariableValue, isAllCardsHaveSabaccValueSet()));
                                    }
                                    @Override
                                    public String getChoiceText(int numCardsToChoose) {
                                        return "Choose card to clone value";
                                    }
                                }
                        );
                    }
                }
                else {
                    // Refresh the destiny value and use that
                    chooseValueSubAction.appendEffect(
                            new RefreshPrintedDestinyValuesEffect(chooseValueSubAction, selectedCards) {
                                @Override
                                protected void refreshedPrintedDestinyValues() {
                                    selectedCard.setSabaccValue(game.getModifiersQuerying().getDestiny(game.getGameState(), selectedCard));

                                    // Choose next card
                                    _subAction.appendEffect(
                                            new ChooseSabaccValuesEffect(_subAction, _currentPlayerId, _cardsWithVariableValue, isAllCardsHaveSabaccValueSet()));

                                }
                            }
                    );
                }
                _subAction.stackSubAction(chooseValueSubAction);
            }
        }

        /**
         * Determines if all cards have a sabacc value set.
         * @return true or false
         */
        private boolean isAllCardsHaveSabaccValueSet() {
            for (PhysicalCard card : _cardsWithVariableValue) {
                if (card.getSabaccValue() == -1)
                    return false;
            }
            return true;
        }
    }

    /**
     * A private effect that performs the steps for a sabacc victory.
     */
    private class SabaccVictoryEffect extends AbstractSubActionEffect {
        private String _winningPlayerId;
        private PhysicalCard _characterToWin;
        private boolean _perfectSabacc;

        /**
         * Creates an effect that performs the steps for a sabacc victory.
         * @param action the action performing this effect
         * @param winningPlayerId the sabacc winner
         * @param characterToWin the sabacc winning character, or null
         * @param perfectSabacc true if victory was from a perfect sabacc, otherwise false
         */
        public SabaccVictoryEffect(Action action, String winningPlayerId, PhysicalCard characterToWin, boolean perfectSabacc) {
            super(action);
            _winningPlayerId = winningPlayerId;
            _characterToWin = characterToWin;
            _perfectSabacc = perfectSabacc;
        }

        @Override
        public boolean isPlayableInFull(SwccgGame game) {
            return true;
        }

        @Override
        protected SubAction getSubAction(SwccgGame game) {
            final SubAction subAction = new SubAction(_action);

            // Record sabacc victory
            subAction.appendEffect(
                    new RecordSabaccVictoryEffect(subAction, _characterToWin));
            // Trigger to perform any responses to winning/losing a game of sabacc.
            subAction.appendEffect(
                    new TriggeringResultEffect(subAction, new SabaccWinnerDeterminedResult(subAction, _characterToWin)));

            // Determine cards to place in Used Pile or be lost
            subAction.appendEffect(
                    new PassthruEffect(subAction) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            final String losingPlayerId = game.getOpponent(_winningPlayerId);
                            Collection<PhysicalCard> losersSabaccHand = game.getGameState().getSabaccHand(losingPlayerId);
                            int numberOfCardsAffected = _perfectSabacc ? 2 : 1;
                            Collection<PhysicalCard> cardsToStealFrom = Filters.filter(losersSabaccHand, game, _stakes);
                            Collection<PhysicalCard> cardsToLoseFrom = Filters.filter(losersSabaccHand, game, Filters.not(Filters.and(_stakes)));

                            int numberOfCardsToSteal = Math.min(numberOfCardsAffected, cardsToStealFrom.size());
                            int numberOfCardsToLose = Math.min(numberOfCardsAffected - numberOfCardsToSteal, cardsToLoseFrom.size());

                            // Winner chooses cards to put to Used Pile and/or be lost from opponent's sabacc hand
                            final SubAction collectWinningsAction = new SubAction(subAction);
                            if (numberOfCardsToSteal > 0) {
                                collectWinningsAction.appendEffect(
                                        new ChooseCardsFromSabaccHandEffect(collectWinningsAction, _winningPlayerId, losingPlayerId, numberOfCardsToSteal, numberOfCardsToSteal, Filters.in(cardsToStealFrom)) {
                                            @Override
                                            protected void cardsSelected(SwccgGame game, Collection<PhysicalCard> selectedCards) {
                                                game.getGameState().sendMessage(_winningPlayerId + " chooses to place " + GameUtils.getAppendedNames(selectedCards) + " as stakes won to Used Pile");

                                                collectWinningsAction.appendEffect(
                                                        new PlaceCardsInCardPileFromSabaccHandEffect(collectWinningsAction, _winningPlayerId, losingPlayerId, selectedCards, Zone.USED_PILE, _winningPlayerId));
                                            }
                                            @Override
                                            public String getChoiceText(int numCardsToChoose) {
                                                return "Choose card" + GameUtils.s(numCardsToChoose) + " to place in your Used Pile";
                                            }
                                        }
                                );
                            }
                            if (numberOfCardsToLose > 0) {
                                collectWinningsAction.appendEffect(
                                        new ChooseCardsFromSabaccHandEffect(collectWinningsAction, _winningPlayerId, losingPlayerId, numberOfCardsToLose, numberOfCardsToLose, Filters.in(cardsToLoseFrom)) {
                                            @Override
                                            protected void cardsSelected(SwccgGame game, Collection<PhysicalCard> selectedCards) {
                                                game.getGameState().sendMessage(_winningPlayerId + " chooses " + GameUtils.getAppendedNames(selectedCards) + " to be lost");

                                                collectWinningsAction.appendEffect(
                                                        new LoseCardsFromSabaccHandEffect(collectWinningsAction, losingPlayerId, selectedCards));
                                            }
                                            @Override
                                            public String getChoiceText(int numCardsToChoose) {
                                                return "Choose card" + GameUtils.s(numCardsToChoose) + " to be lost";
                                            }
                                        }
                                );
                            }
                            subAction.stackSubAction(collectWinningsAction);
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

    /**
     * Determines if sabacc can be initiated.
     * @param game the game
     * @return true or false
     */
    private boolean canSabaccBeInitiated(SwccgGame game) {
        return !game.getGameState().isDuringBattle()
                && game.getGameState().getReserveDeckSize(game.getDarkPlayer())>=2 && game.getGameState().getReserveDeckSize(game.getLightPlayer())>=2;
    }

    /**
     * Gets the additional wild card filter for the player.
     * @param game the game
     * @param playerId the player
     * @return additional wild card filter
     */
    private Filterable getWildCardsFilterAsPerk(SwccgGame game, String playerId) {
        if (!game.getModifiersQuerying().hasFlagActive(game.getGameState(), ModifierFlag.MAY_NOT_USE_WILD_CARDS_IN_SABACC, playerId)) {
            PhysicalCard cardPlayingSabacc = playerId.equals(_action.getPerformingPlayer()) ? _sabaccPlayer : _opponentSabaccPlayer;
            if (cardPlayingSabacc != null && _wildCardPerks != null) {
                for (Filterable key : _wildCardPerks.keySet()) {
                    if (Filters.and(key).accepts(game.getGameState(), game.getModifiersQuerying(), cardPlayingSabacc))
                        return _wildCardPerks.get(key);
                }
            }
        }
        return Filters.none;
    }

    /**
     * Gets the optional clone card value for the player.
     * @param game the game
     * @param playerId the player
     * @return optional clone card value, or null
     */
    private Integer getCloneCardValueAsPerk(SwccgGame game, String playerId) {
        PhysicalCard cardPlayingSabacc = playerId.equals(_action.getPerformingPlayer()) ? _sabaccPlayer : _opponentSabaccPlayer;
        if (cardPlayingSabacc != null && _cloneCardPerks != null) {
            for (Filterable key : _cloneCardPerks.keySet()) {
                if (Filters.and(key).accepts(game.getGameState(), game.getModifiersQuerying(), cardPlayingSabacc))
                    return _cloneCardPerks.get(key);
            }
        }
        return null;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return _sabaccInitiated;
    }
}
