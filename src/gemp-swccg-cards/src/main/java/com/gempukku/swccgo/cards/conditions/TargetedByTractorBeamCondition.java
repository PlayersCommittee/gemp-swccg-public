package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.game.state.UsingTractorBeamState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

import java.util.Collection;

/**
 * A condition that is fulfilled when a specified card is targeted by a tractor beam.
 */
public class TargetedByTractorBeamCondition implements Condition {
    private Filter _targetFilter;

    /**
     * Creates a condition that is fulfilled when a card accepted by the target filter is targeted by a tractor beam.
     * @param targetFilter the target filter
     */
    public TargetedByTractorBeamCondition(Filterable targetFilter) {
        _targetFilter = Filters.and(targetFilter);
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        if (gameState.isDuringUsingTractorBeam()) {
            UsingTractorBeamState usingTractorBeamState = gameState.getUsingTractorBeamState();
            if (usingTractorBeamState != null) {
                Collection<PhysicalCard> targets = usingTractorBeamState.getTargets();
                for (PhysicalCard card : targets) {
                    if (_targetFilter.accepts(gameState, modifiersQuerying, card))
                        return true;
                }
            }
        }
        return false;
    }
}
