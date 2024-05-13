package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that causes specified cards to not add to power of anything that card is piloting.
 */
public class DoesNotAddToPowerOfAnythingPilotedModifier extends AbstractModifier {

    /**
     * Creates a modifier that causes specified cards accepted by the filter to not add to power of anything that card is piloting.
     * @param source the source the modifier
     * @param affectFilter the filter
     */
    public DoesNotAddToPowerOfAnythingPilotedModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null);
    }

    /**
     * Creates a modifier that causes specified cards accepted by the filter to not add to power of anything that card is piloting.
     * @param source the source the modifier
     * @param affectFilter the filter
     * @param condition the condition
     */
    public DoesNotAddToPowerOfAnythingPilotedModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "Does not add to power of anything piloted", affectFilter, condition, ModifierType.DOES_NOT_ADD_TO_POWER_WHEN_PILOTING);
    }
}
