package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that creates a modifier that specifies "Draws X battle destiny if unable to otherwise" until the end of the
 * turn.
 */
public class DrawsBattleDestinyIfUnableToOtherwiseUntilEndOfTurnEffect extends AddUntilEndOfTurnModifierEffect {

    /**
     * Creates an effect that creates a modifier that specifies "Draws X battle destiny if unable to otherwise" until the
     * end of the turn.
     * @param action the action
     * @param modifierAmount the number of destiny
     * @param actionMsg the message to send about the modifier
     */
    public DrawsBattleDestinyIfUnableToOtherwiseUntilEndOfTurnEffect(Action action, int modifierAmount, String actionMsg) {
        super(action, new DrawsBattleDestinyIfUnableToOtherwiseModifier(action.getActionSource(), modifierAmount), actionMsg);
    }
}
