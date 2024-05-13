package com.gempukku.swccgo.logic.actions;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.effects.BattleEffect;
import com.gempukku.swccgo.logic.effects.PayInitiateBattleCostEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.SnapshotData;

import java.util.Collections;
import java.util.List;

/**
 * The action to initiate a battle.
 */
public class InitiateBattleAction extends AbstractTopLevelRuleAction {
    private PhysicalCard _location;
    private boolean _useForceCostApplied;
    private boolean _battleInitiated;
    private boolean _free;

    /**
     * Needed to generate snapshot.
     */
    public InitiateBattleAction() {
    }

    @Override
    public void generateSnapshot(Action selfSnapshot, SnapshotData snapshotData) {
        super.generateSnapshot(selfSnapshot, snapshotData);
        InitiateBattleAction snapshot = (InitiateBattleAction) selfSnapshot;

        snapshot._location = snapshotData.getDataForSnapshot(_location);
        snapshot._useForceCostApplied = _useForceCostApplied;
        snapshot._battleInitiated = _battleInitiated;
        snapshot._free = _free;
    }

    /**
     * Creates an action to initiate a battle at the specified location.
     * @param playerId the player initiating the battle
     * @param location the location
     */
    public InitiateBattleAction(String playerId, final PhysicalCard location, boolean free) {
        super(location, playerId);
        _location = location;
        _free = free;
    }

    @Override
    public String getText() {
        if (_free)
            return "Initiate battle for free";
        return "Initiate battle";
    }

    @Override
    public Effect nextEffect(SwccgGame game) {
        if (!isAnyCostFailed()) {
            Effect cost = getNextCost();
            if (cost != null)
                return cost;

            if (!_useForceCostApplied) {
                _useForceCostApplied = true;

                appendCost(new PayInitiateBattleCostEffect(this, _location, getPerformingPlayer(), _free));
                return getNextCost();
            }

            if (!_battleInitiated) {
                _battleInitiated = true;

                return new BattleEffect(this, _location, false, null, getAddedModifiers());
            }

            Effect effect = getNextEffect();
            if (effect != null)
                return effect;
        }

        return null;
    }

    public List<Modifier> getAddedModifiers() {
        return Collections.emptyList();
    }

    @Override
    public boolean wasActionCarriedOut() {
        return _battleInitiated;
    }
}
