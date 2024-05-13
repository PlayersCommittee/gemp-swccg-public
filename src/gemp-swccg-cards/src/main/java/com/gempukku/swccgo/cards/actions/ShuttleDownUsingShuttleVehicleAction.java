package com.gempukku.swccgo.cards.actions;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.AbstractTopLevelRuleAction;
import com.gempukku.swccgo.logic.effects.ShuttleDownUsingShuttleVehicleEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * An action to shuttle characters from a starship at the related system to an exterior site by using a shuttle vehicle.
 */
public class ShuttleDownUsingShuttleVehicleAction extends AbstractTopLevelRuleAction {
    private PhysicalCard _shuttleVehicle;
    private PhysicalCard _starship;
    private Effect _shuttleEffect;
    private boolean _cardsMoved;
    private Action _that;

    /**
     * Creates an action to shuttle characters from a starship at the related system to an exterior site by using a shuttle vehicle.
     * @param playerId the player
     * @param game the game
     * @param shuttleVehicle the shuttle vehicle
     */
    public ShuttleDownUsingShuttleVehicleAction(final String playerId, SwccgGame game, final PhysicalCard shuttleVehicle) {
        super(shuttleVehicle, playerId);
        _shuttleVehicle = shuttleVehicle;
        _that = this;

        final GameState gameState = game.getGameState();
        final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

        // Determine which starships contain a character that the shuttle vehicle can shuttle
        Collection<PhysicalCard> starships = Filters.filterActive(game, null, Filters.and(Filters.your(playerId),
                Filters.or(Filters.capital_starship, Filters.starfighter), Filters.presentAt(Filters.relatedSystem(shuttleVehicle))));
        final List<PhysicalCard> validStarships = new ArrayList<PhysicalCard>();
        for (PhysicalCard starship : starships) {
            // Get the characters aboard the starship
            Collection<PhysicalCard> characters = Filters.filterActive(game, null, Filters.and(Filters.character, Filters.aboardExceptRelatedSites(starship)));
            for (PhysicalCard character : characters) {
                if (Filters.canShuttleDownUsingShuttleVehicle(shuttleVehicle, Filters.sameCardId(starship)).accepts(gameState, modifiersQuerying, character)) {
                    validStarships.add(starship);
                    break;
                }
            }
        }

        appendTargeting(
                new ChooseCardOnTableEffect(_that, playerId, "Choose where to shuttle characters using " + GameUtils.getCardLink(shuttleVehicle), validStarships) {
                    @Override
                    protected void cardSelected(PhysicalCard selectedCard) {
                        _starship = selectedCard;

                        // Starship chosen, shuttle characters.
                        _shuttleEffect = new ShuttleDownUsingShuttleVehicleEffect(_that, _shuttleVehicle, _starship);
                    }
                }
        );
    }

    @Override
    public String getText() {
        return "Shuttle characters to here";
    }

    @Override
    public Effect nextEffect(SwccgGame game) {
        // Verify no costs have failed
        if (!isAnyCostFailed()) {

            Effect cost = getNextCost();
            if (cost != null)
                return cost;

            if (!_cardsMoved) {
                _cardsMoved = true;
                return _shuttleEffect;
            }

            Effect effect = getNextEffect();
            if (effect != null)
                return effect;
        }

        return null;
    }

    @Override
    public boolean wasActionCarriedOut() {
        return _cardsMoved && _shuttleEffect.wasCarriedOut();
    }
}
