package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier that causes the specified player to generate no Force at affected locations.
 */
public class GenerateNoForceModifier extends ResetForceGenerationModifier {

    /**
     * Creates a modifier that causes the specified player to generate no Force at locations accepted by the location filter.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     * @param playerId the player
     */
    public GenerateNoForceModifier(PhysicalCard source, Filterable locationFilter, String playerId) {
        super(source, locationFilter, null, 0, playerId);
    }

    /**
     * Creates a modifier that causes the specified player to generate no Force at locations accepted by the location filter.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param playerId the player
     */
    public GenerateNoForceModifier(PhysicalCard source, Filterable locationFilter, Condition condition, String playerId) {
        super(source, locationFilter, condition, 0, playerId);
    }

    @Override
    public Condition getAdditionalCondition(GameState gameState, ModifiersQuerying modifiersQuerying, final PhysicalCard location) {
        PhysicalCard source = getSource(gameState);
        final int permSourceCardId = source.getPermanentCardId();

        // Check if card can cancel Force generation for player
        return new Condition() {
            @Override
            public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
                PhysicalCard source = gameState.findCardByPermanentId(permSourceCardId);

                return !modifiersQuerying.isImmuneToForceGenerationCancel(gameState, _playerId, location, source);
            }
        };
    }
}
