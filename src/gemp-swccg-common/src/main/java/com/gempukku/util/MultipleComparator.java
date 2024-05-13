package com.gempukku.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A comparator that sorts using comparators in order until a difference is found.
 * @param <T> the object type to compare
 */
public class MultipleComparator<T> implements Comparator<T> {
    private List<Comparator<T>> _comparators = new ArrayList<Comparator<T>>();

    /**
     * Creates a comparator that sorts using the specified comparators until a difference is found.
     * @param comparators the comparators
     */
    public MultipleComparator(Comparator<T>... comparators) {
        Collections.addAll(_comparators, comparators);
    }

    /**
     * Adds a comparator to use for sorting.
     * @param comparator a comparator
     */
    public void addComparator(Comparator<T> comparator) {
        _comparators.add(comparator);
    }

    @Override
    public int compare(T o1, T o2) {
        for (Comparator<T> comparator : _comparators) {
            int result = comparator.compare(o1, o2);
            if (result != 0)
                return result;
        }
        return 0;
    }
}
