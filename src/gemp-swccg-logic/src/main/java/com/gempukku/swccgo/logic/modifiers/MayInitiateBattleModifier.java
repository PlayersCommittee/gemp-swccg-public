package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that allows affected cards to initiate battle.
 */
public class MayInitiateBattleModifier extends AbstractModifier {

    /**
     * Creates a modifier that allows the source card to initiate battle.
     * @param source the card that is the source of the modifier and that is allowed to initiate battle
     */
    public MayInitiateBattleModifier(PhysicalCard source) {
        this(source, source, null);
    }

    /**
     * Creates a modifier that allows cards accepted by the filter to be initiate battle.
     * @param source the source of the modifier
     * @param affectFilter the filter
     */
    public MayInitiateBattleModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null);
    }

    /**
     * Creates a modifier that allows cards accepted by the filter to be initiate battle.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayInitiateBattleModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "May initiate battle", affectFilter, condition, ModifierType.MAY_INITIATE_BATTLE);
    }
}
