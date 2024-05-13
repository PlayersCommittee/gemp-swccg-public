package com.gempukku.swccgo.game;

/**
 * This class is an exception that is to be thrown when a deck is detected as being invalid.
 */
public class DeckInvalidException extends Exception {

    /**
     * Creates an exception that is to be thrown when a deck is detected as being invalid.
     * @param message the message in the exception
     */
    public DeckInvalidException(String message) {
        super(message);
    }
}
