package com.gempukku.swccgo.cards.effects;

import com.gempukku.swccgo.common.LocationPlacementDirection;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.layout.LocationPlacement;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.effects.ChooseArbitraryCardsEffect;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.results.ConvertLocationResult;

import java.util.Collection;
import java.util.Collections;

public class ConvertLocationByRaisingToTopEffect extends AbstractSubActionEffect {
    private String _playerId;
    private PhysicalCard _topLocation;
    private boolean _convertToOwnersOnly;

    public ConvertLocationByRaisingToTopEffect(Action action, PhysicalCard topLocation, boolean convertToOwnersOnly) {
        super(action);
        _playerId = action.getPerformingPlayer();
        _topLocation = topLocation;
        _convertToOwnersOnly = convertToOwnersOnly;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        SubAction subAction = new SubAction(_action);
        Collection<PhysicalCard> canBeRaisedToTop = game.getGameState().getConvertedLocationsUnderTopLocation(_topLocation);
        if (_convertToOwnersOnly) {
            canBeRaisedToTop = Filters.filter(canBeRaisedToTop, game, Filters.owner(_playerId));
        }

        if (!canBeRaisedToTop.isEmpty()) {
            subAction.appendEffect(
                    new ChooseArbitraryCardsEffect(subAction, _playerId, "Choose location to raise to top", canBeRaisedToTop, 1, 1) {
                        @Override
                        protected void cardsSelected(SwccgGame game, Collection<PhysicalCard> selectedCards) {
                            PhysicalCard newTopLocation = selectedCards.iterator().next();
                            boolean isCollapsed = newTopLocation.isCollapsed();
                            GameState gameState = game.getGameState();

                            gameState.sendMessage(_action.getPerformingPlayer() + " raises " + GameUtils.getCardLink(newTopLocation) + " to the top to convert " + GameUtils.getCardLink(_topLocation));
                            LocationPlacement placement = new LocationPlacement(_topLocation.getPartOfSystem(), _topLocation.getBlueprint().getRelatedStarshipOrVehiclePersona(), _topLocation.getRelatedStarshipOrVehicle(), _topLocation, LocationPlacementDirection.REPLACE);
                            gameState.removeCardsFromZone(Collections.singleton(newTopLocation));
                            newTopLocation.setCollapsed(isCollapsed);
                            gameState.addLocationToTable(game, newTopLocation, placement);

                            // Emit effect result
                            game.getActionsEnvironment().emitEffectResult(
                                    new ConvertLocationResult(_action.getPerformingPlayer(), _topLocation, newTopLocation));
                        }
                    });
        }
        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }
}
