package com.gempukku.swccgo.cards.effects;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.results.RelocateFromLostInSpaceOrWeatherVaneToLocationResult;


/**
 * An effect that relocates a card from Weather Vane to a location.
 */
public class RelocateFromWeatherVaneToLocation extends AbstractSuccessfulEffect {
    private PhysicalCard _card;
    private PhysicalCard _location;

    /**
     * Creates an effect that relocates a card from Weather Vane to a location.
     * @param action the action performing this effect
     * @param card the card to relocate from Weather Vane
     * @param location the location
     */
    public RelocateFromWeatherVaneToLocation(Action action, PhysicalCard card, PhysicalCard location) {
        super(action);
        _card = card;
        _location = location;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();
        PhysicalCard stackedOn = _card.getStackedOn();
        if (Filters.Weather_Vane.accepts(game, stackedOn)) {
            gameState.sendMessage(GameUtils.getCardLink(_card) + " is relocated from " + GameUtils.getCardLink(stackedOn) + " to " + GameUtils.getCardLink(_location));
            gameState.moveCardToLocation(_card, _location);
            game.getActionsEnvironment().emitEffectResult(new RelocateFromLostInSpaceOrWeatherVaneToLocationResult(_action.getPerformingPlayer(), _card, _location));
        }
    }
}
