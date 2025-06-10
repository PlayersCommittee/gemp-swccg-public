package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier that allows cards to have destiny number cloned by specified player's clone cards when not in sabacc hand.
 */
public class MayHaveDestinyNumberClonedInSabaccModifier extends AbstractModifier {

    /**
     * Creates a modifier that allows source card to have destiny number cloned by player's clone cards when not in sabacc hand.
     * @param source the card that is the source of the modifier and is affected by the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param playerId the player
     */
    public MayHaveDestinyNumberClonedInSabaccModifier(PhysicalCard source, Condition condition, String playerId) {
        this(source, source, condition, playerId);
    }

    /**
     * Creates a modifier that allows cards accepted by the filter to have destiny number cloned by player's clone cards
     * when not in sabacc hand.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param playerId the player
     */
    private MayHaveDestinyNumberClonedInSabaccModifier(PhysicalCard source, Filterable affectFilter, Condition condition, String playerId) {
        super(source, null, affectFilter, condition, ModifierType.MAY_CLONE_DESTINY_IN_SABACC, true);
        _playerId = playerId;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return "May have destiny number cloned in sabacc";
    }
}
