package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.logic.modifiers.TotalBattleDestinyMultiplierModifier;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that doubles total battle destiny for the specified player until the end of battle.
 */
public class DoubleTotalBattleDestinyEffect extends AddUntilEndOfBattleModifierEffect {

    /**
     * Creates an effect that doubles total battle destiny for the specified player until the end of battle.
     * @param action the action
     * @param playerId the player whose total battle destiny is modified
     */
    public DoubleTotalBattleDestinyEffect(Action action, String playerId) {
        super(action, new TotalBattleDestinyMultiplierModifier(action.getActionSource(), 2, playerId), "Doubles total battle destiny");
    }
}
