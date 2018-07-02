package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that causes the specified player to choose a player by choosing a side of the Force.
 */
public class ChoosePlayerBySideEffect extends AbstractChoosePlayerEffect {

    /**
     * Creates an effect that causes the specified player to choose a player by choosing a side of the Force.
     * @param action the action performing this effect
     * @param playerId the player to make the choice
     */
    public ChoosePlayerBySideEffect(Action action, String playerId) {
        super(action, playerId);
    }

    protected String getChoiceText() {
        return "Choose player";
    }

    @Override
    protected FullEffectResult playEffectReturningResult(final SwccgGame game) {
        game.getUserFeedback().sendAwaitingDecision(_playerToMakeChoice,
                new MultipleChoiceAwaitingDecision(getChoiceText(), new String[]{"Dark side", "Light side"}) {
                    @Override
                    protected void validDecisionMade(int index, String result) {
                        String playerChosen = (index == 0) ? game.getDarkPlayer() : game.getLightPlayer();
                        setPlayerChosen(game, playerChosen);
                    }
                });
        return new FullEffectResult(true);
    }
}
