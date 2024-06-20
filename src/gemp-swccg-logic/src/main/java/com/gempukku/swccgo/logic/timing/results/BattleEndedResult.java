package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.BattleState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * This effect result is triggered when a battle has just ended.
 */
public class BattleEndedResult extends EffectResult {
    private PhysicalCard _location;
    private BattleState _battleState;

    /**
     * Creates an effect result that is triggered when a battle has just ended.
     * @param action the action performing this effect result
     * @param location the battle location
     */
    public BattleEndedResult(Action action, PhysicalCard location, BattleState battleState) {
        super(Type.BATTLE_ENDED, action.getPerformingPlayer());
        _location = location;
        _battleState = battleState;
    }

    /**
     * Gets the battle location.
     * @return the location
     */
    public PhysicalCard getLocation() {
        return _location;
    }

    /**
     * Gets the BattleState.
     * @return the BattleState
     */
    public BattleState getBattleState() {
        return _battleState;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "Battle at " + GameUtils.getCardLink(_location) + " just ended";
    }
}
