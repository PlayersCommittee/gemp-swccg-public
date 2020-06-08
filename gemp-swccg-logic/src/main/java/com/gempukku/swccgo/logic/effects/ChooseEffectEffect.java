package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.timing.*;

import java.util.LinkedList;
import java.util.List;

/**
 * An effect that causes the specified player to choice which effect to perform from a list of choices.
 */
public class ChooseEffectEffect extends AbstractSubActionEffect {
    private String _choicePlayerId;
    private List<StandardEffect> _possibleEffects;

    /**
     * Creates an effect that causes the specified player to choice which effect to perform from a list of choices.
     * @param action the action performing this effect
     * @param choicePlayerId the player
     * @param possibleEffects the effects to choose from
     */
    public ChooseEffectEffect(Action action, String choicePlayerId, List<StandardEffect> possibleEffects) {
        super(action);
        _choicePlayerId = choicePlayerId;
        _possibleEffects = possibleEffects;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        for (Effect effect : _possibleEffects) {
            if (effect.isPlayableInFull(game))
                return true;
        }
        return false;
    }

    @Override
    protected SubAction getSubAction(final SwccgGame game) {
        final SubAction subAction = new SubAction(_action);
        if (!_possibleEffects.isEmpty()) {
            subAction.appendEffect(
                    new ChooseEffect(subAction, _choicePlayerId, _possibleEffects));
        }
        return subAction;
    }

    protected String getChoiceText() {
        return "Choose effect to be performed";
    }

    /**
     * A private effect for choosing the next effect to perform.
     */
    private class ChooseEffect extends AbstractSuccessfulEffect {
        private SubAction _subAction;
        private String _playerId;
        private List<StandardEffect> _remainingEffects;

        /**
         * Creates an effect for choosing the next effect to perform.
         * @param subAction the action
         * @param playerId the player
         * @param remainingEffects the remaining effects to perform
         */
        public ChooseEffect(SubAction subAction, String playerId, List<StandardEffect> remainingEffects) {
            super(subAction);
            _subAction = subAction;
            _playerId = playerId;
            _remainingEffects = remainingEffects;
        }

        @Override
        protected void doPlayEffect(SwccgGame game) {
            final List<StandardEffect> possibleEffects = new LinkedList<StandardEffect>();
            for (StandardEffect effect : _remainingEffects) {
                if (effect.isPlayableInFull(game))
                    possibleEffects.add(effect);
            }

            if (possibleEffects.size() == 1) {
                _subAction.appendEffect(possibleEffects.get(0));
            }
            else if (!possibleEffects.isEmpty()) {
                game.getUserFeedback().sendAwaitingDecision(_playerId,
                        new MultipleChoiceAwaitingDecision(getChoiceText(), getEffectsText(possibleEffects, game)) {
                            @Override
                            protected void validDecisionMade(int index, String result) {
                                StandardEffect effectToPerform = possibleEffects.get(index);
                                _subAction.appendEffect(effectToPerform);
                            }
                        });
            }
        }
    }

    private String[] getEffectsText(List<StandardEffect> possibleEffects, SwccgGame game) {
        String[] result = new String[possibleEffects.size()];
        for (int i = 0; i < result.length; i++)
            result[i] = possibleEffects.get(i).getText(game);
        return result;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }
}