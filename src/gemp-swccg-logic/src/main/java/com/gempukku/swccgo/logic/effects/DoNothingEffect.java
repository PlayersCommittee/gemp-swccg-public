package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that does nothing.
 */
public class DoNothingEffect extends AbstractSuccessfulEffect {

    /**
     * Creates an effect that does nothing.
     * @param action the action performing this effect
     */
    public DoNothingEffect(Action action) {
        super(action);
    }

    @Override
    public String getText(SwccgGame game) {
        return "Do nothing";
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        game.getGameState().sendMessage(_action.getPerformingPlayer() + " choose to do nothing");
    }
}
