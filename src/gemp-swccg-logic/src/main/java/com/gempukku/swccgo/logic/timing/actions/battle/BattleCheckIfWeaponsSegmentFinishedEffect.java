package com.gempukku.swccgo.logic.timing.actions.battle;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;

/**
 * An effect that checks if the battle can continue and schedules the next effect.
 */
class BattleCheckIfWeaponsSegmentFinishedEffect extends AbstractSuccessfulEffect {

    /**
     * Creates an effect that checks if the battle can continue and schedules the next effect.
     * @param action the action performing this effect
     */
    BattleCheckIfWeaponsSegmentFinishedEffect(BattleWeaponsSegmentAction action) {
        super(action);
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        BattleWeaponsSegmentAction battleWeaponsSegmentAction = (BattleWeaponsSegmentAction) _action;

        if (game.getGameState().getBattleState().canContinue(game) && (battleWeaponsSegmentAction.getConsecutivePasses() < battleWeaponsSegmentAction.getPlayOrder().getPlayerCount())) {
            battleWeaponsSegmentAction.appendEffect(
                    new BattlePlayerPlaysNextActionEffect(battleWeaponsSegmentAction));
        }
    }
}
