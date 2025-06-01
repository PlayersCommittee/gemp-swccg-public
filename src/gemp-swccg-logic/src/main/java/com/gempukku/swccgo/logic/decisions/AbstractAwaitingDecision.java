package com.gempukku.swccgo.logic.decisions;

import java.util.HashMap;
import java.util.Map;

/**
 * The abstract class that defines the based implementation for a decision a player need to make.
 */
public abstract class AbstractAwaitingDecision implements AwaitingDecision {
    private final int _id;
    private final String _text;
    private final AwaitingDecisionType _decisionType;
    private final Map<String, String[]> _params = new HashMap<>();

    /**
     * Creates an awaiting decision of the specified type, and with the specified id and text.
     * @param id the id
     * @param text the text to show the player making the decision
     * @param decisionType the decision type
     */
    public AbstractAwaitingDecision(int id, String text, AwaitingDecisionType decisionType) {
        _id = id;
        _text = text;
        _decisionType = decisionType;
    }

    /**
     * Sets the specified parameter to the specified string value.
     * @param name the parameter name
     * @param value the value
     */
    protected void setParam(String name, String value) {
        _params.put(name, new String[] {value});
    }

    /**
     * Sets the specified parameter to the specified string array value.
     * @param name the parameter name
     * @param value the value
     */
    protected void setParam(String name, String[] value) {
        _params.put(name, value);
    }

    @Override
    public int getAwaitingDecisionId() {
        return _id;
    }

    @Override
    public String getText() {
        return _text;
    }

    @Override
    public AwaitingDecisionType getDecisionType() {
        return _decisionType;
    }

    @Override
    public Map<String, String[]> getDecisionParameters() {
        return _params;
    }
}
