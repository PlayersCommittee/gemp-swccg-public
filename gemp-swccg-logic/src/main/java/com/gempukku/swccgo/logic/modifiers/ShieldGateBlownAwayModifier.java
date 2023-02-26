package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;

/**
 * A modifier that indicates that the Shield Gate has been "blown away" this game
 */
public class ShieldGateBlownAwayModifier extends AbstractModifier {
    /**
     * Creates modifier that indicates that the Shield Gate has been "blown away" this game
     * @param source the source of the modifier
     */
    public ShieldGateBlownAwayModifier(PhysicalCard source) {
        super(source, null, null, null, ModifierType.SHIELD_GATE_BLOWN_AWAY, true);
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return "Shield Gate is 'blown away'";
    }
}