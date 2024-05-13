package com.gempukku.swccgo.logic.modifiers;

import java.util.List;

/**
 * An interface that collects modifiers.
 */
public interface ModifierCollector {

    /**
     * Adds a modifier to the collector.
     * @param modifier the modifier
     */
    void addModifier(Modifier modifier);

    /**
     * Clears the current collected modifiers.
     */
    void clear();

    /**
     * Gets the current collected modifiers.
     * @return the modifiers
     */
    List<Modifier> getCurrentModifiers();

    /**
     * Gets the previous collected modifiers.
     * @return the modifiers
     */
    List<Modifier> getPrevModifiers();
}
