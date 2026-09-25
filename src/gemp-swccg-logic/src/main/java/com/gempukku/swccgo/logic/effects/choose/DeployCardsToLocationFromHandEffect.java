package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.StandardEffect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * An effect that causes the player performing the action to repeatedly choose and deploy cards from hand
 * to a specified location until the player passes (or no deployable cards remain).
 */
public class DeployCardsToLocationFromHandEffect extends AbstractSubActionEffect {
    private final String _playerId;
    private final Filter _cardFilter;
    private final Filter _locationFilter;
    private final boolean _forFree;
    private int _numDeployed;

    /**
     * Creates an effect that causes the player performing the action to choose and deploy cards accepted by the card filter
     * from hand to a location accepted by the location filter (optional; may pass at any time).
     * @param action the action performing this effect
     * @param playerId the player
     * @param cardFilter the card filter
     * @param locationFilter the location filter
     * @param forFree true if deploying for free, otherwise false
     */
    public DeployCardsToLocationFromHandEffect(Action action, String playerId, Filter cardFilter, Filterable locationFilter, boolean forFree) {
        super(action);
        _playerId = playerId;
        _locationFilter = Filters.and(locationFilter);
        _forFree = forFree;
        // Location capability lives here, not on each calling card. deployableToLocation is not
        // enough by itself: side-of-table Effects still return a PlayCardToZoneAction.
        // Deploying on a location (e.g. Presence Of The Force) is not deploying to that site;
        // Effects that deploy on a card at the site (e.g. Disarmed) are.
        Filter deploysToSiteOrCardsThere = Filters.or(
                Filters.not(Filters.Effect),
                Filters.deploys_on_characters);
        _cardFilter = Filters.and(cardFilter, deploysToSiteOrCardsThere,
                Filters.deployableToLocation(action.getActionSource(), _locationFilter, forFree, 0));
        _numDeployed = 0;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    /**
     * Hand plus stacked cards that may deploy as if from hand (e.g. aliens on Den Of Thieves).
     */
    private Collection<PhysicalCard> cardsAvailableToDeploy(SwccgGame game) {
        List<PhysicalCard> cards = new ArrayList<PhysicalCard>();
        cards.addAll(game.getGameState().getHand(_playerId));
        cards.addAll(Filters.filter(game.getGameState().getAllStackedCards(), game,
                Filters.and(Filters.owner(_playerId), Filters.canDeployAsIfFromHand)));
        return Filters.filter(cards, game, _cardFilter);
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        final SubAction subAction = new SubAction(_action, _playerId);
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        if (!cardsAvailableToDeploy(game).isEmpty()) {
                            subAction.insertEffect(getChooseOneCardToDeployEffect(subAction));
                        }
                    }
                }
        );
        return subAction;
    }

    private StandardEffect getChooseOneCardToDeployEffect(final SubAction subAction) {
        // min 0 so the player may pass; each selection deploys one card then re-offers
        return new ChooseCardsFromHandEffect(subAction, _playerId, _playerId, 0, 1, _cardFilter, true, true) {
            @Override
            public boolean isPlayableInFull(SwccgGame game) {
                return true;
            }

            @Override
            public String getChoiceText(int numCardsToChoose) {
                return "Choose card" + GameUtils.s(numCardsToChoose) + " to deploy to that site (or pass)";
            }

            @Override
            protected void cardsSelected(SwccgGame game, Collection<PhysicalCard> selectedCards) {
                if (selectedCards.isEmpty()) {
                    return;
                }
                PhysicalCard selectedCard = selectedCards.iterator().next();
                _numDeployed++;
                subAction.insertEffect(
                        new DeployCardToLocationFromHandEffect(subAction, selectedCard, _locationFilter, _forFree, false),
                        new PassthruEffect(subAction) {
                            @Override
                            protected void doPlayEffect(SwccgGame game) {
                                if (!cardsAvailableToDeploy(game).isEmpty()) {
                                    subAction.insertEffect(getChooseOneCardToDeployEffect(subAction));
                                }
                            }
                        }
                );
            }
        };
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }

    public int getNumDeployed() {
        return _numDeployed;
    }
}
