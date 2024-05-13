package com.gempukku.swccgo.game;

import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.List;

/**
 * This interface represents a proxy object that will implement triggers that are not attached to a specific "active" card
 * in the game.
 * It is mainly used to implement specific game rules, or actions that need to still trigger after the card that "scheduled"
 * the actions is no longer in play when the action needs to happen.
 */
public interface ActionProxy {

    /**
     * Gets the required trigger actions that must be performed before the specified effect.
     * @param game the game
     * @param effect the effect
     * @return the trigger actions
     */
    List<TriggerAction> getRequiredBeforeTriggers(SwccgGame game, Effect effect);

    /**
     * Gets the trigger actions that must be performed after the specified effect result.
     * @param game the game
     * @param effectResult the effect result
     * @return the trigger actions
     */
    List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult effectResult);

    /**
     * Gets the trigger actions that may be performed after the specified effect result.
     * @param game the game
     * @param effectResult the effect result
     * @return the trigger actions
     */
    List<TriggerAction> getOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult effectResult);
}
