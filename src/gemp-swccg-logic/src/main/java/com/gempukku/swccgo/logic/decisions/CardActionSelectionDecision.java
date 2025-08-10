package com.gempukku.swccgo.logic.decisions;

import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.List;

/**
 * A decision that involves choosing a card from the table (or hand) on the User Interface to perform an action.
 */
public abstract class CardActionSelectionDecision extends AbstractAwaitingDecision {
    private List<Action> _actions;

    /**
     * Creates a decision that involves choosing a card from the table (or hand) on the User Interface to perform an action.
     * @param decisionId the decision id
     * @param text the text to show the player making the decision
     * @param actions the actions to choose from
     * @param yourTurn true if the decision is during the player's turn, otherwise false
     * @param autoPassEligible true if the decision is auto-pass eligible, otherwise false
     * @param noPass true if passing is not an option, otherwise false
     * @param noLongDelay true if no long delay should be used when mimic decision time when no actions, otherwise false
     * @param revertEligible true if choosing to revert to a previous game state is supported during this decision, otherwise false
     */
    public CardActionSelectionDecision(int decisionId, String text, List<Action> actions, boolean yourTurn, boolean autoPassEligible, boolean noPass, boolean noLongDelay, boolean revertEligible) {
        super(decisionId, text, AwaitingDecisionType.CARD_ACTION_CHOICE);
        _actions = actions;
        setParam("actionId", getActionIds(actions));
        setParam("cardId", getCardIds(actions));
        setParam("blueprintId", getBlueprintIdsForVirtualActions(actions));
        setParam("testingText", getTestingTextsForVirtualActions(actions));
        setParam("backSideTestingText", getBackSideTestingTextsForVirtualActions(actions));
        setParam("actionText", getActionTexts(actions));
        setParam("yourTurn", String.valueOf(yourTurn));
        setParam("autoPassEligible", String.valueOf(autoPassEligible));
        setParam("noPass", String.valueOf(noPass));
        setParam("noLongDelay", String.valueOf(noLongDelay));
        setParam("revertEligible", String.valueOf(revertEligible));
    }

    /**
     * For testing, being able to inject an extra action at any point.
     *
     * @param action
     */
    public void addAction(Action action) {
        _actions.add(action);
    }

    /**
     * Gets the temp action ids
     * @param actions the actions
     * @return the temp action ids
     */
    private String[] getActionIds(List<Action> actions) {
        String[] result = new String[actions.size()];
        for (int i = 0; i < result.length; i++)
            result[i] = String.valueOf(i);
        return result;
    }

    /**
     * Gets an array of card ids.
     * @param actions the actions
     * @return the card ids
     */
    private String[] getCardIds(List<Action> actions) {
        String[] result = new String[actions.size()];
        for (int i = 0; i < result.length; i++) {
            Action action = actions.get(i);
            if (action.getActionAttachedToCard() == null) {
                throw new UnsupportedOperationException("Null card id in CardActionSelectionDecision; Type: " + action.getType() + "; Text:" + action.getText() + "; Player:" + action.getPerformingPlayer() + "; Class: " + action.getClass().getSimpleName());
            }
            result[i] = String.valueOf(action.getActionAttachedToCard().getCardId());
        }
        return result;
    }

    /**
     * Gets the card blueprint ids in case the card is not currently on the table (or hand).
     * @param actions the actions
     * @return the card blueprint ids
     */
    private String[] getBlueprintIdsForVirtualActions(List<Action> actions) {
        String[] result = new String[actions.size()];
        for (int i = 0; i < result.length; i++) {
            Action action = actions.get(i);
            if (action.isOptionalOffTableCardAction())
                result[i] = String.valueOf(action.getActionSource().getBlueprintId(true));
            else
                result[i] = "inPlay";
        }
        return result;
    }

    /**
     * Gets the card testing texts.
     * @param actions the actions
     * @return the card testing texts
     */
    private String[] getTestingTextsForVirtualActions(List<Action> actions) {
        String[] result = new String[actions.size()];
        for (int i = 0; i < result.length; i++) {
            PhysicalCard physicalCard = actions.get(i).getActionAttachedToCard();
            if (physicalCard != null)
                result[i] = String.valueOf(physicalCard.getTestingText(null, physicalCard.getBlueprint().getCardCategory() != CardCategory.OBJECTIVE, false));
            else
                result[i] = "null";
        }
        return result;
    }

    /**
     * Gets the card backside testing texts.
     * @param actions the actions
     * @return the card testing texts
     */
    private String[] getBackSideTestingTextsForVirtualActions(List<Action> actions) {
        String[] result = new String[actions.size()];
        for (int i = 0; i < result.length; i++) {
            PhysicalCard physicalCard = actions.get(i).getActionAttachedToCard();
            if (physicalCard != null)
                result[i] = String.valueOf(physicalCard.getTestingText(null, physicalCard.getBlueprint().getCardCategory() != CardCategory.OBJECTIVE, true));
            else
                result[i] = "null";
        }
        return result;
    }

    /**
     * Gets the action texts
     * @param actions the actions
     * @return the texts to show
     */
    private String[] getActionTexts(List<Action> actions) {
        String[] result = new String[actions.size()];
        for (int i = 0; i < result.length; i++)
            result[i] = actions.get(i).getText();
        return result;
    }

    /**
     * Gets the action the player selected during the decision.
     * @param result the result
     * @return the action selected
     * @throws DecisionResultInvalidException
     */
    protected Action getSelectedAction(String result) throws DecisionResultInvalidException {
        if (result.isEmpty())
            return null;

        try {
            int actionIndex = Integer.parseInt(result);
            if (actionIndex < 0 || actionIndex >= _actions.size())
                throw new DecisionResultInvalidException();

            return _actions.get(actionIndex);
        } catch (NumberFormatException exp) {
            throw new DecisionResultInvalidException();
        }
    }
}
