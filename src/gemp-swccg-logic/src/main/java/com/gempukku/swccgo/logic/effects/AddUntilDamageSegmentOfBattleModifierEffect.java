package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that adds a modifier until the damage segment of battle.
 */
public class AddUntilDamageSegmentOfBattleModifierEffect extends AddModifierWithDurationEffect {

    /**
     * Creates an effect that adds a modifier until the damage segment of battle.
     * @param action the action adding the modifier
     * @param modifier the modifier
     * @param actionMsg the action message
     */
    public AddUntilDamageSegmentOfBattleModifierEffect(Action action, Modifier modifier, String actionMsg) {
        super(action, modifier, actionMsg);
    }

    @Override
    protected String getMsgText(SwccgGame game) {
        if (getActionMsg() == null)
            return null;

        if (_action.getPerformingPlayer() == null) {
            return GameUtils.getCardLink(_action.getActionSource()) + " " + getActionMsg() + " until damage segment of battle";
        }
        else {
            return _action.getPerformingPlayer() + " " + getActionMsg() + " until damage segment of battle using " + GameUtils.getCardLink(_action.getActionSource());
        }
    }

    @Override
    public final void doPlayEffect(SwccgGame game) {
        sendMsg(game);
        game.getModifiersEnvironment().addUntilDamageSegmentOfBattleModifier(_modifier);
    }
}
