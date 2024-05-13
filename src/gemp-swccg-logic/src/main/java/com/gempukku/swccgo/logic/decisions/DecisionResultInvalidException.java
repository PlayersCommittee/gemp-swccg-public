package com.gempukku.swccgo.logic.decisions;

/**
 * An exception that is to be thrown when an invalid decision result is made.
 */
public class DecisionResultInvalidException extends Exception {
    private String _warningMessage;

    /**
     * Creates an exception that is to be thrown when an invalid decision result is made with a default message.
     */
    public DecisionResultInvalidException() {
        this("Something went wrong");
    }

    /**
     * Creates an exception that is to be thrown when an invalid decision result is made with the specified warning message.
     * @param warningMessage the message
     */
    public DecisionResultInvalidException(String warningMessage) {
        _warningMessage = warningMessage;
    }

    /**
     * Gets the warning message.
     * @return the warning message
     */
    public String getWarningMessage() {
        return _warningMessage;
    }
}
