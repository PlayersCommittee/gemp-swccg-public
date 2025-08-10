package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A condition that is fulfilled during a battle where the specified player has no cards with ability participating.
 */
public class NoAbilityInBattleCondition implements Condition {
    private String _playerId;

    /**
     * Creates a condition that is fulfilled during a battle where the specified player has no cards with ability participating.
     * @param playerId the player
     */
    public NoAbilityInBattleCondition(String playerId) {
        _playerId = playerId;
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        if (!gameState.isDuringBattle())
            return false;

        return !Filters.canSpot(gameState.getBattleState().getAllCardsParticipating(), gameState.getGame(),
                Filters.and(Filters.owner(_playerId), Filters.hasAbilityOrHasPermanentPilotWithAbility));
    }
}
