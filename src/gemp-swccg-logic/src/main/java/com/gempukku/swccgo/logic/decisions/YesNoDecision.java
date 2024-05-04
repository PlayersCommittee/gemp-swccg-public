package com.gempukku.swccgo.logic.decisions;

/**
 * A decision that involves choosing Yes or No on the User Interface.
 */
public class YesNoDecision extends MultipleChoiceAwaitingDecision {

    /**
     * Creates a decision that involves choosing Yes or No on the User Interface.
     * @param text the text to show the player making the decision
     */
    public YesNoDecision(String text) {
        super(text, new String[]{"Yes", "No"});
    }

    @Override
    protected final void validDecisionMade(int index, String result) {
        if (index == 0)
            yes();
        else
            no();
    }

    /**
     * This method is called if the choice was "Yes".
     */
    protected void yes() {
    }

    /**
     * This method is called if the choice was "No".
     */
    protected void no() {
    }
}
