package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier that causes card to remain in play and only reduce for forfeit when 'forfeited'.
 */
public class RemainsInPlayWhenForfeitedFromPlayModifier extends AbstractModifier {

    /**
     * Creates a modifier that causes card to remain in play and only reduce for forfeit when 'forfeited'.
     * @param source the source of the modifier and card affected by modifier
     */
    public RemainsInPlayWhenForfeitedFromPlayModifier(PhysicalCard source) {
        super(source, "Remains in play when forfeited", source, ModifierType.REMAINS_IN_PLAY_WHEN_FORFEITED);
    }

    @Override
    public Condition getAdditionalCondition(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        final int permCardId = self.getPermanentCardId();

        return new Condition() {
            @Override
            public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
                PhysicalCard self = gameState.findCardByPermanentId(permCardId);

                return !self.isHit() && !modifiersQuerying.isProhibitedFromHavingForfeitReduced(gameState, self);
            }
        };
    }
}
