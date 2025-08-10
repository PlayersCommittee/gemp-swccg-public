package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.EpicEventState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.game.state.TargetTheMainGeneratorState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A condition that is fulfilled when the specified card (or a card accepted by the specified filter) is the AT-AT or
 * AT-AT Cannon used during Target The Main Generator.
 */
public class TargetingTheMainGeneratorCondition implements Condition {
    private Integer _permCardId;
    private Filter _filter;

    /**
     * Creates a condition that is fulfilled when the specified card is used during Target The Main Generator.
     * @param card the card
     */
    public TargetingTheMainGeneratorCondition(PhysicalCard card) {
        _permCardId = card.getPermanentCardId();
    }

    /**
     * Creates a condition that is fulfilled when a card accepted by the specified card is used during Target The Main Generator.
     * @param filter the filter
     */
    public TargetingTheMainGeneratorCondition(Filter filter) {
        _filter = filter;
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        PhysicalCard card = gameState.findCardByPermanentId(_permCardId);

        EpicEventState epicEventState = gameState.getEpicEventState();
        if (epicEventState != null && epicEventState.getEpicEventType() == EpicEventState.Type.TARGET_THE_MAIN_GENERATOR) {
            TargetTheMainGeneratorState targetTheMainGeneratorState = (TargetTheMainGeneratorState) epicEventState;
            Filter filterToCheck = (card != null) ? Filters.sameCardId(card) : _filter;

            if (targetTheMainGeneratorState.getAtat() != null
                    && filterToCheck.accepts(gameState, modifiersQuerying, targetTheMainGeneratorState.getAtat())) {
                return true;
            }
            if (targetTheMainGeneratorState.getAtatCannon() != null
                    && filterToCheck.accepts(gameState, modifiersQuerying, targetTheMainGeneratorState.getAtatCannon())) {
                return true;
            }
            if (targetTheMainGeneratorState.getAtatPilot() != null
                    && filterToCheck.accepts(gameState, modifiersQuerying, targetTheMainGeneratorState.getAtatPilot())) {
                return true;
            }
        }
        return false;
    }
}
