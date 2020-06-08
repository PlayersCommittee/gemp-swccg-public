package com.gempukku.swccgo.cards.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.results.RelocateToWeatherVaneResult;


/**
 * An effect that relocates a card from Lost Pile to Weather Vane.
 */
public class RelocateFromLostPileToWeatherVane extends AbstractSuccessfulEffect {
    private PhysicalCard _card;

    /**
     * Creates an effect that relocates a card from Lost Pile to Weather Vane.
     * @param action the action performing this effect
     * @param card the card to relocate to Weather Vane
     */
    public RelocateFromLostPileToWeatherVane(Action action, PhysicalCard card) {
        super(action);
        _card = card;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();
        Zone fromZone = GameUtils.getZoneFromZoneTop(_card.getZone());
        if (fromZone == Zone.LOST_PILE) {
            PhysicalCard weatherVane = Filters.findFirstActive(game, null, Filters.Weather_Vane);
            if (weatherVane != null) {
                gameState.removeCardFromZone(_card);
                gameState.stackCard(_card, weatherVane, false, true, false);
                gameState.sendMessage(GameUtils.getCardLink(_card) + " is relocated from " + fromZone.getHumanReadable() + " to " + GameUtils.getCardLink(weatherVane));
                game.getActionsEnvironment().emitEffectResult(new RelocateToWeatherVaneResult(_action.getPerformingPlayer(), _card, fromZone));
            }
        }
    }
}
