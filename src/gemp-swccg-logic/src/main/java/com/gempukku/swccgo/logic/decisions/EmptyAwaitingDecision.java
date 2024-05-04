package com.gempukku.swccgo.logic.decisions;


/**
 * This class is used to send a "fake" decision to the player that the browser will
 * automatically response without player intervention.
 */
public class EmptyAwaitingDecision extends AbstractAwaitingDecision {

    /**
     * Creates an empty decision with a default timeout.
     */
    public EmptyAwaitingDecision() {
        this(1000);
    }

    /**
     * Creates an empty decision with a specified timeout.
     */
    public EmptyAwaitingDecision(int timeoutInMs) {
        super(1, "EMPTY", AwaitingDecisionType.EMPTY);
        setParam("timeoutValue", String.valueOf(timeoutInMs));
    }

    @Override
    public void decisionMade(String result) throws DecisionResultInvalidException {
    }
}
