package com.gempukku.swccgo.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Guarantees that only one instance of the object will exists, via a synchronized double-check.
 *
 * @param <T>
 * @param <U>
 */
public class ExpireObjectCache<T, U> {
    private Map<T, U> _cachedObjects = new ConcurrentHashMap<T, U>();

    public U getCachedObject(T key, Producable<T, U> producable) {
        U result = _cachedObjects.get(key);
        if (result != null)
            return result;

        synchronized (this) {
            result = _cachedObjects.get(key);
            if (result != null)
                return result;

            result = producable.produce(key);
            _cachedObjects.put(key, result);
            return result;
        }
    }

    public synchronized void clearCache() {
        _cachedObjects.clear();
    }
}
