package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;

/**
 * An effect that performs the actions required when an illegal Objective is deployed.
 */
public class IllegalObjectiveEffect extends AbstractSuccessfulEffect {
    private PhysicalCard _objective;

    /**
     * Creates an effect that performs the actions required when an illegal Objective is deployed.
     * @param action the action performing this effect
     * @param objective the Objective
     */
    public IllegalObjectiveEffect(Action action, PhysicalCard objective) {
        super(action);
        _objective = objective;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();

        String playerId = _objective.getOwner();
        // Put any of player's non-location cards (except the Objective or Starting Effect) on table back in Reserve Deck
        Collection<PhysicalCard> nonLocationCards = Filters.filterAllOnTable(game,
                Filters.and(Filters.owner(playerId), Filters.not(Filters.or(_objective, Filters.location, Filters.Starting_Effect))));
        gameState.removeCardsFromZone(nonLocationCards);
        for(PhysicalCard card : nonLocationCards) {
            gameState.addCardToZone(card, Zone.RESERVE_DECK, playerId);
        }
        // Put any of player's location cards on table back in Reserve Deck
        Collection<PhysicalCard> locationCards = Filters.filterAllOnTable(game,
                Filters.and(Filters.owner(playerId), Filters.location));
        gameState.removeCardsFromZone(locationCards);
        for(PhysicalCard card : locationCards) {
            gameState.addCardToZone(card, Zone.RESERVE_DECK, playerId);
        }

        // Put the Objective out of play
        gameState.removeCardFromZone(_objective);
        gameState.addCardToZone(_objective, Zone.OUT_OF_PLAY, playerId);
    }
}
