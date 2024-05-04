package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;

/**
 * A modifier for not being able to exist (deploy or move to) specified targets.
 */
public class MayNotExistAtTargetModifier extends AbstractModifier {
    private Filter _targetFilter;

    /**
     * Creates a modifier for not being able to exist (deploy or move to) specified targets.
     * @param source the card that is the source of the modifier and that may not exist at specified targets
     * @param targetFilter the filter for targets that affected cards may not exist at
     */
    public MayNotExistAtTargetModifier(PhysicalCard source, Filterable targetFilter) {
        this(source, source, targetFilter);
    }

    /**
     * Creates a modifier for not being able to exist (deploy or move to) specified targets.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that may not exist at specified targets
     * @param targetFilter the filter for targets that affected cards may not exist at
     */
    public MayNotExistAtTargetModifier(PhysicalCard source, Filterable affectFilter, Filterable targetFilter) {
        super(source, null, affectFilter, null, ModifierType.MAY_NOT_EXIST_AT_TARGET, true);
        _targetFilter = Filters.and(targetFilter);
    }

    @Override
    public boolean isProhibitedFromExistingAt(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard target) {
        return Filters.and(_targetFilter).accepts(gameState, modifiersQuerying, target);
    }
}
