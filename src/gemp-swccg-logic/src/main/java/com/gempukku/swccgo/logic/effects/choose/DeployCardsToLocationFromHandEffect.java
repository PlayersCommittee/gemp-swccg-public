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

import java.util.Collection;

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
        _cardFilter = Filters.and(cardFilter, Filters.deployableToLocation(action.getActionSource(), _locationFilter, forFree, 0));
        _numDeployed = 0;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        final SubAction subAction = new SubAction(_action, _playerId);
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        Collection<PhysicalCard> fromHand = Filters.filter(game.getGameState().getHand(_playerId), game, _cardFilter);
                        if (!fromHand.isEmpty()) {
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
                                Collection<PhysicalCard> remaining = Filters.filter(game.getGameState().getHand(_playerId), game, _cardFilter);
                                if (!remaining.isEmpty()) {
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
