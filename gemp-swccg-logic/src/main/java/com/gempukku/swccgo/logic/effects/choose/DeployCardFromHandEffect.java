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
 * An effect that causes the player performing the action to deploy a card from hand.
 */
public class DeployCardFromHandEffect extends AbstractSubActionEffect {
    private String _playerId;
    private Filter _cardFilter;
    private PhysicalCard _cardToDeploy;
    private Filter _targetFilter;
    private String _targetSystem;
    private Filter _specialLocationConditions;
    private boolean _forFree;
    private boolean _asReact;
    private float _changeInCost;
    private Filter _changeInCostCardFilter;
    private DeploymentOption _deploymentOption;
    private DeploymentRestrictionsOption _deploymentRestrictionsOption;
    private boolean _cardSelected;
    private DeployCardFromHandEffect _that;

    /**
     * Creates an effect that causes the specified player to choose and deploy a card accepted by the card filter from hand.
     * @param action the action performing this effect
     * @param playerId the player
     * @param cardFilter the card filter
     * @param forFree true if deploying for free, otherwise false
     */
    public DeployCardFromHandEffect(Action action, String playerId, Filter cardFilter, boolean forFree) {
        this(action, playerId, cardFilter, null, null, forFree);
    }

    /**
     * Creates an effect that causes the specified player to choose and deploy a card accepted by the card filter from hand.
     * @param action the action performing this effect
     * @param playerId the player
     * @param cardFilter the card filter
     * @param changeInCost change in amount of Force (can be positive or negative) required
     */
    public DeployCardFromHandEffect(Action action, String playerId, Filter cardFilter, float changeInCost) {
        this(action, playerId, cardFilter, null, null, null, false, changeInCost, null, null);
    }

    /**
     * Creates an effect that causes the specified player to choose and deploy a card accepted by the card filter from hand.
     * @param action the action performing this effect
     * @param playerId the player
     * @param cardFilter the card filter
     * @param forFree true if deploying for free, otherwise false
     * @param deploymentOption specifies special deployment options, or null
     */
    public DeployCardFromHandEffect(Action action, String playerId, Filter cardFilter, boolean forFree, DeploymentOption deploymentOption) {
        this(action, playerId, cardFilter, null, null, null, forFree, 0, deploymentOption, null);
    }

    /**
     * Creates an effect that causes the specified player to choose and deploy a card accepted by the card filter from hand.
     * @param action the action performing this effect
     * @param playerId the player
     * @param cardFilter the card filter
     * @param specialLocationConditions a filter for special conditions that deployed location must satisfy, or null
     * @param forFree true if deploying for free, otherwise false
     */
    public DeployCardFromHandEffect(Action action, String playerId, Filter cardFilter, Filter specialLocationConditions, boolean forFree) {
        this(action, playerId, cardFilter, null, specialLocationConditions, forFree);
    }

    /**
     * Creates an effect that causes the specified player to choose and deploy a card accepted by the card filter from hand
     * to a card accepted by the target filter.
     * @param action the action performing this effect
     * @param playerId the player
     * @param cardFilter the card filter
     * @param targetFilter the target filter, or null
     * @param specialLocationConditions a filter for special conditions that deployed location must satisfy, or null
     * @param forFree true if deploying for free, otherwise false
     */
    protected DeployCardFromHandEffect(Action action, String playerId, Filter cardFilter, Filter targetFilter, Filter specialLocationConditions, boolean forFree) {
        this(action, playerId, cardFilter, targetFilter, null, specialLocationConditions, forFree, 0, null, null);
    }

    /**
     * Creates an effect that causes the specified player to choose and deploy a card accepted by the card filter from hand
     * to the specified system.
     * @param action the action performing this effect
     * @param playerId the player
     * @param cardFilter the card filter
     * @param targetFilter the target filter, or null
     * @param targetSystem the system name, or null
     * @param specialLocationConditions a filter for special conditions that deployed location must satisfy, or null
     * @param forFree true if deploying for free, otherwise false
     * @param deploymentOption specifies special deployment options, or null
     * @param deploymentRestrictionsOption specifies which deployment restrictions are to be ignored, or null
     */
    protected DeployCardFromHandEffect(Action action, String playerId, Filter cardFilter, Filter targetFilter, String targetSystem, Filter specialLocationConditions, boolean forFree, float changeInCost, DeploymentOption deploymentOption, DeploymentRestrictionsOption deploymentRestrictionsOption) {
        super(action);
        _playerId = playerId;
        _cardFilter = cardFilter;
        _targetFilter = targetFilter;
        _targetSystem = targetSystem;
        _specialLocationConditions = specialLocationConditions;
        _forFree = forFree;
        _changeInCost = changeInCost;
        _changeInCostCardFilter = null;
        _asReact = false;
        _deploymentOption = deploymentOption;
        _deploymentRestrictionsOption = deploymentRestrictionsOption;
        _that = this;
    }

    /**
     * Creates an effect that causes the card owner to deploy the specified card from hand.
     * @param action the action performing this effect
     * @param cardToDeploy the card to deploy
     * @param forFree true if deploying for free, otherwise false
     */
    public DeployCardFromHandEffect(Action action, PhysicalCard cardToDeploy, boolean forFree) {
        this(action, cardToDeploy, null, forFree, false, null);
    }

    /**
     * Creates an effect that causes the card owner to deploy the specified card from hand to the specified target.
     * @param action the action performing this effect
     * @param cardToDeploy the card to deploy
     * @param targetFilter the target filter
     * @param forFree true if deploying for free, otherwise false
     * @param deploymentRestrictionsOption specifies which deployment restrictions are to be ignored, or null
     */
    protected DeployCardFromHandEffect(Action action, PhysicalCard cardToDeploy, Filter targetFilter, boolean forFree, boolean asReact, DeploymentRestrictionsOption deploymentRestrictionsOption) {
        super(action);
        _playerId = cardToDeploy.getOwner();
        _cardToDeploy = cardToDeploy;
        _targetFilter = targetFilter;
        _forFree = forFree;
        _changeInCost = 0;
        _changeInCostCardFilter = null;
        _asReact = asReact;
        _deploymentOption = null;
        _deploymentRestrictionsOption = deploymentRestrictionsOption;
        _that = this;
    }

    public String getChoiceText() {
        return "Choose card to deploy hand";
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
                _changeInCost, false, _action.getText(), Filters.any, Filters.any, null, false) : null;

        // Set the filters
        if (_targetSystem != null) {
            if (_targetFilter != null) {
                _targetFilter = Filters.and(_targetFilter, Filters.locationAndCardsAtLocation(Filters.partOfSystem(_targetSystem)));
                _cardFilter = (_cardFilter != null) ? Filters.and(_cardFilter, Filters.deployableToTarget(_action.getActionSource(), _targetFilter, false, _forFree, _changeInCost, _changeInCostCardFilter, _deploymentOption, _deploymentRestrictionsOption, null, reactActionOption)) : null;
                _cardToDeploy = (_cardToDeploy != null && Filters.deployableToTarget(_action.getActionSource(), _targetFilter, false, _forFree, _changeInCost, _changeInCostCardFilter, _deploymentOption, _deploymentRestrictionsOption, null, reactActionOption).accepts(game, _cardToDeploy)) ? _cardToDeploy : null;
            }
            else {
                _targetFilter = Filters.locationAndCardsAtLocation(Filters.partOfSystem(_targetSystem));
                _cardFilter = (_cardFilter != null) ? Filters.and(_cardFilter,  Filters.deployableToSystem(_action.getActionSource(), _targetSystem, false, _specialLocationConditions, _forFree, _changeInCost, _changeInCostCardFilter, _deploymentOption, _deploymentRestrictionsOption, reactActionOption)) : null;
                _cardToDeploy = (_cardToDeploy != null && Filters.deployableToSystem(_action.getActionSource(), _targetSystem, false, _specialLocationConditions, _forFree, _changeInCost, _changeInCostCardFilter, _deploymentOption, _deploymentRestrictionsOption, reactActionOption).accepts(game, _cardToDeploy)) ? _cardToDeploy : null;
            }
        }
        else if (_targetFilter != null) {
            _cardFilter = (_cardFilter != null) ? Filters.and(_cardFilter, Filters.deployableToTarget(_action.getActionSource(), _targetFilter, false, _forFree, _changeInCost, _changeInCostCardFilter, _deploymentOption, _deploymentRestrictionsOption, null, reactActionOption)) : null;
            _cardToDeploy = (_cardToDeploy != null && Filters.deployableToTarget(_action.getActionSource(), _targetFilter, false, _forFree, _changeInCost, _changeInCostCardFilter, _deploymentOption, _deploymentRestrictionsOption, null, reactActionOption).accepts(game, _cardToDeploy)) ? _cardToDeploy : null;
        }
        else {
            _targetFilter = Filters.any;
            _cardFilter = (_cardFilter != null) ? Filters.and(_cardFilter, Filters.deployable(_action.getActionSource(), false, _specialLocationConditions, _forFree, _changeInCost, _changeInCostCardFilter, _deploymentOption, _deploymentRestrictionsOption, reactActionOption)) : null;
            _cardToDeploy = (_cardToDeploy != null && Filters.deployable(_action.getActionSource(), false, _specialLocationConditions, _forFree, _changeInCost, _changeInCostCardFilter, _deploymentOption, _deploymentRestrictionsOption, reactActionOption).accepts(game, _cardToDeploy)) ? _cardToDeploy : null;
        }

        if (reactActionOption != null) {
            reactActionOption.setCardToReactFilter(_cardToDeploy != null ? _cardToDeploy : _cardFilter);
            reactActionOption.setForFreeCardFilter(Filters.none);
            reactActionOption.setTargetFilter(_targetFilter);
        }

        final SubAction subAction = new SubAction(_action, _playerId);
        subAction.setAllowAbort(_action.isAllowAbort());
        if (_cardFilter != null) {
            subAction.appendTargeting(
                    new ChooseCardFromHandEffect(subAction, _playerId, _cardFilter, true) {
                        @Override
                        protected void cardSelected(SwccgGame game, final PhysicalCard selectedCard) {
                            _cardSelected = true;

                            float changeInCostToUse = (_changeInCostCardFilter == null || _changeInCostCardFilter.accepts(game, selectedCard)) ? _changeInCost : 0;
                            PlayCardAction playCardAction;
                            if (_targetSystem != null && Filters.location.accepts(gameState, modifiersQuerying, selectedCard))
                                playCardAction = selectedCard.getBlueprint().getPlayLocationToSystemAction(_playerId, game, selectedCard, _action.getActionSource(), _targetSystem, _specialLocationConditions);
                            else
                                playCardAction = selectedCard.getBlueprint().getPlayCardAction(_playerId, game, selectedCard, _action.getActionSource(), _forFree, changeInCostToUse, _deploymentOption, _deploymentRestrictionsOption, null, reactActionOption, null, false, 0, _targetFilter, _specialLocationConditions);

                            subAction.insertEffect(
                                    new StackActionEffect(subAction, playCardAction),
                                    new PassthruEffect(subAction) {
                                        @Override
                                        protected void doPlayEffect(SwccgGame game) {
                                            cardDeployed(selectedCard);
                                        }
                                    }
                            );
                        }

                        @Override
                        public String getChoiceText(int numCardsToChoose) {
                            return _that.getChoiceText();
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
                                playCardAction = _cardToDeploy.getBlueprint().getPlayLocationToSystemAction(_playerId, game, _cardToDeploy, _action.getActionSource(), _targetSystem, _specialLocationConditions);
                            else
                                playCardAction = _cardToDeploy.getBlueprint().getPlayCardAction(_playerId, game, _cardToDeploy, _action.getActionSource(), _forFree, changeInCostToUse, _deploymentOption, _deploymentRestrictionsOption, null, reactActionOption, null, false, 0, _targetFilter, _specialLocationConditions);

                            subAction.insertEffect(
                                    new StackActionEffect(subAction, playCardAction),
                                    new PassthruEffect(subAction) {
                                        @Override
                                        protected void doPlayEffect(SwccgGame game) {
                                            cardDeployed(_cardToDeploy);
                                        }
                                    }
                            );
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

    /**
     * A callback method for the card deployed.
     * @param card the card deployed
     */
    protected void cardDeployed(PhysicalCard card) {
    }
}
