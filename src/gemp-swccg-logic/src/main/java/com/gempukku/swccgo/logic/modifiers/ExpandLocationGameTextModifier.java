package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that causes the game text on the specified player's side of the location the source card is "at" to be expanded
 * to locations affected by the modifier.
 * Note: The expansion of the location game texts are actually controlled by the ExpandGameTextRule class.
 */
public class ExpandLocationGameTextModifier extends AbstractModifier {

    /**
     * Creates a modifier that causes the game text on the specified player's side of the location the source card is "at"
     * to be expanded to locations affected by the modifier.
     * @param source the source of the modifier
     * @param expandToLocationFilter the filter for locations the game text is expanded to
     * @param playerId the player whose location game text is expanded
     */
    public ExpandLocationGameTextModifier(PhysicalCard source, Filterable expandToLocationFilter, String playerId) {
        super(source, null, expandToLocationFilter, null, ModifierType.EXPAND_LOCATION_GAME_TEXT, true);
         _playerId = playerId;
    }

    @Override
    public Condition getAdditionalCondition(GameState gameState, ModifiersQuerying modifiersQuerying, final PhysicalCard expandToLocation) {
        PhysicalCard location = modifiersQuerying.getLocationHere(gameState, getSource(gameState));
        if (location == null)
            return null;

        final String playersSideToExpand = (location.isInverted() ? (gameState.getOpponent(_playerId)) : _playerId);
        final int permLocationCardId = location.getPermanentCardId();
        return new Condition() {
            @Override
            public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
                PhysicalCard location = gameState.findCardByPermanentId(permLocationCardId);

                return !location.isLocationGameTextCanceledForPlayer(playersSideToExpand)
                        && !expandToLocation.isLocationGameTextCanceledForPlayer(playersSideToExpand);
            }
        };
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        PhysicalCard location = modifiersQuerying.getLocationHere(gameState, getSource(gameState));
        if (location == null)
            return null;

        Side sideToExpand = (location.isInverted() ? (gameState.getSide(gameState.getOpponent(_playerId))) : gameState.getSide(_playerId));
        if (sideToExpand == Side.DARK)
            return "Includes dark side player's game text from " + GameUtils.getCardLink(location);
        else
            return "Includes light side player's game text from " + GameUtils.getCardLink(location);
    }

    @Override
    public PhysicalCard includesGameTextFrom(GameState gameState, ModifiersQuerying modifiersQuerying, Side side) {
        PhysicalCard location = modifiersQuerying.getLocationHere(gameState, getSource(gameState));
        if (location == null)
            return null;

        Side sideToExpand = gameState.getSide(_playerId);
        if (sideToExpand == side)
            return location;
        else
            return null;
    }
}
