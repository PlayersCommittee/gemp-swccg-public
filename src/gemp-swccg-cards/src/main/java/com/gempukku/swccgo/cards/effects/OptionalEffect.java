package com.gempukku.swccgo.cards.effects;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.StandardEffect;

public class OptionalEffect extends AbstractSubActionEffect {
    private String _playerId;
    private StandardEffect _optionalEffect;

    public OptionalEffect(Action action, String playerId, StandardEffect optionalEffect) {
        super(action);
        _playerId = playerId;
        _optionalEffect = optionalEffect;
    }

    @Override
    public String getText(SwccgGame game) {
        return null;
    }

    @Override
    public Type getType() {
        return null;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        // TODO: Fix this
        return null;
    }

    @Override
    public void playEffect(final SwccgGame game) {
        if (_optionalEffect.isPlayableInFull(game))
            game.getUserFeedback().sendAwaitingDecision(_playerId,
                    new MultipleChoiceAwaitingDecision("Do you wish to " + _optionalEffect.getText(game) + "?", new String[]{"Yes", "No"}) {
                        @Override
                        protected void validDecisionMade(int index, String result) {
                            if (index == 0) {
                                SubAction subAction = new SubAction(_action);
                                subAction.appendEffect(_optionalEffect);

                                // TODO: Fix this
                                // processSubAction(game, subAction);
                            }
                        }
                    });
    }

    @Override
    protected boolean wasActionCarriedOut() {

        // TODO: Fix this
        return true;
    }
}
