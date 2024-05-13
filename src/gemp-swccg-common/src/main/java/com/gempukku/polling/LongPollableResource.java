package com.gempukku.polling;

/**
 * Interface for a long pollable resource.
 */
public interface LongPollableResource {
    /**
     * Registers the request for changes, however if there are any changes that can be consumed immediately, then
     * true is returned.
     * @param waitingRequest the request to register
     * @return true if any changes can be consumed immediately, otherwise false
     */
    boolean registerRequest(WaitingRequest waitingRequest);

    /**
     * Unregisters the request.
     * @param waitingRequest the request to unregister
     */
    void unregisterRequest(WaitingRequest waitingRequest);
}
