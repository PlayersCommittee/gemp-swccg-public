package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.AbstractStandardEffect;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An abstract effect for choosing a player.
 */
public abstract class AbstractChoosePlayerEffect extends AbstractStandardEffect implements ChoosePlayerEffect {
    protected String _playerToMakeChoice;
    private String _playerChosen;

    /**
     * Creates an effect that causes the specified player to choose a player.
     * @param action the action performing this effect
     * @param playerId the player to make the choice
     */
    protected AbstractChoosePlayerEffect(Action action, String playerId) {
        super(action);
        _playerToMakeChoice = playerId;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    public boolean wasCarriedOut() {
        return _playerChosen != null;
    }

    /**
     * Gets the player chosen.
     * @return the player chosen
     */
    public String getPlayerChosen() {
        return _playerChosen;
    }

    /**
     * Sets the player chosen.
     * @param game the game
     * @param playerId the player chosen
     */
    protected final void setPlayerChosen(SwccgGame game, String playerId) {
        _playerChosen = playerId;
        playerChosen(game, playerId);
    }

    /**
     * A callback method for the player chosen.
     * @param game the game
     * @param playerId the player chosen
     */
    protected void playerChosen(SwccgGame game, String playerId) {
    }
}

