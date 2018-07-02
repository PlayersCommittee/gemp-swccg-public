package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.SwccgBuiltInCardBlueprint;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

/**
 * A condition that is fulfilled during a battle where the specified player has fired weapons in that battle.
 */
public class FiredWeaponsInBattleCondition implements Condition {
    private String _playerId;
    private int _count;
    private Filter _weaponFilter;

    /**
     * Creates a condition that is fulfilled during a battle where the specified player has fired weapons in that battle.
     * @param playerId the player
     */
    public FiredWeaponsInBattleCondition(String playerId) {
        this(playerId, Filters.any);
    }

    /**
     * Creates a condition that is fulfilled during a battle where the specified player has fired weapons accepted by the
     * weapon filter in that battle.
     * @param playerId the player
     */
    public FiredWeaponsInBattleCondition(String playerId, Filter weaponFilter) {
        this(playerId, 1, weaponFilter);
    }

    /**
     * Creates a condition that is fulfilled during a battle where the specified player has fired at least a specified
     * number of weapons accepted by the weapon filter in that battle.
     * @param playerId the player
     */
    public FiredWeaponsInBattleCondition(String playerId, int count, Filter weaponFilter) {
        _playerId = playerId;
        _count = count;
        _weaponFilter = weaponFilter;
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        if (!gameState.isDuringBattle())
            return false;

        int matchesFound = Filters.filterCount(modifiersQuerying.getWeaponsFiredInBattleByPlayer(_playerId, true), gameState.getGame(), _count, _weaponFilter).size();
        if (matchesFound >= _count) {
            return true;
        }

        for (SwccgBuiltInCardBlueprint permWeapon : modifiersQuerying.getPermanentWeaponsFiredInBattleByPlayer(_playerId, true)) {
            if (_weaponFilter.accepts(gameState, modifiersQuerying, permWeapon)) {
                matchesFound++;
            }
            if (matchesFound >= _count) {
                return true;
            }
        }

        return false;
    }
}
