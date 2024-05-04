package com.gempukku.swccgo.cards.actions;

import com.gempukku.swccgo.logic.actions.SystemQueueAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.StackActionEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.List;

/**
 * The action to choose an action from multiple action options.
 */
public class ChoiceAction extends SystemQueueAction {
    private List<Action> _actionChoices;
    private Action _that;

    /**
     * Creates an action for the specified player to choose which way to play a card from multiple play options.
     * @param playerId the player to choose the action
     * @param msgText the message text to show for the action choice
     * @param actionChoices the action choices
     */
    public ChoiceAction(String playerId, String msgText, final List<Action> actionChoices) {
        _actionChoices = actionChoices;
        _that = this;

        String[] actionChoiceTexts = new String[_actionChoices.size()];
        for (int i=0; i<actionChoiceTexts.length; ++i) {
            actionChoiceTexts[i] = _actionChoices.get(i).getText();
        }
        appendEffect(
                new PlayoutDecisionEffect(_that, playerId,
                        new MultipleChoiceAwaitingDecision(msgText, actionChoiceTexts) {
                            @Override
                            protected void validDecisionMade(int index, String result) {
                                Action actionChosen = _actionChoices.get(index);
                                appendEffect(
                                        new StackActionEffect(_that, actionChosen));
                            }
                        }));
    }
}
