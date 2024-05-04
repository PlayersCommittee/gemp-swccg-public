package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.AttemptToBlowAwayDeathStarIIState;
import com.gempukku.swccgo.game.state.EpicEventState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

/**
 * A condition that is fulfilled when the specified card is attempting to 'blow away' Death Star II.
 */
public class AttemptToBlowAwayDeathStarIICondition implements Condition {
    private Integer _permCardId;
    private Filter _filter;

    /**
     * Creates a condition that is fulfilled when the specified card is attempting to 'blow away' Death Star II.
     * @param card the card
     */
    public AttemptToBlowAwayDeathStarIICondition(PhysicalCard card) {
        _permCardId = card.getPermanentCardId();
    }

    /**
     * Creates a condition that is fulfilled when a card accepted by the specified card is attempting to 'blow away' Death Star II.
     * @param filter the filter
     */
    public AttemptToBlowAwayDeathStarIICondition(Filter filter) {
        _filter = filter;
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        PhysicalCard card = gameState.findCardByPermanentId(_permCardId);

        EpicEventState epicEventState = gameState.getEpicEventState();
        if (epicEventState != null && epicEventState.getEpicEventType() == EpicEventState.Type.ATTEMPT_TO_BLOW_AWAY_DEATH_STAR_II) {
            AttemptToBlowAwayDeathStarIIState attemptToBlowAwayDeathStarIIState = (AttemptToBlowAwayDeathStarIIState) epicEventState;
            Filter filterToCheck = (card != null) ? Filters.sameCardId(card) : _filter;

            if (attemptToBlowAwayDeathStarIIState.getStarfighter() != null
                    && filterToCheck.accepts(gameState, modifiersQuerying, attemptToBlowAwayDeathStarIIState.getStarfighter())) {
                return true;
            }
            if (attemptToBlowAwayDeathStarIIState.getPilot() != null
                    && filterToCheck.accepts(gameState, modifiersQuerying, attemptToBlowAwayDeathStarIIState.getPilot())) {
                return true;
            }
        }
        return false;
    }
}
