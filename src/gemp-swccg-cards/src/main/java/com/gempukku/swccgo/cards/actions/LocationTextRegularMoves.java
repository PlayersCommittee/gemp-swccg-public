package com.gempukku.swccgo.cards.actions;

import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.ArrayList;
import java.util.List;

/**
 * Collects location-text movement as a regular-move option for a specific card (#954).
 */
public final class LocationTextRegularMoves {
    private LocationTextRegularMoves() {
    }

    /**
     * @return a location-text move action for the card, a choice among several, or null
     */
    public static Action forCard(String playerId, SwccgGame game, PhysicalCard cardToMove, boolean forFree, Filter moveTargetFilter) {
        GameState gameState = game.getGameState();
        gameState.beginIncludeLocationTextAsRegularMove();
        try {
            List<Action> found = new ArrayList<Action>();
            for (PhysicalCard location : Filters.filterTopLocationsOnTable(game, Filters.location)) {
                List<Action> locationActions = location.getBlueprint().getTopLevelActions(playerId, game, location);
                if (locationActions == null) {
                    continue;
                }
                for (Action action : locationActions) {
                    if (action instanceof MoveUsingLocationTextAction) {
                        Action locked = ((MoveUsingLocationTextAction) action).lockToCard(game, cardToMove, moveTargetFilter, forFree);
                        if (locked != null) {
                            found.add(locked);
                        }
                    }
                }
            }
            if (found.isEmpty()) {
                return null;
            }
            if (found.size() == 1) {
                return found.get(0);
            }
            return new ChoiceAction(playerId, "Choose location-text move", found);
        } finally {
            gameState.endIncludeLocationTextAsRegularMove();
        }
    }
}
