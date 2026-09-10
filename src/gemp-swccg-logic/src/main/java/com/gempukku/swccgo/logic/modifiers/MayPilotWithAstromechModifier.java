package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that allows an unpiloted starfighter to move and use power, maneuver, and hyperspeed
 * while an Astromech Translator is aboard with an astromech character.
 */
public class MayPilotWithAstromechModifier extends AbstractModifier {

    /**
     * Creates a modifier that allows an unpiloted starfighter to move and use power, maneuver, and hyperspeed.
     * @param source the source of the modifier
     * @param affectFilter the filter for affected cards
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayPilotWithAstromechModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "May move and use power, maneuver and hyperspeed while unpiloted", affectFilter, condition, ModifierType.MAY_PILOT_WITH_ASTROMECH);
    }
}
