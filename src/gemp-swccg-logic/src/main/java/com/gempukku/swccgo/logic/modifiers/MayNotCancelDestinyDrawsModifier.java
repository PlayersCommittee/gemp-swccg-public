package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier that prevents all destiny draws from being canceled.
 */
public class MayNotCancelDestinyDrawsModifier extends AbstractModifier {

    private String _playerDrawing;
    private String _playerToCancel;
    private boolean _bothPlayers;

    /**
     * Creates a modifier that prevents a player from cancelling any destiny draws.
     *
     * @param source      the source of the modifier
     * @param bothPlayers true if neither player may cancel destiny draws
     */
    public MayNotCancelDestinyDrawsModifier(PhysicalCard source, Condition condition, boolean bothPlayers) {
        this(source, condition, null, null);
        _bothPlayers = bothPlayers;
    }

    /**
     * Creates a modifier that prevents a player from cancelling any destiny draws.
     *
     * @param source   the source of the modifier
     * @param playerDrawing the player whose destiny draws may not be cancelled
     * @param playerToCancel the player who may not cancel destiny draws
     */
    public MayNotCancelDestinyDrawsModifier(PhysicalCard source, String playerDrawing, String playerToCancel) {
        this(source, null, playerDrawing, playerToCancel);
    }

    /**
     * Creates a modifier that prevents a player from cancelling any destiny draws.
     * @param source the source of the modifier
     * @param condition conditions which must be met for the modifier to be active
     * @param playerDrawing the player whose destiny draws may not be cancelled
     * @param playerToCancel the player who may not cancel destiny draws
     */
    public MayNotCancelDestinyDrawsModifier(PhysicalCard source, Condition condition, String playerDrawing, String playerToCancel) {
        super(source, null, Filters.any, condition, ModifierType.MAY_NOT_CANCEL_DESTINY_DRAWS, true);
        _playerDrawing = playerDrawing;
        _playerToCancel = playerToCancel;

    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return null;
    }

    @Override
    public boolean mayNotCancelDestiny(String playerDrawing, String playerToModify) {
        return _bothPlayers
                || (_playerDrawing == null || _playerDrawing.equals(playerDrawing))
                && (_playerToCancel == null || _playerToCancel.equals(playerToModify));
    }
}
