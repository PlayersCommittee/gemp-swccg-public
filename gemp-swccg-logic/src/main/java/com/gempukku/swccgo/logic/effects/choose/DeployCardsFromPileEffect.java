package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.DeploymentOption;
import com.gempukku.swccgo.game.DeploymentRestrictionsOption;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.effects.ShufflePileEffect;
import com.gempukku.swccgo.logic.effects.StackActionEffect;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.StandardEffect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * An effect that causes the player performing the action to choose and deploy cards from the specified card pile.
 */
class DeployCardsFromPileEffect extends AbstractSubActionEffect {
    private String _playerId;
    private Zone _cardPile;
    private Filter _cardFilter;
    private int _minimum;
    private int _maximum;
    private Filter _targetFilter;
    private String _targetSystem;
    private Filter _specialLocationConditions;
    private boolean _forFree;
    private float _changeInCost;
    private Filter _changeInCostCardFilter;
    private DeploymentOption _deploymentOption;
    private DeploymentRestrictionsOption _deploymentRestrictionsOption;
    private boolean _reshuffle;
    private List<PhysicalCard> _cardsDeployed = new ArrayList<PhysicalCard>();
    private int _numDeployed;
    private DeployCardsFromPileEffect _that;

    /**
     * Creates an effect that causes the player performing the action to choose and deploy cards by the card filter
     * from the specified card pile.
     * @param action the action performing this effect
     * @param zone the card pile
     * @param cardFilter the card filter
     * @param specialLocationConditions a filter for special conditions that deployed location must satisfy, or null
     * @param minimum the minimum number of cards to deploy
     * @param maximum the maximum number of cards to deploy
     * @param forFree true if deploying for free, otherwise false
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    protected DeployCardsFromPileEffect(Action action, Zone zone, Filter cardFilter, Filter specialLocationConditions, int minimum, int maximum, boolean forFree, boolean reshuffle) {
        this(action, zone, cardFilter, minimum, maximum, null, specialLocationConditions, forFree, 0, reshuffle);
    }

    /**
     * Creates an effect that causes the player performing the action to choose and deploy cards accepted by the card filter
     * from the specified card pile to a card accepted by the target filter.
     * @param action the action performing this effect
     * @param zone the card pile
     * @param cardFilter the card filter
     * @param minimum the minimum number of cards to deploy
     * @param maximum the maximum number of cards to deploy
     * @param targetFilter the target filter, or null
     * @param specialLocationConditions a filter for special conditions that deployed location must satisfy, or null
     * @param forFree true if deploying for free, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    protected DeployCardsFromPileEffect(Action action, Zone zone, Filter cardFilter, int minimum, int maximum, Filter targetFilter, Filter specialLocationConditions, boolean forFree, float changeInCost, boolean reshuffle) {
        this(action, action.getPerformingPlayer(), zone, cardFilter, minimum, maximum, targetFilter, null, specialLocationConditions, forFree, changeInCost, reshuffle);
    }

    /**
     * Creates an effect that causes the player performing the action to choose and deploy cards accepted by the card filter
     * from the specified card pile to the specified system.
     * @param action the action performing this effect
     * @param playerId the player to deploy cards
     * @param zone the card pile
     * @param cardFilter the card filter
     * @param minimum the minimum number of cards to deploy
     * @param maximum the maximum number of cards to deploy
     * @param targetFilter the target filter, or null
     * @param targetSystem the system name, or null
     * @param specialLocationConditions a filter for special conditions that deployed location must satisfy, or null
     * @param forFree true if deploying for free, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    protected DeployCardsFromPileEffect(Action action, String playerId, Zone zone, Filter cardFilter, int minimum, int maximum, Filter targetFilter, String targetSystem, Filter specialLocationConditions, boolean forFree, float changeInCost, boolean reshuffle) {
        super(action);
        _playerId = playerId;
        _cardPile = zone;
        _cardFilter = Filters.or(cardFilter, Filters.hasPermanentAboard(cardFilter));
        _minimum = minimum;
        _maximum = maximum;
        _targetFilter = targetFilter;
        _targetSystem = targetSystem;
        _specialLocationConditions = specialLocationConditions;
        _forFree = forFree;
        _changeInCost = changeInCost;
        _changeInCostCardFilter = null;
        _deploymentOption = null;
        _deploymentRestrictionsOption = null;
        _reshuffle = reshuffle;
        _that = this;

        // Set the filters
        if (_targetSystem != null) {
            if (_targetFilter != null) {
                _targetFilter = Filters.and(_targetFilter, Filters.locationAndCardsAtLocation(Filters.partOfSystem(_targetSystem)));
                _cardFilter = Filters.and(_cardFilter, Filters.deployableToTarget(_action.getActionSource(), _targetFilter, false, _forFree, _changeInCost, _changeInCostCardFilter, _deploymentOption, _deploymentRestrictionsOption, null, null));
            }
            else {
                _targetFilter = Filters.locationAndCardsAtLocation(Filters.partOfSystem(_targetSystem));
                _cardFilter = Filters.and(_cardFilter,  Filters.deployableToSystem(_action.getActionSource(), _targetSystem, false, specialLocationConditions, _forFree, _changeInCost, _changeInCostCardFilter, _deploymentOption, _deploymentRestrictionsOption, null));
            }
        }
        else if (_targetFilter != null) {
            _cardFilter = Filters.and(_cardFilter, Filters.deployableToTarget(_action.getActionSource(), _targetFilter, false, _forFree, _changeInCost, _changeInCostCardFilter, _deploymentOption, _deploymentRestrictionsOption, null, null));
        }
        else {
            _targetFilter = Filters.any;
            _cardFilter = Filters.and(_cardFilter, Filters.deployable(_action.getActionSource(), false, specialLocationConditions, _forFree, _changeInCost, _changeInCostCardFilter, _deploymentOption, _deploymentRestrictionsOption, null));
        }
    }

    public String getChoiceText(int numCardsToChoose) {
        return "Choose card" + GameUtils.s(numCardsToChoose) + " to deploy from " + _cardPile.getHumanReadable();
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        if (_cardsDeployed.size() < _minimum)
            return true;

        if (_cardsDeployed.size() < _maximum) {
            List<PhysicalCard> cardPile = new LinkedList<PhysicalCard>(game.getGameState().getCardPile(_playerId, _cardPile));
            return !Filters.filterCount(cardPile, game, 1, _cardFilter).isEmpty();
        }

        return false;
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        final SubAction subAction = new SubAction(_action, _playerId);

        subAction.appendEffect(getChooseOneCardToDeployEffect(subAction));
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        // Shuffle the card pile
                        if (_reshuffle) {
                            subAction.appendEffect(
                                    new ShufflePileEffect(subAction, subAction.getActionSource(), subAction.getPerformingPlayer(), subAction.getPerformingPlayer(), _cardPile, true));
                        }
                    }
                }
        );

        return subAction;
    }

    private StandardEffect getChooseOneCardToDeployEffect(final SubAction subAction) {
        return new ChooseCardsFromPileEffect(subAction, subAction.getPerformingPlayer(), _cardPile, subAction.getPerformingPlayer(), _cardsDeployed.size() < _minimum ? 1 : 0, 1, _maximum - _numDeployed, false, false, _cardFilter) {
            @Override
            public String getChoiceText(int numCardsToChoose) {
                return _that.getChoiceText(numCardsToChoose);
            }
            @Override
            protected void cardsSelected(final SwccgGame game, Collection<PhysicalCard> cards) {
                final GameState gameState = game.getGameState();
                final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

                if (!cards.isEmpty()) {
                    final PhysicalCard card = cards.iterator().next();

                    _cardsDeployed.add(card);
                    // Increment by acceptsCount since squadrons can count as more than one based on filter
                    _numDeployed += _cardFilter.acceptsCount(game, card);

                    float changeInCostToUse = (_changeInCostCardFilter == null || _changeInCostCardFilter.accepts(game, card)) ? _changeInCost : 0;
                    PlayCardAction playCardAction;
                    if (_targetSystem != null && Filters.location.accepts(gameState, modifiersQuerying, card))
                        playCardAction = card.getBlueprint().getPlayLocationToSystemAction(subAction.getPerformingPlayer(), game, card, _that.getAction().getActionSource(), _targetSystem, _specialLocationConditions);
                    else
                        playCardAction = card.getBlueprint().getPlayCardAction(subAction.getPerformingPlayer(), game, card, _that.getAction().getActionSource(), _forFree, changeInCostToUse, _deploymentOption, _deploymentRestrictionsOption, null, null, null, false, 0, _targetFilter, _specialLocationConditions);

                    subAction.insertEffect(
                            new StackActionEffect(subAction, playCardAction),
                            new PassthruEffect(subAction) {
                                @Override
                                protected void doPlayEffect(SwccgGame game) {
                                    if (_numDeployed < _that._maximum
                                            && _that.isPlayableInFull(game)) {
                                        subAction.insertEffect(
                                                getChooseOneCardToDeployEffect(subAction));
                                    }
                                }
                            }
                    );
                }
            }
            @Override
            public boolean isSkipTriggerPlayerLookedAtCardsInPile() {
                return _numDeployed > 0;
            }
        };
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return _numDeployed >= _minimum;
    }
}
