package com.gempukku.swccgo.logic.decisions;

import java.util.Map;

/**
 * The interface for a decision to be made by a player.
 */
public interface AwaitingDecision {

    /**
     * Gets the awaiting decision id.
     * @return the id
     */
    int getAwaitingDecisionId();

    /**
     * Gets the text to show the player making the decision.
     * @return the text
     */
    String getText();

    /**
     * Gets the decision type.
     * @return the decision type
     */
    AwaitingDecisionType getDecisionType();

    /**
     * Gets the decision parameters.
     * @return the decision parameters
     */
    Map<String, String[]> getDecisionParameters();

    /**
     * This method is called with the result of the players decision.
     * @param result the decision result
     * @throws DecisionResultInvalidException
     */
    void decisionMade(String result) throws DecisionResultInvalidException;
}
