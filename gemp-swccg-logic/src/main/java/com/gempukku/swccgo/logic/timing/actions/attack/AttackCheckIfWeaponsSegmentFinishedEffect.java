package com.gempukku.swccgo.logic.timing.actions.attack;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;

/**
 * An effect that checks if the attack can continue and schedules the next effect.
 */
class AttackCheckIfWeaponsSegmentFinishedEffect extends AbstractSuccessfulEffect {

    /**
     * Creates an effect that checks if the attack can continue and schedules the next effect.
     * @param action the action performing this effect
     */
    AttackCheckIfWeaponsSegmentFinishedEffect(AttackWeaponsSegmentAction action) {
        super(action);
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        AttackWeaponsSegmentAction attackWeaponsSegmentAction = (AttackWeaponsSegmentAction) _action;

        if (game.getGameState().getAttackState().canContinue() && (attackWeaponsSegmentAction.getConsecutivePasses() < attackWeaponsSegmentAction.getPlayOrder().getPlayerCount())) {
            attackWeaponsSegmentAction.appendEffect(
                    new AttackPlayerPlaysNextActionEffect(attackWeaponsSegmentAction));
        }
    }
}
