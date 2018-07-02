package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that causes the affected cards to satisfy all of owner's battle damage when forfeited.
 */
public class SatisfiesAllBattleDamageWhenForfeitedModifier extends AbstractModifier {

    /**
     * Creates a modifier that causes the affected cards to satisfy all of owner's battle damage when forfeited.
     * @param source the card that is the source of the modifier that satisfies all battle damage when forfeited
     */
    public SatisfiesAllBattleDamageWhenForfeitedModifier(PhysicalCard source) {
        super(source, "Satisfies all battle damage when forfeited", source, ModifierType.SATISFIES_ALL_BATTLE_DAMAGE_WHEN_FORFEITED);
    }

    /**
     * Creates a modifier that causes the affected cards to satisfy all of owner's battle damage when forfeited.
     * @param source the card that is the source of the modifier that satisfies all battle damage when forfeited
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public SatisfiesAllBattleDamageWhenForfeitedModifier(PhysicalCard source, Condition condition) {
        super(source, "Satisfies all battle damage when forfeited", source, condition, ModifierType.SATISFIES_ALL_BATTLE_DAMAGE_WHEN_FORFEITED);
    }
}
