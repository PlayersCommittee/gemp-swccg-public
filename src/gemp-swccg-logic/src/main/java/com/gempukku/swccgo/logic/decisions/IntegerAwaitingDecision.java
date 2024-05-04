package com.gempukku.swccgo.logic.decisions;

/**
 * A decision that involves choosing an integer on the User Interface.
 */
public abstract class IntegerAwaitingDecision extends AbstractAwaitingDecision {
    private Integer _min;
    private Integer _max;

    /**
     * Creates a decision that involves choosing an integer in the specified range on the User Interface.
     * @param text the text to show the player making the decision
     * @param min the minimum integer in the range
     * @param max the maximum integer in the range
     * @param defaultValue the default value
     */
    public IntegerAwaitingDecision(String text, Integer min, Integer max, Integer defaultValue) {
        super(1, text, AwaitingDecisionType.INTEGER);
        _min = min;
        _max = max;
        if (min != null)
            setParam("min", min.toString());
        if (max != null)
            setParam("max", max.toString());
        if (defaultValue != null)
            setParam("defaultValue", defaultValue.toString());
    }

    /**
     * Gets the integer selected during the decision.
     * @param result the result
     * @return the integer selected
     * @throws DecisionResultInvalidException
     */
    private int getValidatedResult(String result) throws DecisionResultInvalidException {
        try {
            int value = Integer.parseInt(result);
            if (_min != null && _min > value)
                throw new DecisionResultInvalidException();
            if (_max != null && _max < value)
                throw new DecisionResultInvalidException();

            return value;
        } catch (NumberFormatException exp) {
            throw new DecisionResultInvalidException();
        }
    }

    @Override
    public final void decisionMade(String result) throws DecisionResultInvalidException {
        decisionMade(getValidatedResult(result));
    }

    /**
     * This method is called when the integer is chosen.
     * @param result the chosen integer
     */
    public abstract void decisionMade(int result) throws DecisionResultInvalidException;
}
