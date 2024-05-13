package com.gempukku.util;

import java.util.Comparator;

/**
 * A comparator that sorts in descending order.
 * @param <T> the object type to compare
 */
public class DescComparator<T> implements Comparator<T> {
    private Comparator<T> _comparator;

    /**
     * Creates comparator that sorts using the specified comparator in descending order.
     * @param comparator a comparator
     */
    public DescComparator(Comparator<T> comparator) {
        _comparator = comparator;
    }

    @Override
    public int compare(T o1, T o2) {
        return _comparator.compare(o2, o1);
    }
}
