package com.gempukku.swccgo.game;

import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.List;

/**
 * This abstract class represents a proxy object that will implement triggers that are not attached to a specific "active"
 * card in the game.
 * It is mainly used to implement specific game rules, or actions that need to still trigger after the card that "scheduled"
 * the actions is no longer in play when the action needs to happen.
 */
public abstract class AbstractActionProxy implements ActionProxy {

    @Override
    public List<TriggerAction> getRequiredBeforeTriggers(SwccgGame game, Effect effect) {
        return null;
    }

    @Override
    public List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult effectResult) {
        return null;
    }

    @Override
    public List<TriggerAction> getOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult effectResult) {
        return null;
    }
}
