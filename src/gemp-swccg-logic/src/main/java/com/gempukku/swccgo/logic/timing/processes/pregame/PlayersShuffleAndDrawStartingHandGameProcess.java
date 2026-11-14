package com.gempukku.swccgo.logic.timing.processes.pregame;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.modifiers.ModifierFlag;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.processes.GameProcess;
import com.gempukku.swccgo.logic.timing.processes.turn.BetweenTurnsProcess;

import java.util.Collection;

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

        GameState gameState = game.getGameState();
        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

        // The starting phase (starting effects, locations/objectives, and starting interrupts) is now complete.
        // Place any cards that are not allowed in Reserve Deck that are still in Reserve Deck out of play. This runs
        // here (after starting interrupts) rather than earlier so that a card allowed to be deployed at game start by a
        // starting interrupt from Reserve Deck (e.g. a double-sided Effect) can be deployed first, and is only removed
        // if it was not deployed. Runs before shuffling/drawing so such a card can never be drawn into a starting hand.
        for (String player : gameState.getPlayerOrder().getAllPlayers()) {
            Collection<PhysicalCard> invalidCards = Filters.filter(gameState.getReserveDeck(player), game, Filters.mayNotBePlacedInReserveDeck);
            if (!invalidCards.isEmpty()) {
                gameState.sendMessage(GameUtils.getAppendedNames(invalidCards) + " " + GameUtils.be(invalidCards)
                        + " placed out of play due to not being allowed to be placed in Reserve Deck");
                gameState.removeCardsFromZone(invalidCards);
                for (PhysicalCard card : invalidCards) {
                    gameState.addCardToZone(card, Zone.OUT_OF_PLAY, card.getOwner());
                }
            }
        }

        // set current player opposite of player with first turn
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
