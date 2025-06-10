package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgBuiltInCardBlueprint;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

public class MayNotBeTargetedByPermanentWeaponsModifier extends AbstractModifier {

    /**
     * Creates a modifier for not being able to be targeted by specified cards.
     * @param source the card that is the source of the modifier and that may not be targeted
     */
    public MayNotBeTargetedByPermanentWeaponsModifier(PhysicalCard source) {
        this(source, source, null);
    }

    /**
     * Creates a modifier for not being able to be targeted by specified cards.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that may not be targeted
     */
    public MayNotBeTargetedByPermanentWeaponsModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null);
    }

    /**
     * Creates a modifier for not being able to be targeted by specified cards.
     * @param source the card that is the source of the modifier and that may not be targeted
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayNotBeTargetedByPermanentWeaponsModifier(PhysicalCard source, Condition condition) {
        this(source, source, condition);
    }

    /**
     * Creates a modifier for not being able to be targeted by specified cards.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that may not be targeted
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayNotBeTargetedByPermanentWeaponsModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, null, affectFilter, condition, ModifierType.MAY_NOT_BE_TARGETED_BY, true);
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return "May not be targeted by permanent weapons";
    }

    @Override
    public boolean mayNotBeTargetedBy(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardToTarget, PhysicalCard targetedBy, SwccgBuiltInCardBlueprint targetedByPermanentWeapon) {
        return (targetedByPermanentWeapon != null && !targetedByPermanentWeapon.getPhysicalCard(gameState.getGame()).getOwner().equals(cardToTarget.getOwner()));
                //|| (targetedBy != null && !targetedBy.getOwner().equals(cardToTarget.getOwner()) && Filters.weapon.accepts(gameState, modifiersQuerying, targetedBy));
    }
}
