package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.effects.choose.ChoosePlayerBySideEffect;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that causes the specified player to choose a player to predict the winning player.
 */
public abstract class PredictWinnerEffect extends ChoosePlayerBySideEffect {

    /**
     * Creates an effect that causes the specified player to choose a player to predict the winning player
     * @param action the action performing this effect
     * @param playerId the player to predict the winner
     */
    public PredictWinnerEffect(Action action, String playerId) {
        super(action, playerId);
    }

    @Override
    protected String getChoiceText() {
        return "Predict winner";
    }

    /**
     * A callback method for the player chosen.
     * @param game the game
     * @param playerId the player chosen
     */
    @Override
    protected final void playerChosen(SwccgGame game, String playerId) {
        game.getGameState().sendMessage(_playerToMakeChoice + " predicts " + playerId + " to be the winner");
        winnerPredicted(playerId);
    }

    /**
     * A callback method for the predicted winner.
     * @param predictedWinner the predicted winner
     */
    protected abstract void winnerPredicted(String predictedWinner);
}
