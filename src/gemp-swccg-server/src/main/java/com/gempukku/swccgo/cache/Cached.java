package com.gempukku.swccgo.cache;

/**
 * An interface for cached data.
 */
public interface Cached {

    /**
     * Clears the data in the cache.
     */
    void clearCache();

    /**
     * Gets the number of items in the cache.
     * @return the number of items
     */
    int getItemCount();
}
