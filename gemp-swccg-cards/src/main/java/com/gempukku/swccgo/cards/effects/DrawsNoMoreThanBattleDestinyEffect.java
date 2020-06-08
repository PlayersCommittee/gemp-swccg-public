package com.gempukku.swccgo.cards.effects;


import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotDrawMoreThanBattleDestinyModifier;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect to cause the specified player to draw no more than a specified number of battle destiny during the current battle.
 */
public class DrawsNoMoreThanBattleDestinyEffect extends AddUntilEndOfBattleModifierEffect {

    /**
     * Creates an effect to cause the specified player to draw no more than a specified number of battle destiny during the current battle.
     * @param action the action performing this effect
     * @param playerId the player
     * @param amount the number of battle destiny
     */
    public DrawsNoMoreThanBattleDestinyEffect(Action action, String playerId, int amount) {
        super(action, new MayNotDrawMoreThanBattleDestinyModifier(action.getActionSource(), amount, playerId),
                "Makes " + playerId + " draw no more than " + amount + " battle destiny");
    }
}
