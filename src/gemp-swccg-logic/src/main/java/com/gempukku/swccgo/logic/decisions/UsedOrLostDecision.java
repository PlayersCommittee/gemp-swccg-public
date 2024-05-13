package com.gempukku.swccgo.logic.decisions;

import com.gempukku.swccgo.common.CardSubtype;

/**
 * A decision that involves choosing between Used and Lost on the User Interface.
 */
public abstract class UsedOrLostDecision extends MultipleChoiceAwaitingDecision {

    /**
     * Creates a decision that involves choosing between Used and Lost on the User Interface.
     * @param text the text to show the player making the decision
     */
    public UsedOrLostDecision(String text) {
        super(text, new String[]{"Used", "Lost"});
    }

    @Override
    protected final void validDecisionMade(int index, String result) {
        if (index == 0)
            typeChosen(CardSubtype.USED);
        else
            typeChosen(CardSubtype.LOST);
    }

    /**
     * This method is called when the choice is made.
     * @param subtype the subtype, Used or Lost, that was chosen
     */
    protected abstract void typeChosen(CardSubtype subtype);
}
