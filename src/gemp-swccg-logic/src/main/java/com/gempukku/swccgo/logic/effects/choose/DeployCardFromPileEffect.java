package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.*;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.effects.ShufflePileEffect;
import com.gempukku.swccgo.logic.effects.StackActionEffect;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

/**
 * An effect that causes the player performing the action to deploy a card from the specified card pile.
 */
public class DeployCardFromPileEffect extends AbstractSubActionEffect {
    private String _playerId;
    private Zone _cardPile;
    private String _cardPileOwner;
    private Filter _cardFilter;
    private PhysicalCard _cardToDeploy;
    private Filter _targetFilter;
    private Filter _ignoreTargetFilterCardFilter;
    private String _targetSystem;
    private Filter _specialLocationConditions;
    private boolean _forFree;
    private float _changeInCost;
    private Filter _forFreeCardFilter;
    private Filter _changeInCostCardFilter;
    private DeploymentOption _deploymentOption;
    private DeployAsCaptiveOption _deployAsCaptiveOption;
    private DeploymentRestrictionsOption _deploymentRestrictionsOption;
    private boolean _asReact;
    private boolean _reshuffle;
    private boolean _cardSelected;
    private DeployCardFromPileEffect _that;

    /**
     * Creates an effect that causes the player performing the action to choose and deploy a card accepted by the card filter
     * from the specified card pile.
     * @param action the action performing this effect
     * @param zone the card pile
     * @param cardFilter the card filter
     * @param ignoreTargetFilterCardFilter the card filter for cards that ignore the target filter
     * @param specialLocationConditions a filter for special conditions that deployed location must satisfy, or null
     * @param forFree true if deploying for free, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @param changeInCostCardFilter the card filter for cards that should have the changeInCost applied
     * @param forFreeCardFilter the card filter for cards that deploy for free, or null
     * @param asReact true if deploying as a react, otherwise false
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    protected DeployCardFromPileEffect(Action action, Zone zone, Filter cardFilter, Filter ignoreTargetFilterCardFilter, Filter specialLocationConditions, boolean forFree, float changeInCost, Filter changeInCostCardFilter, Filter forFreeCardFilter, boolean asReact, boolean reshuffle) {
        this(action, action.getPerformingPlayer(), zone, cardFilter, null, ignoreTargetFilterCardFilter, null, specialLocationConditions, forFree, forFreeCardFilter, changeInCost, changeInCostCardFilter, null, null, asReact, reshuffle);
    }

    /**
     * Creates an effect that causes the player performing the action to choose and deploy a card accepted by the card filter
     * from the specified card pile.
     * @param action the action performing this effect
     * @param zone the card pile
     * @param cardFilter the card filter
     * @param ignoreTargetFilterCardFilter the card filter for cards that ignore the target filter
     * @param specialLocationConditions a filter for special conditions that deployed location must satisfy, or null
     * @param forFree true if deploying for free, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @param forFreeCardFilter the card filter for cards that deploy for free, or null
     * @param asReact true if deploying as a react, otherwise false
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    protected DeployCardFromPileEffect(Action action, Zone zone, Filter cardFilter, Filter ignoreTargetFilterCardFilter, Filter specialLocationConditions, boolean forFree, float changeInCost, Filter forFreeCardFilter, boolean asReact, boolean reshuffle) {
        this(action, action.getPerformingPlayer(), zone, cardFilter, ignoreTargetFilterCardFilter, null, specialLocationConditions, forFree, changeInCost, forFreeCardFilter, null, null, asReact, reshuffle);
    }

    /**
     * Creates an effect that causes the player performing the action to choose and deploy a card accepted by the card filter
     * from the specified card pile to a card accepted by the target filter.
     * @param action the action performing this effect
     * @param performingPlayerId the player to deploy cards
     * @param zone the card pile
     * @param cardFilter the card filter
     * @param targetFilter the target filter, or null
     * @param ignoreTargetFilterCardFilter the card filter for cards that ignore the target filter
     * @param specialLocationConditions a filter for special conditions that deployed location must satisfy, or null
     * @param forFree true if deploying for free, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @param forFreeCardFilter the card filter for cards that deploy for free, or null
     * @param deploymentRestrictionsOption specifies which deployment restrictions are to be ignored, or null
     * @param deployAsCaptiveOption specifies the way to deploy the card as a captive, or null
     * @param asReact true if deploying as a react, otherwise false
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    protected DeployCardFromPileEffect(Action action, String performingPlayerId, Zone zone, Filter cardFilter, Filter targetFilter, Filter ignoreTargetFilterCardFilter, Filter specialLocationConditions, boolean forFree, float changeInCost, Filter forFreeCardFilter, DeploymentRestrictionsOption deploymentRestrictionsOption, DeployAsCaptiveOption deployAsCaptiveOption, boolean asReact, boolean reshuffle) {
        this(action, performingPlayerId, zone, cardFilter, targetFilter, ignoreTargetFilterCardFilter, null, specialLocationConditions, forFree, forFreeCardFilter, changeInCost, null, deploymentRestrictionsOption, deployAsCaptiveOption, asReact, reshuffle);
    }

    /**
     * Creates an effect that causes the player performing the action to choose and deploy a card accepted by the card filter
     * from the specified card pile to the specified system.
     * @param action the action performing this effect
     * @param performingPlayerId the player to deploy cards
     * @param zone the card pile
     * @param cardFilter the card filter
     * @param targetFilter the target filter, or null
     * @param ignoreTargetFilterCardFilter the card filter for cards that ignore the target filter
     * @param targetSystem the system name, or null
     * @param specialLocationConditions a filter for special conditions that deployed location must satisfy, or null
     * @param forFree true if deploying for free, otherwise false
     * @param forFreeCardFilter the card filter for cards that deploy for free, or null
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @param changeInCostCardFilter the card filter for cards that are affected by the change in cost, or null for all
     * @param deploymentRestrictionsOption specifies which deployment restrictions are to be ignored, or null
     * @param deployAsCaptiveOption specifies the way to deploy the card as a captive, or null
     * @param asReact true if deploying as a react, otherwise false
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    public DeployCardFromPileEffect(Action action, String performingPlayerId, Zone zone, Filter cardFilter, Filter targetFilter, Filter ignoreTargetFilterCardFilter, String targetSystem, Filter specialLocationConditions, boolean forFree, Filter forFreeCardFilter, float changeInCost, Filter changeInCostCardFilter, DeploymentRestrictionsOption deploymentRestrictionsOption, DeployAsCaptiveOption deployAsCaptiveOption, boolean asReact, boolean reshuffle) {
        super(action);
        _playerId = performingPlayerId;
        _cardPile = zone;
        _cardPileOwner = _playerId;
        _cardFilter = Filters.or(cardFilter, Filters.hasPermanentAboard(cardFilter));
        _targetFilter = targetFilter;
        _ignoreTargetFilterCardFilter = ignoreTargetFilterCardFilter;
        _targetSystem = targetSystem;
        _specialLocationConditions = specialLocationConditions;
        _forFree = forFree;
        _changeInCost = changeInCost;
        _forFreeCardFilter = forFreeCardFilter;
        _changeInCostCardFilter = changeInCostCardFilter;
        _deploymentOption = null;
        _deploymentRestrictionsOption = deploymentRestrictionsOption;
        _deployAsCaptiveOption = deployAsCaptiveOption;
        _asReact = asReact;
        _reshuffle = reshuffle;
        _that = this;
    }

    /**
     * Creates an effect that causes the player performing the action to deploy the specified card from the specified card
     * pile to the specified target.
     * @param action the action performing this effect
     * @param zone the card pile
     * @param card the card
     * @param targetFilter the target filter
     * @param forFree true if deploying for free, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @param asReact true if deploying as a react, otherwise false
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    protected DeployCardFromPileEffect(Action action, Zone zone, PhysicalCard card, Filter targetFilter, boolean forFree, float changeInCost, boolean asReact, boolean reshuffle) {
        super(action);
        _playerId = action.getPerformingPlayer();
        _cardPile = zone;
        _cardPileOwner = _playerId;
        _cardToDeploy = card;
        _targetFilter = targetFilter;
        _forFree = forFree;
        _changeInCost = changeInCost;
        _asReact = asReact;
        _deploymentOption = null;
        _deploymentRestrictionsOption = null;
        _deployAsCaptiveOption = null;
        _reshuffle = reshuffle;
        _that = this;
    }

    public String getChoiceText() {
        return "Choose card to deploy from " + _cardPile.getHumanReadable();
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return game.getGameState().getCardPileSize(_playerId, _cardPile) > 0;
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        final GameState gameState = game.getGameState();
        final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

        final ReactActionOption reactActionOption = _asReact ? new ReactActionOption(_action.getActionSource(), _forFree,
                _changeInCost, false, _action.getText(), Filters.any, Filters.any, null, false) : null;

        // Set the filters
        if (_targetSystem != null) {
            if (_targetFilter != null) {
                _targetFilter = Filters.and(_targetFilter, Filters.locationAndCardsAtLocation(Filters.partOfSystem(_targetSystem)));
                if (_cardFilter != null) {
                    Filter cardFilter = _cardFilter;
                    if (_forFreeCardFilter != null) {
                        _cardFilter = Filters.and(cardFilter, Filters.or(Filters.deployableToTarget(_action.getActionSource(), _targetFilter, false, _forFree, _changeInCost, _changeInCostCardFilter, _deploymentOption, _deploymentRestrictionsOption, _deployAsCaptiveOption, reactActionOption),
                                Filters.and(_forFreeCardFilter, Filters.deployableToTarget(_action.getActionSource(), _targetFilter, false, true, 0, _changeInCostCardFilter, _deploymentOption, _deploymentRestrictionsOption, _deployAsCaptiveOption, reactActionOption))));
                        if (_ignoreTargetFilterCardFilter != null) {
                            _cardFilter = Filters.or(_cardFilter, Filters.and(cardFilter, _ignoreTargetFilterCardFilter, Filters.deployable(_action.getActionSource(), false, _specialLocationConditions, _forFree, _changeInCost, _changeInCostCardFilter, _deploymentOption, _deploymentRestrictionsOption, reactActionOption),
                                    Filters.and(_forFreeCardFilter, Filters.deployable(_action.getActionSource(), false, _specialLocationConditions, true, 0, _changeInCostCardFilter, _deploymentOption, _deploymentRestrictionsOption, reactActionOption))));
                        }
                    }
                    else {
                        _cardFilter = Filters.and(cardFilter, Filters.deployableToTarget(_action.getActionSource(), _targetFilter, false, _forFree, _changeInCost, _changeInCostCardFilter, _deploymentOption, _deploymentRestrictionsOption, _deployAsCaptiveOption, reactActionOption));
                        if (_ignoreTargetFilterCardFilter != null) {
                            _cardFilter = Filters.or(_cardFilter, Filters.and(cardFilter, _ignoreTargetFilterCardFilter, Filters.deployable(_action.getActionSource(), false, _specialLocationConditions, _forFree, _changeInCost, _changeInCostCardFilter, _deploymentOption, _deploymentRestrictionsOption, reactActionOption)));
                        }
                    }
                }
                _cardToDeploy = (_cardToDeploy != null && Filters.deployableToTarget(_action.getActionSource(), _targetFilter, false, _forFree, _changeInCost, _changeInCostCardFilter, _deploymentOption, _deploymentRestrictionsOption, _deployAsCaptiveOption, reactActionOption).accepts(game, _cardToDeploy)) ? _cardToDeploy : null;
            }
            else {
                _targetFilter = Filters.locationAndCardsAtLocation(Filters.partOfSystem(_targetSystem));
                if (_cardFilter != null) {
                    Filter cardFilter = _cardFilter;
                    if (_forFreeCardFilter != null) {
                        _cardFilter = Filters.and(cardFilter, Filters.or(Filters.deployableToSystem(_action.getActionSource(), _targetSystem, false, _specialLocationConditions, _forFree, _changeInCost, _changeInCostCardFilter, _deploymentOption, _deploymentRestrictionsOption, reactActionOption),
                                Filters.and(_forFreeCardFilter, Filters.deployableToSystem(_action.getActionSource(), _targetSystem, false, _specialLocationConditions, true, 0, _changeInCostCardFilter, _deploymentOption, _deploymentRestrictionsOption, reactActionOption))));
                    }
                    else {
                        _cardFilter = Filters.and(cardFilter,  Filters.deployableToSystem(_action.getActionSource(), _targetSystem, false, _specialLocationConditions, _forFree, _changeInCost, _changeInCostCardFilter, _deploymentOption, _deploymentRestrictionsOption, reactActionOption));
                    }
                }
                _cardToDeploy = (_cardToDeploy != null && Filters.deployableToSystem(_action.getActionSource(), _targetSystem, false, _specialLocationConditions, _forFree, _changeInCost, _changeInCostCardFilter, _deploymentOption, _deploymentRestrictionsOption, reactActionOption).accepts(game, _cardToDeploy)) ? _cardToDeploy : null;
            }
        }
        else if (_targetFilter != null) {
            if (_cardFilter != null) {
                Filter cardFilter = _cardFilter;
                if (_forFreeCardFilter != null) {
                    _cardFilter = Filters.and(cardFilter, Filters.or(Filters.deployableToTarget(_action.getActionSource(), _targetFilter, false, _forFree, _changeInCost, _changeInCostCardFilter, _deploymentOption, _deploymentRestrictionsOption, _deployAsCaptiveOption, reactActionOption),
                            Filters.and(_forFreeCardFilter, Filters.deployableToTarget(_action.getActionSource(), _targetFilter, false, true, 0, _changeInCostCardFilter, _deploymentOption, _deploymentRestrictionsOption, _deployAsCaptiveOption, reactActionOption))));
                    if (_ignoreTargetFilterCardFilter != null) {
                        _cardFilter = Filters.or(_cardFilter, Filters.and(cardFilter, _ignoreTargetFilterCardFilter, Filters.deployable(_action.getActionSource(), false, _specialLocationConditions, _forFree, _changeInCost, _changeInCostCardFilter, _deploymentOption, _deploymentRestrictionsOption, reactActionOption),
                                Filters.and(_forFreeCardFilter, Filters.deployable(_action.getActionSource(), false, _specialLocationConditions, true, 0, _changeInCostCardFilter, _deploymentOption, _deploymentRestrictionsOption, reactActionOption))));
                    }
                }
                else {
                    _cardFilter = Filters.and(cardFilter, Filters.deployableToTarget(_action.getActionSource(), _targetFilter, false, _forFree, _changeInCost, _changeInCostCardFilter, _deploymentOption, _deploymentRestrictionsOption, _deployAsCaptiveOption, reactActionOption));
                    if (_ignoreTargetFilterCardFilter != null) {
                        _cardFilter = Filters.or(_cardFilter, Filters.and(cardFilter, _ignoreTargetFilterCardFilter, Filters.deployable(_action.getActionSource(), false, _specialLocationConditions, _forFree, _changeInCost, _changeInCostCardFilter, _deploymentOption, _deploymentRestrictionsOption, reactActionOption)));
                    }
                }
            }
            _cardToDeploy = (_cardToDeploy != null && Filters.deployableToTarget(_action.getActionSource(), _targetFilter, false, _forFree, _changeInCost, _changeInCostCardFilter, _deploymentOption, _deploymentRestrictionsOption, _deployAsCaptiveOption, reactActionOption).accepts(game, _cardToDeploy)) ? _cardToDeploy : null;
        }
        else {
            _targetFilter = Filters.any;
            if (_cardFilter != null) {
                Filter cardFilter = _cardFilter;
                if (_forFreeCardFilter != null) {
                    _cardFilter = Filters.and(cardFilter, Filters.or(Filters.deployable(_action.getActionSource(), false, _specialLocationConditions, _forFree, _changeInCost, _changeInCostCardFilter, _deploymentOption, _deploymentRestrictionsOption, reactActionOption),
                            Filters.and(_forFreeCardFilter, Filters.deployable(_action.getActionSource(), false, _specialLocationConditions, true, 0, _changeInCostCardFilter, _deploymentOption, _deploymentRestrictionsOption, reactActionOption))));
                }
                else {
                    _cardFilter = Filters.and(cardFilter, Filters.deployable(_action.getActionSource(), false, _specialLocationConditions, _forFree, _changeInCost, _changeInCostCardFilter, _deploymentOption, _deploymentRestrictionsOption, reactActionOption));
                }
            }
            _cardToDeploy = (_cardToDeploy != null && Filters.deployable(_action.getActionSource(), false, _specialLocationConditions, _forFree, _changeInCost, _changeInCostCardFilter, _deploymentOption, _deploymentRestrictionsOption, reactActionOption).accepts(game, _cardToDeploy)) ? _cardToDeploy : null;
        }

        if (reactActionOption != null) {
            reactActionOption.setCardToReactFilter(_cardToDeploy != null ? _cardToDeploy : _cardFilter);
            reactActionOption.setForFreeCardFilter(_forFreeCardFilter != null ? _forFreeCardFilter : Filters.none);
            reactActionOption.setTargetFilter(_targetFilter);
        }

        final SubAction subAction = new SubAction(_action, _playerId);
        if (_cardFilter != null) {
            subAction.appendEffect(
                    new ChooseCardFromPileEffect(subAction, subAction.getPerformingPlayer(), _cardPile, _cardPileOwner, _cardFilter) {
                        @Override
                        protected void cardSelected(final SwccgGame game, final PhysicalCard selectedCard) {
                            _cardSelected = true;
                            if (_forFreeCardFilter != null) {
                                if (reactActionOption != null) {
                                    if (!reactActionOption.isForFree()) {
                                        reactActionOption.setForFree(reactActionOption.getForFreeCardFilter() != null && reactActionOption.getForFreeCardFilter().accepts(game, selectedCard));
                                    }
                                }
                                else {
                                    if (!_forFree) {
                                        _forFree = _forFreeCardFilter.accepts(game, selectedCard);
                                    }
                                }
                            }
                            if (_ignoreTargetFilterCardFilter != null && _ignoreTargetFilterCardFilter.accepts(game, selectedCard)) {
                                _targetFilter = Filters.any;
                            }

                            float changeInCostToUse = (_changeInCostCardFilter == null || _changeInCostCardFilter.accepts(game, selectedCard)) ? _changeInCost : 0;
                            PlayCardAction playCardAction;
                            if (_targetSystem != null && Filters.location.accepts(gameState, modifiersQuerying, selectedCard)
                                    && (_ignoreTargetFilterCardFilter==null || !_ignoreTargetFilterCardFilter.accepts(game, selectedCard)))
                                playCardAction = selectedCard.getBlueprint().getPlayLocationToSystemAction(subAction.getPerformingPlayer(), game, selectedCard, _action.getActionSource(), _targetSystem, _specialLocationConditions);
                            else
                                playCardAction = selectedCard.getBlueprint().getPlayCardAction(subAction.getPerformingPlayer(), game, selectedCard, _action.getActionSource(), _forFree, changeInCostToUse, _deploymentOption, _deploymentRestrictionsOption, _deployAsCaptiveOption, reactActionOption, null, false, 0, _targetFilter, _specialLocationConditions);

                            playCardAction.setReshuffle(_reshuffle);
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

                        @Override
                        public boolean isForStealAndDeploy() {
                            return !subAction.getPerformingPlayer().equals(_cardPileOwner);
                        }
                    }
            );
            subAction.appendEffect(
                    new PassthruEffect(subAction) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            if (!_cardSelected && _reshuffle) {
                                subAction.appendEffect(
                                        new ShufflePileEffect(subAction, subAction.getActionSource(), subAction.getPerformingPlayer(), _cardPileOwner, _cardPile, true));
                            }
                        }
                    }
            );
        }
        else if (_cardToDeploy != null) {
            subAction.appendEffect(
                    new PassthruEffect(subAction) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {

                            float changeInCostToUse = (_changeInCostCardFilter == null || _changeInCostCardFilter.accepts(game, _cardToDeploy)) ? _changeInCost : 0;
                            PlayCardAction playCardAction;
                            if (_targetSystem != null && Filters.location.accepts(gameState, modifiersQuerying, _cardToDeploy))
                                playCardAction = _cardToDeploy.getBlueprint().getPlayLocationToSystemAction(subAction.getPerformingPlayer(), game, _cardToDeploy, _action.getActionSource(), _targetSystem, _specialLocationConditions);
                            else
                                playCardAction = _cardToDeploy.getBlueprint().getPlayCardAction(subAction.getPerformingPlayer(), game, _cardToDeploy, _action.getActionSource(), _forFree, changeInCostToUse, _deploymentOption, _deploymentRestrictionsOption, _deployAsCaptiveOption, reactActionOption, null, false, 0, _targetFilter, _specialLocationConditions);

                            // Check if card is no longer able to be deployed at this point
                            if (playCardAction == null) {
                                gameState.sendMessage(subAction.getPerformingPlayer() + " is unable to deploy " + GameUtils.getCardLink(_cardToDeploy) + " from " + (_cardPileOwner.equals(_action.getPerformingPlayer()) ? "" : (_cardPileOwner + "'s ")) + _cardPile.getHumanReadable());
                                return;
                            }

                            _cardSelected = true;

                            playCardAction.setReshuffle(_reshuffle);
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
