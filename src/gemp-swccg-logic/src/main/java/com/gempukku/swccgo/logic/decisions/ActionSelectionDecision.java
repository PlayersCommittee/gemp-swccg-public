package com.gempukku.swccgo.logic.decisions;

import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.List;

/**
 * A decision that involves choosing which action to perform by choosing the action source from a pop-up window in the User Interface.
 */
public abstract class ActionSelectionDecision extends AbstractAwaitingDecision {
    private List<Action> _actions;

    /**
     * Creates a decision that involves choosing which action perform.
     * @param text the text to show the player making the decision
     * @param actions the actions to choose from
     */
    public ActionSelectionDecision(String text, List<Action> actions) {
        super(1, text, AwaitingDecisionType.ACTION_CHOICE);
        _actions = actions;

        setParam("actionId", getActionIds(actions));
        setParam("blueprintId", getBlueprintIds(actions));
        setParam("testingText", getTestingTexts(actions));
        setParam("backSideTestingText", getBackSideTestingTexts(actions));
        setParam("horizontal", getHorizontals(actions));
        setParam("actionText", getActionTexts(actions));
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
     * Gets the card blueprint ids.
     * @param actions the actions
     * @return the card blueprint ids
     */
    private String[] getBlueprintIds(List<Action> actions) {
        String[] result = new String[actions.size()];
        for (int i = 0; i < result.length; i++) {
            PhysicalCard physicalCard = actions.get(i).getActionAttachedToCard();
            if (physicalCard != null)
                result[i] = String.valueOf(physicalCard.getBlueprintId(physicalCard.getBlueprint().getCardCategory() != CardCategory.OBJECTIVE));
            else
                result[i] = "rules";
        }
        return result;
    }

    /**
     * Gets the card testing texts.
     * @param actions the actions
     * @return the card testing texts
     */
    private String[] getTestingTexts(List<Action> actions) {
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
    private String[] getBackSideTestingTexts(List<Action> actions) {
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
     * Gets the card horizontals.
     * @param actions the actions
     * @return the card horizontals
     */
    private String[] getHorizontals(List<Action> actions) {
        String[] result = new String[actions.size()];
        for (int i = 0; i < result.length; i++) {
            PhysicalCard physicalCard = actions.get(i).getActionAttachedToCard();
            if (physicalCard != null)
                result[i] = String.valueOf(physicalCard.getBlueprint().isHorizontal());
            else
                result[i] = "false";
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
            throw new DecisionResultInvalidException();

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
