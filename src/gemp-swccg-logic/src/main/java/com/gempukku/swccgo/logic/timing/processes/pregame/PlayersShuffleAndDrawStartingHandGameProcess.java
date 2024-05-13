package com.gempukku.swccgo.logic.timing.processes.pregame;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.ModifierFlag;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.processes.GameProcess;
import com.gempukku.swccgo.logic.timing.processes.turn.BetweenTurnsProcess;

/**
 * The game process for shuffling deck and drawing starting hands.
 */
public class PlayersShuffleAndDrawStartingHandGameProcess implements GameProcess {
    private String _darkPlayerId;
    private String _lightPlayerId;
    private GameProcess _followingGameProcess;

    /**
     * Creates the game process for shuffling deck and drawing starting hands.
     * @param game the game
     */
    public PlayersShuffleAndDrawStartingHandGameProcess(SwccgGame game) {
        _darkPlayerId = game.getDarkPlayer();
        _lightPlayerId = game.getLightPlayer();
        _followingGameProcess = new BetweenTurnsProcess();
    }

    @Override
    public void process(SwccgGame game) {

        // set current player opposite of player with first turn
        GameState gameState = game.getGameState();
        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
        if (modifiersQuerying.hasFlagActive(gameState, ModifierFlag.LIGHT_SIDE_GOES_FIRST)) {
            gameState.setFirstPlayer(_lightPlayerId);
            gameState.sendMessage("Light Side goes first");
        }
        else {
            gameState.setFirstPlayer(_darkPlayerId);
            gameState.sendMessage("Dark Side goes first");
        }

        for (String player : gameState.getPlayerOrder().getAllPlayers()) {

            // Shuffle Reserve Deck
            gameState.sendMessage(player + " shuffles " + player + "'s " + Zone.RESERVE_DECK.getHumanReadable());
            gameState.shuffleReserveDeck(player);

            // Draw starting hand
            int openingHandSize = modifiersQuerying.getNumCardsToDrawInStartingHand(gameState, player);
            gameState.sendMessage(player + " draws "  + openingHandSize + " cards into hand");
            gameState.playerDrawsCardsIntoStartingHandFromReserveDeck(player, openingHandSize);
        }
        // clear any modifiers, counters, action proxies that may exist prior to starting first turn
        game.getModifiersEnvironment().removeEndOfTurnModifiers();
        game.getModifiersEnvironment().removeEndOfTurnCounters();
        game.getActionsEnvironment().removeEndOfTurnActionProxies();
    }

    @Override
    public GameProcess getNextProcess() {
        return _followingGameProcess;
    }
}
