package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that creates a modifier that specifies "Draws X battle destiny if unable to otherwise" until the end of the
 * battle.
 */
public class DrawsBattleDestinyIfUnableToOtherwiseEffect extends AddUntilEndOfBattleModifierEffect {

    /**
     * Creates an effect that creates a modifier that specifies "Draws X battle destiny if unable to otherwise" until the
     * end of the battle.
     * @param action the action
     * @param playerId the player
     * @param modifierAmount the number of destiny
     */
    public DrawsBattleDestinyIfUnableToOtherwiseEffect(Action action, String playerId, int modifierAmount) {
        super(action, new DrawsBattleDestinyIfUnableToOtherwiseModifier(action.getActionSource(), Filters.and(Filters.owner(playerId), Filters.participatingInBattle), modifierAmount),
                "Causes " + playerId + " to draw " + modifierAmount + " battle destiny if unable to otherwise");
    }
}
