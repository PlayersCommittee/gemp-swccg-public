package com.gempukku.swccgo.cards.effects;


import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotDrawBattleDestinyModifier;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect to cause the specified player to draw no battle destiny during the current battle.
 */
public class DrawsNoBattleDestinyEffect extends AddUntilEndOfBattleModifierEffect {

    /**
     * Creates an effect to cause the specified player to draw no battle destiny during the current battle.
     * @param action the action performing this effect
     * @param playerId the player
     */
    public DrawsNoBattleDestinyEffect(Action action, String playerId) {
        super(action, new MayNotDrawBattleDestinyModifier(action.getActionSource(), playerId),
                "Makes " + playerId + " draw no battle destiny");
    }
}
