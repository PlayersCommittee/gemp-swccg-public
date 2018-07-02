package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.*;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.effects.StackActionEffect;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

/**
 * An effect that causes the player performing the action to deploy a card that is stacked on a specified card.
 */
public class DeployStackedCardEffect extends AbstractSubActionEffect {
    private Filter _cardFilter;
    private PhysicalCard _stackedOn;
    private PhysicalCard _cardToDeploy;
    private Filter _targetFilter;
    private String _targetSystem;
    private Filter _specialLocationConditions;
    private boolean _forFree;
    private float _changeInCost;
    private Filter _changeInCostCardFilter;
    private DeploymentOption _deploymentOption;
    private DeploymentRestrictionsOption _deploymentRestrictionsOption;
    private boolean _asReact;
    private boolean _includePlayable;
    private boolean _cardSelected;

    /**
     * Creates an effect that causes the player performing the action to choose and deploy a card accepted by the card filter
     * that is stacked on the specified card.
     * @param action the action performing this effect
     * @param stackedOn the card that the card to deploy is stacked on
     * @param cardFilter the card filter
     * @param forFree true if deploying for free, otherwise false
     */
    public DeployStackedCardEffect(Action action, PhysicalCard stackedOn, Filter cardFilter, boolean forFree) {
        this(action, stackedOn, cardFilter, false, forFree);
    }

    /**
     * Creates an effect that causes the player performing the action to choose and deploy a card accepted by the card filter
     * that is stacked on the specified card.
     * @param action the action performing this effect
     * @param stackedOn the card that the card to deploy is stacked on
     * @param cardFilter the card filter
     * @param includePlayable true if includes playable cards, false if only deployable cards
     * @param forFree true if deploying for free, otherwise false
     */
    public DeployStackedCardEffect(Action action, PhysicalCard stackedOn, Filter cardFilter, boolean includePlayable, boolean forFree) {
        this(action, stackedOn, cardFilter, includePlayable, null, forFree, false);
    }

    /**
     * Creates an effect that causes the player performing the action to choose and deploy a card accepted by the card filter
     * that is stacked on the specified card.
     * @param action the action performing this effect
     * @param stackedOn the card that the card to deploy is stacked on
     * @param cardFilter the card filter
     * @param includePlayable true if includes playable cards, false if only deployable cards
     * @param specialLocationConditions a filter for special conditions that deployed location must satisfy, or null
     * @param forFree true if deploying for free, otherwise false
     * @param asReact true if deploying as a react, otherwise false
     */
    protected DeployStackedCardEffect(Action action, PhysicalCard stackedOn, Filter cardFilter, boolean includePlayable, Filter specialLocationConditions, boolean forFree, boolean asReact) {
        this(action, stackedOn, cardFilter, null, includePlayable, specialLocationConditions, forFree, asReact);
    }

    /**
     * Creates an effect that causes the player performing the action to choose and deploy a card accepted by the card filter
     * that is stacked on the specified card to a card accepted by the target filter.
     * @param action the action performing this effect
     * @param stackedOn the card that the card to deploy is stacked on
     * @param cardFilter the card filter
     * @param targetFilter the target filter, or null
     * @param includePlayable true if includes playable cards, false if only deployable cards
     * @param specialLocationConditions a filter for special conditions that deployed location must satisfy, or null
     * @param forFree true if deploying for free, otherwise false
     * @param asReact true if deploying as a react, otherwise false
     */
    protected DeployStackedCardEffect(Action action, PhysicalCard stackedOn, Filter cardFilter, Filter targetFilter, boolean includePlayable, Filter specialLocationConditions, boolean forFree, boolean asReact) {
        this(action, stackedOn, cardFilter, targetFilter, null, includePlayable, specialLocationConditions, forFree, asReact);
    }

    /**
     * Creates an effect that causes the player performing the action to choose and deploy a card accepted by the card filter
     * that is stacked on the specified card to the specified system.
     * @param action the action performing this effect
     * @param stackedOn the card that the card to deploy is stacked on
     * @param cardFilter the card filter
     * @param targetFilter the target filter, or null
     * @param targetSystem the system name, or null
     * @param includePlayable true if includes playable cards, false if only deployable cards
     * @param specialLocationConditions a filter for special conditions that deployed location must satisfy, or null
     * @param forFree true if deploying for free, otherwise false
     * @param asReact true if deploying as a react, otherwise false
     */
    protected DeployStackedCardEffect(Action action, PhysicalCard stackedOn, Filter cardFilter, Filter targetFilter, String targetSystem, boolean includePlayable, Filter specialLocationConditions, boolean forFree, boolean asReact) {
        super(action);
        _stackedOn = stackedOn;
        _cardFilter = cardFilter;
        _targetFilter = targetFilter;
        _targetSystem = targetSystem;
        _includePlayable = includePlayable;
        _specialLocationConditions = specialLocationConditions;
        _forFree = forFree;
        _changeInCost = 0;
        _changeInCostCardFilter = null;
        _deploymentOption = null;
        _deploymentRestrictionsOption = null;
        _asReact = asReact;
    }

    /**
     * Creates an effect that causes the player performing the action to deploy the specified card that is stacked on the
     * specified card to the specified target.
     * @param action the action performing this effect
     * @param stackedOn the card that the card to deploy is stacked on
     * @param cardToDeploy the card to deploy
     * @param targetFilter the target filter
     * @param forFree true if deploying for free, otherwise false
     */
    protected DeployStackedCardEffect(Action action, PhysicalCard stackedOn, PhysicalCard cardToDeploy, Filter targetFilter, boolean forFree) {
        super(action);
        _stackedOn = stackedOn;
        _cardToDeploy = cardToDeploy;
        _targetFilter = targetFilter;
        _includePlayable = true;
        _forFree = forFree;
        _deploymentOption = null;
        _deploymentRestrictionsOption = null;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(final SwccgGame game) {
        final GameState gameState = game.getGameState();
        final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

        final ReactActionOption reactActionOption = _asReact ? new ReactActionOption(_action.getActionSource(), _forFree,
                0, false, _action.getText(), Filters.any, Filters.any, null, false) : null;

        // Set the filters
        if (_targetSystem != null) {
            if (_targetFilter != null) {
                _targetFilter = Filters.and(_targetFilter, Filters.locationAndCardsAtLocation(Filters.partOfSystem(_targetSystem)));
                _cardFilter = (_cardFilter != null) ? Filters.and(_cardFilter, Filters.deployableToTarget(_action.getActionSource(), _targetFilter, _includePlayable, _forFree, _changeInCost, _changeInCostCardFilter, _deploymentOption, _deploymentRestrictionsOption, null, reactActionOption)) : null;
                _cardToDeploy = (_cardToDeploy != null && Filters.deployableToTarget(_action.getActionSource(), _targetFilter, _includePlayable, _forFree, _changeInCost, _changeInCostCardFilter, _deploymentOption, _deploymentRestrictionsOption, null, reactActionOption).accepts(game, _cardToDeploy)) ? _cardToDeploy : null;
            }
            else {
                _targetFilter = Filters.locationAndCardsAtLocation(Filters.partOfSystem(_targetSystem));
                _cardFilter = (_cardFilter != null) ? Filters.and(_cardFilter,  Filters.deployableToSystem(_action.getActionSource(), _targetSystem, _includePlayable, _specialLocationConditions, _forFree, _changeInCost, _changeInCostCardFilter, _deploymentOption, _deploymentRestrictionsOption, reactActionOption)) : null;
                _cardToDeploy = (_cardToDeploy != null && Filters.deployableToSystem(_action.getActionSource(), _targetSystem, _includePlayable, _specialLocationConditions, _forFree, _changeInCost, _changeInCostCardFilter, _deploymentOption, _deploymentRestrictionsOption, reactActionOption).accepts(game, _cardToDeploy)) ? _cardToDeploy : null;
            }
        }
        else if (_targetFilter != null) {
            _cardFilter = (_cardFilter != null) ? Filters.and(_cardFilter, Filters.deployableToTarget(_action.getActionSource(), _targetFilter, _includePlayable, _forFree, _changeInCost, _changeInCostCardFilter, _deploymentOption, _deploymentRestrictionsOption, null, reactActionOption)) : null;
            _cardToDeploy = (_cardToDeploy != null && Filters.deployableToTarget(_action.getActionSource(), _targetFilter, _includePlayable, _forFree, _changeInCost, _changeInCostCardFilter, _deploymentOption, _deploymentRestrictionsOption, null, reactActionOption).accepts(game, _cardToDeploy)) ? _cardToDeploy : null;
        }
        else {
            _targetFilter = Filters.any;
            _cardFilter = (_cardFilter != null) ? Filters.and(_cardFilter, Filters.deployable(_action.getActionSource(), _includePlayable, _specialLocationConditions, _forFree, _changeInCost, _changeInCostCardFilter, _deploymentOption, _deploymentRestrictionsOption, reactActionOption)) : null;
            _cardToDeploy = (_cardToDeploy != null && Filters.deployable(_action.getActionSource(), _includePlayable, _specialLocationConditions, _forFree, _changeInCost, _changeInCostCardFilter, _deploymentOption, _deploymentRestrictionsOption, reactActionOption).accepts(game, _cardToDeploy)) ? _cardToDeploy : null;
        }

        if (reactActionOption != null) {
            reactActionOption.setCardToReactFilter(_cardToDeploy != null ? _cardToDeploy : _cardFilter);
            reactActionOption.setForFreeCardFilter(Filters.none);
            reactActionOption.setTargetFilter(_targetFilter);
        }

        final SubAction subAction = new SubAction(_action);
        if (_cardFilter != null) {
            subAction.appendEffect(
                    new ChooseStackedCardEffect(subAction, subAction.getPerformingPlayer(), _stackedOn, _cardFilter) {
                        @Override
                        protected void cardSelected(PhysicalCard selectedCard) {
                            _cardSelected = true;

                            float changeInCostToUse = (_changeInCostCardFilter == null || _changeInCostCardFilter.accepts(game, selectedCard)) ? _changeInCost : 0;
                            PlayCardAction playCardAction;
                            if (_targetSystem != null && Filters.location.accepts(gameState, modifiersQuerying, selectedCard))
                                playCardAction = selectedCard.getBlueprint().getPlayLocationToSystemAction(_action.getPerformingPlayer(), game, selectedCard, _action.getActionSource(), _targetSystem, _specialLocationConditions);
                            else
                                playCardAction = selectedCard.getBlueprint().getPlayCardAction(_action.getPerformingPlayer(), game, selectedCard, _action.getActionSource(), _forFree, changeInCostToUse, _deploymentOption, _deploymentRestrictionsOption, null, reactActionOption, null, false, 0, _targetFilter, _specialLocationConditions);

                            subAction.appendEffect(
                                    new StackActionEffect(subAction, playCardAction));
                        }
                    }
            );
        }
        else if (_cardToDeploy != null) {
            subAction.appendEffect(
                    new PassthruEffect(subAction) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            _cardSelected = true;

                            float changeInCostToUse = (_changeInCostCardFilter == null || _changeInCostCardFilter.accepts(game, _cardToDeploy)) ? _changeInCost : 0;
                            PlayCardAction playCardAction;
                            if (_targetSystem != null && Filters.location.accepts(gameState, modifiersQuerying, _cardToDeploy))
                                playCardAction = _cardToDeploy.getBlueprint().getPlayLocationToSystemAction(_action.getPerformingPlayer(), game, _cardToDeploy, _action.getActionSource(), _targetSystem, _specialLocationConditions);
                            else
                                playCardAction = _cardToDeploy.getBlueprint().getPlayCardAction(_action.getPerformingPlayer(), game, _cardToDeploy, _action.getActionSource(), _forFree, changeInCostToUse, _deploymentOption, _deploymentRestrictionsOption, null, reactActionOption, null, false, 0, _targetFilter, _specialLocationConditions);

                            subAction.appendEffect(
                                    new StackActionEffect(subAction, playCardAction));
                        }
                    }
            );
        }
        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return _cardSelected;
    }
}
