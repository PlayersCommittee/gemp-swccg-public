package com.gempukku.swccgo.logic.effects.choose;

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
public class DeployStackedCardsEffect extends AbstractSubActionEffect {
    private String _playerId;
    private PhysicalCard _stackedOn;
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
    private List<PhysicalCard> _cardsDeployed = new ArrayList<PhysicalCard>();
    private DeployStackedCardsEffect _that;

    /**
     * Creates an effect that causes the player performing the action to choose and deploy cards by the card filter
     * stacked on the specified card.
     * @param action the action performing this effect
     * @param stackedOn the card that the card to deploy is stacked on
     * @param cardFilter the card filter
     * @param minimum the minimum number of cards to deploy
     * @param maximum the maximum number of cards to deploy
     * @param forFree true if deploying for free, otherwise false
     */
    public DeployStackedCardsEffect(Action action, PhysicalCard stackedOn, Filter cardFilter, int minimum, int maximum, boolean forFree) {
        this(action, stackedOn, cardFilter, minimum, maximum, null, null, forFree);
    }

    /**
     * Creates an effect that causes the player performing the action to choose and deploy cards accepted by the card filter
     * stacked on the specified card to a card accepted by the target filter.
     * @param action the action performing this effect
     * @param stackedOn the card that the card to deploy is stacked on
     * @param cardFilter the card filter
     * @param minimum the minimum number of cards to deploy
     * @param maximum the maximum number of cards to deploy
     * @param targetFilter the target filter, or null
     * @param specialLocationConditions a filter for special conditions that deployed location must satisfy, or null
     * @param forFree true if deploying for free, otherwise false
     */
    protected DeployStackedCardsEffect(Action action, PhysicalCard stackedOn, Filter cardFilter, int minimum, int maximum, Filter targetFilter, Filter specialLocationConditions, boolean forFree) {
        this(action, action.getPerformingPlayer(), stackedOn, cardFilter, minimum, maximum, targetFilter, null, specialLocationConditions, forFree);
    }

    /**
     * Creates an effect that causes the player performing the action to choose and deploy cards accepted by the card filter
     * stacked on the specified card to the specified system.
     * @param action the action performing this effect
     * @param playerId the player to deploy cards
     * @param stackedOn the card that the card to deploy is stacked on
     * @param cardFilter the card filter
     * @param minimum the minimum number of cards to deploy
     * @param maximum the maximum number of cards to deploy
     * @param targetFilter the target filter, or null
     * @param targetSystem the system name, or null
     * @param specialLocationConditions a filter for special conditions that deployed location must satisfy, or null
     * @param forFree true if deploying for free, otherwise false
     */
    protected DeployStackedCardsEffect(Action action, String playerId, PhysicalCard stackedOn, Filter cardFilter, int minimum, int maximum, Filter targetFilter, String targetSystem, Filter specialLocationConditions, boolean forFree) {
        super(action);
        _playerId = playerId;
        _stackedOn = stackedOn;
        _cardFilter = Filters.or(cardFilter, Filters.hasPermanentAboard(cardFilter));
        _minimum = minimum;
        _maximum = maximum;
        _targetFilter = targetFilter;
        _targetSystem = targetSystem;
        _specialLocationConditions = specialLocationConditions;
        _forFree = forFree;
        _changeInCost = 0;
        _changeInCostCardFilter = null;
        _deploymentOption = null;
        _deploymentRestrictionsOption = null;
        _that = this;

        // Set the filters
        if (_targetSystem != null) {
            if (_targetFilter != null) {
                _targetFilter = Filters.and(_targetFilter, Filters.locationAndCardsAtLocation(Filters.partOfSystem(_targetSystem)));
                _cardFilter = Filters.and(_cardFilter, Filters.deployableToTarget(_action.getActionSource(), _targetFilter, false, _forFree, _changeInCost, _changeInCostCardFilter, _deploymentOption, _deploymentRestrictionsOption, null, null));
            }
            else {
                _targetFilter = Filters.locationAndCardsAtLocation(Filters.partOfSystem(_targetSystem));
                _cardFilter = Filters.and(_cardFilter,  Filters.deployableToSystem(_action.getActionSource(), _targetSystem, false, _specialLocationConditions, _forFree, _changeInCost, _changeInCostCardFilter, _deploymentOption, _deploymentRestrictionsOption, null));
            }
        }
        else if (_targetFilter != null) {
            _cardFilter = Filters.and(_cardFilter, Filters.deployableToTarget(_action.getActionSource(), _targetFilter, false, _forFree, _changeInCost, _changeInCostCardFilter, _deploymentOption, _deploymentRestrictionsOption, null, null));
        }
        else {
            _targetFilter = Filters.any;
            _cardFilter = Filters.and(_cardFilter, Filters.deployable(_action.getActionSource(), false, _specialLocationConditions, _forFree, _changeInCost, _changeInCostCardFilter, _deploymentOption, _deploymentRestrictionsOption, null));
        }
    }

    public String getChoiceText(int numCardsToChoose) {
        return "Choose card" + GameUtils.s(numCardsToChoose) + " stacked on " + GameUtils.getCardLink(_stackedOn) + " to deploy";
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        if (_cardsDeployed.size() < _minimum)
            return true;

        if (_cardsDeployed.size() < _maximum) {
            List<PhysicalCard> stackedCards = new LinkedList<PhysicalCard>(game.getGameState().getStackedCards(_stackedOn));
            return !Filters.filterCount(stackedCards, game, 1, _cardFilter).isEmpty();
        }

        return false;
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        final SubAction subAction = new SubAction(_action, _playerId);
        subAction.appendEffect(getChooseOneCardToDeployEffect(subAction));
        return subAction;
    }

    private StandardEffect getChooseOneCardToDeployEffect(final SubAction subAction) {
        return new ChooseStackedCardsEffect(subAction, subAction.getPerformingPlayer(), _stackedOn, _cardsDeployed.size() < _minimum ? 1 : 0, 1, _cardFilter) {
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

                    float changeInCostToUse = (_changeInCostCardFilter == null || _changeInCostCardFilter.accepts(game, card)) ? _changeInCost : 0;
                    PlayCardAction playCardAction;
                    if (_targetSystem != null && Filters.location.accepts(gameState, modifiersQuerying, card))
                        playCardAction = card.getBlueprint().getPlayLocationToSystemAction(subAction.getPerformingPlayer(), game, card, _action.getActionSource(), _targetSystem, _specialLocationConditions);
                    else
                        playCardAction = card.getBlueprint().getPlayCardAction(subAction.getPerformingPlayer(), game, card, _action.getActionSource(), _forFree, changeInCostToUse, _deploymentOption, _deploymentRestrictionsOption, null, null, null, false, 0, _targetFilter, _specialLocationConditions);

                    subAction.insertEffect(
                            new StackActionEffect(subAction, playCardAction),
                            new PassthruEffect(subAction) {
                                @Override
                                protected void doPlayEffect(SwccgGame game) {
                                    if (_cardsDeployed.size() < _that._maximum
                                            && _that.isPlayableInFull(game)) {
                                        subAction.insertEffect(
                                                getChooseOneCardToDeployEffect(subAction));
                                    }
                                }
                            }
                    );
                }
            }
        };
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return _cardsDeployed.size() >= _minimum;
    }
}
