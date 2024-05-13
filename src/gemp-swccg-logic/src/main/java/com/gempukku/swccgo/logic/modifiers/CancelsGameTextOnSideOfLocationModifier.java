package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that causes the game text on the specified player's side of specified locations to be canceled.
 * Note: The affected cards' game texts are actually canceled by an action triggered by the CancelGameTextRule class.
 */
public class CancelsGameTextOnSideOfLocationModifier extends AbstractModifier {

    /**
     * Creates a modifier that causes the game text on the specified player's side of specified locations to be canceled.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     * @param playerId the player whose side of the location is affected
     */
    public CancelsGameTextOnSideOfLocationModifier(PhysicalCard source, Filterable locationFilter, String playerId) {
        this(source, locationFilter, null, playerId);
    }

    /**
     * Creates a modifier that causes the game text on the specified player's side of specified locations to be canceled.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param playerId the player whose side of the location is affected
     */
    public CancelsGameTextOnSideOfLocationModifier(PhysicalCard source, Filterable locationFilter, Condition condition, String playerId) {
        super(source, null, Filters.and(Filters.onTable, Filters.location, locationFilter), condition, ModifierType.CANCEL_LOCATION_GAME_TEXT_FOR_PLAYER, true);
        _playerId = playerId;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return self.isLocationGameTextCanceledForPlayer(_playerId) ? "Game text canceled for " + _playerId : null;
    }

    @Override
    public boolean isCanceledTextForPlayer(String playerId) {
        return playerId.equals(_playerId);
    }
}
