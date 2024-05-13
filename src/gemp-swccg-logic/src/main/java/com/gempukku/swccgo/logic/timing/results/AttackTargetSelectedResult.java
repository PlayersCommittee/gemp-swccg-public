package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.InitiateAttackNonCreatureAction;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * This effect result is triggered when a character was just selected for an attack.
 */
public class AttackTargetSelectedResult extends EffectResult {
    private PhysicalCard _creature;
    private PhysicalCard _target;
    private InitiateAttackNonCreatureAction _action;

    /**
     * Creates an effect result that is triggered when a character was just selected for an attack.
     * @param action the action performing this effect result
     * @param creature the creature that is attacking
     * @param target the target that was selected
     */
    public AttackTargetSelectedResult(InitiateAttackNonCreatureAction action, PhysicalCard creature, PhysicalCard target) {
        super(Type.ATTACK_TARGET_SELECTED, action.getPerformingPlayer());
        _action = action;
        _creature = creature;
        _target = target;
    }

    public InitiateAttackNonCreatureAction getInitiateAttackNonCreatureAction() {
        return _action;
    }

    public PhysicalCard getCreature() {
        return _creature;
    }

    public PhysicalCard getTarget() {
        return _target;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return GameUtils.getCardLink(_target) + " selected to be attacked by " + GameUtils.getCardLink(_creature);
    }
}
