package com.gempukku.polling;

/**
 * Interface for a long polling resource.
 */
public interface LongPollingResource {

    /**
     * Determines if it was processed.
     * @return true if processed, otherwise false
     */
    boolean wasProcessed();

    /**
     * Process if it was not already processed.
     */
    void processIfNotProcessed();
}
