package com.gempukku.swccgo.logic.modifiers;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * This class collects modifiers.
 */

public class ModifierCollectorImpl implements ModifierCollector {
    private List<Modifier> _currentModifiers = new LinkedList<Modifier>();
    private List<Modifier> _prevModifiers = new LinkedList<Modifier>();

    @Override
    public void clear() {
        _prevModifiers.addAll(_currentModifiers);
        _currentModifiers.clear();
    }

    @Override
    public void addModifier(Modifier modifier) {
        if (!_currentModifiers.contains(modifier)) {
            _currentModifiers.add(modifier);
        }
    }

    @Override
    public List<Modifier> getCurrentModifiers() {
        return Collections.unmodifiableList(_currentModifiers);
    }

    @Override
    public List<Modifier> getPrevModifiers() {
        return Collections.unmodifiableList(_prevModifiers);
    }
}
