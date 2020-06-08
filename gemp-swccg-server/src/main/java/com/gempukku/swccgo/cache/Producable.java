package com.gempukku.swccgo.cache;

public interface Producable<T, U> {
    U produce(T key);
}
