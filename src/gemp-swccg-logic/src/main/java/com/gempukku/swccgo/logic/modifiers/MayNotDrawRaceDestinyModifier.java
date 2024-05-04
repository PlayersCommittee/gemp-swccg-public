package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier for "May not draw race destiny".
 */
public class MayNotDrawRaceDestinyModifier extends AbstractModifier {

    /**
     * Creates a "May not draw race destiny" modifier.
     * @param source the source of the modifier
     * @param playerId the player that may not draw battle destiny
     */
    public MayNotDrawRaceDestinyModifier(PhysicalCard source, String playerId) {
        this(source, null, playerId);
    }

    /**
     * Creates a "May not draw race destiny" modifier.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param playerId the player that may not draw battle destiny
     */
    public MayNotDrawRaceDestinyModifier(PhysicalCard source, Condition condition, String playerId) {
        super(source, null, null, condition, ModifierType.MAY_NOT_DRAW_RACE_DESTINY, true);
         _playerId = playerId;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return gameState.getSide(_playerId) + " may not draw race destiny";
    }
}
