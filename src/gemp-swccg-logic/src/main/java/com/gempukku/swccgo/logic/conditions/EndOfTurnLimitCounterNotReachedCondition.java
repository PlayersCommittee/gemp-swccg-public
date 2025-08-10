package com.gempukku.swccgo.logic.conditions;

import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A condition that is fulfilled when the end of turn limit counter is not reached yet.
 */
public class EndOfTurnLimitCounterNotReachedCondition implements Condition {
    private PhysicalCard _source;
    private int _limit;

    /**
     * Creates a condition that is fulfilled when the end of turn limit counter is not reached yet.
     *
     * @param source the source of the modifier
     * @param limit  value that hasn't been reached yet (i.e. 'first n cards this turn')
     */
    public EndOfTurnLimitCounterNotReachedCondition(PhysicalCard source, int limit) {
        _source = source;
        _limit = limit;
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        PhysicalCard source = gameState.findCardByPermanentId(_source.getPermanentCardId());
        return modifiersQuerying.getUntilEndOfTurnLimitCounter(source, source.getOwner(), source.getCardId(), GameTextActionId.OTHER_CARD_ACTION_DEFAULT).getUsedLimit() < _limit;
    }
}