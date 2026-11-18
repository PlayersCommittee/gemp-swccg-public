package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier that gives affected cards the option to deploy for free (in addition to their printed cost).
 */
public class MayDeployFreeModifier extends AbstractModifier {

    /**
     * Creates a modifier that gives the source card the option to deploy for free.
     * @param source the card that is the source of the modifier and may deploy for free
     */
    public MayDeployFreeModifier(PhysicalCard source) {
        this(source, source, null);
    }

    /**
     * Creates a modifier that gives the source card the option to deploy for free.
     * @param source the card that is the source of the modifier and may deploy for free
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayDeployFreeModifier(PhysicalCard source, Condition condition) {
        this(source, source, condition);
    }

    /**
     * Creates a modifier that gives affected cards the option to deploy for free.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that may deploy for free
     */
    public MayDeployFreeModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null);
    }

    /**
     * Creates a modifier that gives affected cards the option to deploy for free.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that may deploy for free
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayDeployFreeModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, null, Filters.or(affectFilter, Filters.hasPermanentAboard(Filters.and(affectFilter))), condition, ModifierType.MAY_DEPLOY_FOR_FREE, true);
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return "May deploy for free";
    }
}
