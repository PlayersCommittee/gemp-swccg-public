package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgBuiltInCardBlueprint;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that prevents weapon destiny draws from being modified.
 */
public class MayNotModifyWeaponDestinyModifier extends AbstractModifier {
    private String _playerDrawing;
    private String _playerToModify;
    private Filter _weaponFilter;
    private Filter _weaponUserFilter;
    private boolean _noSpecifiedUser;

    /**
     * Creates a modifier that prevents weapon destiny draws from being modified by a specified player.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param playerToModify the player that may not modify weapon destiny draws
     * @param weaponFilter the weapon filter
     */
    public MayNotModifyWeaponDestinyModifier(PhysicalCard source, Condition condition, String playerToModify, Filterable weaponFilter) {
        this(source, condition, playerToModify, weaponFilter, Filters.any);
        _noSpecifiedUser = true;
    }

    /**
     * Creates a modifier that prevents weapon destiny draws from being modified by a specified player.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param playerToModify the player that may not modify weapon destiny draws
     * @param weaponFilter the weapon filter
     * @param weaponUserFilter the weapon user filter
     */
    public MayNotModifyWeaponDestinyModifier(PhysicalCard source, Condition condition, String playerToModify, Filterable weaponFilter, Filterable weaponUserFilter) {
        super(source, null, null, condition, ModifierType.MAY_NOT_MODIFY_WEAPON_DESTINY, true);
        _playerDrawing = null;
        _playerToModify = playerToModify;
        _weaponFilter = Filters.and(weaponFilter);
        _weaponUserFilter = Filters.and(weaponUserFilter);
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return null;
    }

    @Override
    public boolean mayNotModifyWeaponDestiny(GameState gameState, ModifiersQuerying modifiersQuerying, String playerDrawing, String playerToModify, PhysicalCard weaponCard, SwccgBuiltInCardBlueprint permanentWeapon, PhysicalCard weaponUser) {
        return (_playerDrawing == null || _playerDrawing.equals(playerDrawing))
                && (_playerToModify == null || _playerToModify.equals(playerToModify))
                && ((weaponCard != null && Filters.and(_weaponFilter).accepts(gameState, modifiersQuerying, weaponCard))
                || (permanentWeapon != null && Filters.and(_weaponFilter).accepts(gameState, modifiersQuerying, permanentWeapon)))
                && (_noSpecifiedUser || (weaponUser != null && Filters.and(_weaponUserFilter).accepts(gameState, modifiersQuerying, weaponUser)));
    }
}
