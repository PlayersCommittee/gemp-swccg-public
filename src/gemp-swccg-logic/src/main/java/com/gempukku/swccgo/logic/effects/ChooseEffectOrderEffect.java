package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.timing.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * An effect that causes the current player to choice which effect to perform next from a list of choices.
 */
public class ChooseEffectOrderEffect extends AbstractSubActionEffect {
    private List<StandardEffect> _remainingEffects;
    private boolean _shortcut;

    /**
     * Creates an effect that causes the current player to choice which effect to perform next from a list of choices.
     * @param action the action performing this effect
     * @param effectsToOrder the effects to choose the order performed
     */
    public ChooseEffectOrderEffect(Action action, List<StandardEffect> effectsToOrder) {
        this(action, effectsToOrder, true);
    }

    /**
     * Creates an effect that causes the current player to choice which effect to perform next from a list of choices.
     * @param action the action performing this effect
     * @param effectsToOrder the effects to choose the order performed
     * @param shortcut true if shortcut, otherwise false
     */
    public ChooseEffectOrderEffect(Action action, List<StandardEffect> effectsToOrder, boolean shortcut) {
        super(action);
        _remainingEffects = new ArrayList<StandardEffect>(effectsToOrder);
        _shortcut = shortcut;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        for (Effect effect : _remainingEffects) {
            if (effect.isPlayableInFull(game))
                return true;
        }
        return false;
    }

    @Override
    protected SubAction getSubAction(final SwccgGame game) {
        final SubAction subAction = new SubAction(_action);
        if (!_remainingEffects.isEmpty()) {
            subAction.appendEffect(
                    new ChooseNextEffect(subAction, game.getGameState().getCurrentPlayerId(), _remainingEffects));
        }
        return subAction;
    }

    /**
     * Sets that effect are just process in specified order instead of asking current player to choose.
     * @param shortcut true if shortcut, otherwise false
     */
    public void setShortcut(boolean shortcut) {
        _shortcut = shortcut;
    }

    protected String getChoiceText() {
        return "Choose effect to be performed first";
    }

    /**
     * A private effect for choosing the next effect to perform.
     */
    private class ChooseNextEffect extends AbstractSuccessfulEffect {
        private SubAction _subAction;
        private String _playerId;
        private List<StandardEffect> _remainingEffects;

        /**
         * Creates an effect for choosing the next effect to perform.
         * @param subAction the action
         * @param playerId the player
         * @param remainingEffects the remaining effects to perform
         */
        public ChooseNextEffect(SubAction subAction, String playerId, List<StandardEffect> remainingEffects) {
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
                if (_shortcut) {
                    StandardEffect effectToPerform = possibleEffects.get(0);
                    _subAction.appendEffect(effectToPerform);
                    // Check if more effect left to perform
                    _remainingEffects.remove(effectToPerform);
                    if (!_remainingEffects.isEmpty()) {
                        _subAction.appendEffect(
                                new ChooseNextEffect(_subAction, _playerId, _remainingEffects));
                    }
                }
                else {
                    game.getUserFeedback().sendAwaitingDecision(game.getGameState().getCurrentPlayerId(),
                            new MultipleChoiceAwaitingDecision(getChoiceText(), getEffectsText(possibleEffects, game)) {
                                @Override
                                protected void validDecisionMade(int index, String result) {
                                    StandardEffect effectToPerform = possibleEffects.get(index);
                                    _subAction.appendEffect(effectToPerform);
                                    // Check if more effect left to perform
                                    _remainingEffects.remove(effectToPerform);
                                    if (!_remainingEffects.isEmpty()) {
                                        _subAction.appendEffect(
                                                new ChooseNextEffect(_subAction, _playerId, _remainingEffects));
                                    }
                                }
                            });
                }
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