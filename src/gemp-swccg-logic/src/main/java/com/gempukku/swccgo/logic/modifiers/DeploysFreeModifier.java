package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier that causes affected cards to deploy free.
 */
public class DeploysFreeModifier extends AbstractModifier {

    /**
     * Creates a modifier that causes the source card to deploy free.
     * @param source the card that is the source of the modifier and deploys free
     */
    public DeploysFreeModifier(PhysicalCard source) {
        this(source, source, null);
    }

    /**
     * Creates a modifier that causes the source card to deploy free.
     * @param source the card that is the source of the modifier and deploys free
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public DeploysFreeModifier(PhysicalCard source, Condition condition) {
        this(source, source, condition);
    }

    /**
     * Creates a modifier that causes affected cards to deploy free.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that deploy free
     */
    public DeploysFreeModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null);
    }

    /**
     * Creates a modifier that causes affected cards to deploy free.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that deploy free
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public DeploysFreeModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, null, Filters.or(affectFilter, Filters.hasPermanentAboard(Filters.and(affectFilter))), condition, ModifierType.DEPLOYS_FREE, true);
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return "Deploys for free";
    }
}
