package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A condition that is fulfilled when the specified location is part of the system of the specified name.
 */
public class PartOfSystemCondition implements Condition {
    private int _permLocationCardId;
    private String _system;

    /**
     * Creates a condition that is fulfilled when the specified location is part of the system of the specified name.
     * @param location the location
     * @param system the name of the system
     */
    public PartOfSystemCondition(PhysicalCard location, String system) {
        _permLocationCardId = location.getPermanentCardId();
        _system = system;
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        PhysicalCard location = gameState.findCardByPermanentId(_permLocationCardId);

        return location.getPartOfSystem() != null
                && location.getPartOfSystem().equals(_system);
    }
}
