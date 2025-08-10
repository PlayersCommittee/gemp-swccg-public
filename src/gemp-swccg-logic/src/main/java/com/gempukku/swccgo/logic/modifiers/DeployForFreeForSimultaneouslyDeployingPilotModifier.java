package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier that causes a pilot simultaneously deploying with the source card to a specified target to deploy for free.
 */
public class DeployForFreeForSimultaneouslyDeployingPilotModifier extends AbstractModifier {
    private Filter _pilotFilter;
    private Filter _targetFilter;

    /**
     * Creates a modifier that causes a pilot accepted by the pilot filter that is simultaneously deploying with the source
     * card for free.
     * @param source the source of the modifier
     * @param pilotFilter the pilot filter
     */
    public DeployForFreeForSimultaneouslyDeployingPilotModifier(PhysicalCard source, Filterable pilotFilter) {
        this(source, pilotFilter, null, Filters.any);
    }

    /**
     * Creates a modifier that causes a pilot accepted by the pilot filter that is simultaneously deploying with the source
     * card to a target accepted by the target filter for free.
     * @param source the source of the modifier
     * @param pilotFilter the pilot filter
     * @param targetFilter the target filter
     */
    public DeployForFreeForSimultaneouslyDeployingPilotModifier(PhysicalCard source, Filterable pilotFilter, Filterable targetFilter) {
        this(source, pilotFilter, null, targetFilter);
    }

    /**
     * Creates a modifier that causes a pilot accepted by the pilot filter that is simultaneously deploying with the source
     * card to a target accepted by the target filter for free.
     * @param source the source of the modifier
     * @param pilotFilter the pilot filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param targetFilter the target filter
     */
    private DeployForFreeForSimultaneouslyDeployingPilotModifier(PhysicalCard source, Filterable pilotFilter, Condition condition, Filterable targetFilter) {
        super(source, null, source, condition, ModifierType.SIMULTANEOUS_PILOT_DEPLOYS_FOR_FREE, true);
        _pilotFilter = Filters.and(pilotFilter);
        _targetFilter = Filters.and(targetFilter);
    }

    @Override
    public boolean isAffectedPilot(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard pilot) {
        return Filters.and(_pilotFilter).accepts(gameState, modifiersQuerying, pilot);
    }

    @Override
    public boolean isAffectedTarget(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard target) {
        return Filters.and(_targetFilter).accepts(gameState, modifiersQuerying, target);
    }
}
