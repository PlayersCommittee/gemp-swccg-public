package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.DeploymentOption;
import com.gempukku.swccgo.game.DeploymentRestrictionsOption;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.effects.ShufflePileEffect;
import com.gempukku.swccgo.logic.effects.StackActionEffect;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

/**
 * An effect that causes the player performing the action to deploy a card from the specified card pile simultaneously
 * with a specified card.
 */
class DeployCardFromPileSimultaneouslyWithCardEffect extends AbstractSubActionEffect {
    private PhysicalCard _cardToDeployWith;
    private Zone _cardPile;
    private String _cardPileOwner;
    private Filter _cardFilter;
    private PhysicalCard _cardToDeploy;
    private Filter _targetFilter;
    private String _targetSystem;
    private boolean _forFree;
    private float _changeInCost;
    private Filter _forFreeCardFilter;
    private boolean _reshuffle;
    private DeploymentOption _deploymentOption;
    private DeploymentRestrictionsOption _deploymentRestrictionsOption;
    private boolean _cardSelected;
    private DeployCardFromPileSimultaneouslyWithCardEffect _that;

    /**
     * Creates an effect that causes the player performing the action to choose and deploy a card accepted by the card filter
     * from the specified card pile simultaneously with the specified card.
     * @param action the action performing this effect
     * @param cardToDeployWith the card to deploy with simultaneously
     * @param cardPile the card pile
     * @param cardPileOwner the card pile owner
     * @param cardFilter the card filter
     * @param forFree true if deploying for free, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @param forFreeCardFilter the card filter for cards that deploy for free, or null
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    protected DeployCardFromPileSimultaneouslyWithCardEffect(Action action, PhysicalCard cardToDeployWith, Zone cardPile, String cardPileOwner, Filter cardFilter, boolean forFree, float changeInCost, Filter forFreeCardFilter, boolean reshuffle) {
        this(action, cardToDeployWith, cardPile, cardPileOwner, cardFilter, null, forFree, changeInCost, forFreeCardFilter, reshuffle);
    }

    /**
     * Creates an effect that causes the player performing the action to choose and deploy a card accepted by the card filter
     * from the specified card pile simultaneously with the specified card to a card accepted by the target filter.
     * @param action the action performing this effect
     * @param cardToDeployWith the card to deploy with simultaneously
     * @param cardPile the card pile
     * @param cardPileOwner the card pile owner
     * @param cardFilter the card filter
     * @param targetFilter the target filter, or null
     * @param forFree true if deploying for free, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @param forFreeCardFilter the card filter for cards that deploy for free, or null
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    protected DeployCardFromPileSimultaneouslyWithCardEffect(Action action, PhysicalCard cardToDeployWith, Zone cardPile, String cardPileOwner, Filter cardFilter, Filter targetFilter, boolean forFree, float changeInCost, Filter forFreeCardFilter, boolean reshuffle) {
        this(action, cardToDeployWith, cardPile, cardPileOwner, cardFilter, targetFilter, null, forFree, changeInCost, forFreeCardFilter, reshuffle);
    }

    /**
     * Creates an effect that causes the player performing the action to choose and deploy a card accepted by the card filter
     * from the specified card pile simultaneously with the specified card to the specified system.
     * @param action the action performing this effect
     * @param cardToDeployWith the card to deploy with simultaneously
     * @param cardPile the card pile
     * @param cardPileOwner the card pile owner
     * @param cardFilter the card filter
     * @param targetFilter the target filter, or null
     * @param targetSystem the system name, or null
     * @param forFree true if deploying for free, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @param forFreeCardFilter the card filter for cards that deploy for free, or null
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    protected DeployCardFromPileSimultaneouslyWithCardEffect(Action action, PhysicalCard cardToDeployWith, Zone cardPile, String cardPileOwner, Filter cardFilter, Filter targetFilter, String targetSystem, boolean forFree, float changeInCost, Filter forFreeCardFilter, boolean reshuffle) {
        super(action);
        _cardToDeployWith = cardToDeployWith;
        _cardPile = cardPile;
        _cardPileOwner = cardPileOwner;
        _cardFilter = Filters.or(cardFilter, Filters.hasPermanentAboard(cardFilter));
        _targetFilter = targetFilter;
        _targetSystem = targetSystem;
        _forFree = forFree;
        _changeInCost = changeInCost;
        _forFreeCardFilter = forFreeCardFilter;
        _deploymentOption = null;
        _deploymentRestrictionsOption = null;
        _reshuffle = reshuffle;
        _that = this;
    }

    /**
     * Creates an effect that causes the player performing the action to deploy the specified card from the specified card
     * pile simultaneously with another specified card to the specified target.
     * @param action the action performing this effect
     * @param cardToDeployWith the card to deploy with simultaneously
     * @param card the card
     * @param targetFilter the target filter
     * @param forFree true if deploying for free, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required
     */
    protected DeployCardFromPileSimultaneouslyWithCardEffect(Action action, PhysicalCard cardToDeployWith, PhysicalCard card, Filter targetFilter, boolean forFree, float changeInCost) {
        super(action);
        _cardToDeployWith = cardToDeployWith;
        _cardPile = GameUtils.getZoneFromZoneTop(card.getZone());
        _cardPileOwner = card.getZoneOwner();
        _cardToDeploy = card;
        _targetFilter = targetFilter;
        _forFree = forFree;
        _changeInCost = changeInCost;
        _deploymentOption = null;
        _deploymentRestrictionsOption = null;
        _that = this;
    }

    public String getChoiceText() {
        return "Choose card to deploy from " + (_cardPileOwner.equals(_action.getPerformingPlayer()) ? "" : (_cardPileOwner + "'s ")) + _cardPile.getHumanReadable() + " simultaneously with " + GameUtils.getCardLink(_cardToDeployWith);
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        // Set the filters
        if (_targetSystem != null) {
            if (_targetFilter != null) {
                _targetFilter = Filters.and(_targetFilter, Filters.locationAndCardsAtLocation(Filters.partOfSystem(_targetSystem)));
                if (_cardFilter != null) {
                    if (_forFreeCardFilter != null) {
                        _cardFilter = Filters.and(_cardFilter, Filters.or(Filters.deployableToTargetSimultaneouslyWith(_action.getActionSource(), _cardToDeployWith, _forFree, _changeInCost, _targetFilter, _forFree, _changeInCost, _deploymentOption, _deploymentRestrictionsOption),
                                Filters.and(_forFreeCardFilter, Filters.deployableToTargetSimultaneouslyWith(_action.getActionSource(), _cardToDeployWith, _forFree, _changeInCost, _targetFilter, true, 0, _deploymentOption, _deploymentRestrictionsOption))));
                    }
                    else {
                        _cardFilter = Filters.and(_cardFilter, Filters.deployableToTargetSimultaneouslyWith(_action.getActionSource(), _cardToDeployWith, _forFree, _changeInCost, _targetFilter, _forFree, _changeInCost, _deploymentOption, _deploymentRestrictionsOption));
                    }
                }
                _cardToDeploy = (_cardToDeploy != null && Filters.deployableToTargetSimultaneouslyWith(_action.getActionSource(), _cardToDeployWith, _forFree, _changeInCost, _targetFilter, _forFree, _changeInCost, _deploymentOption, _deploymentRestrictionsOption).accepts(game, _cardToDeploy)) ? _cardToDeploy : null;
            }
            else {
                _targetFilter = Filters.locationAndCardsAtLocation(Filters.partOfSystem(_targetSystem));
                if (_cardFilter != null) {
                    if (_forFreeCardFilter != null) {
                        _cardFilter = Filters.and(_cardFilter, Filters.or(Filters.deployableToSystemSimultaneouslyWith(_action.getActionSource(), _cardToDeployWith, _forFree, _changeInCost, _targetSystem, _forFree, _changeInCost, _deploymentOption, _deploymentRestrictionsOption),
                                Filters.and(_forFreeCardFilter, Filters.deployableToSystemSimultaneouslyWith(_action.getActionSource(), _cardToDeployWith, _forFree, _changeInCost, _targetSystem, true, 0, _deploymentOption, _deploymentRestrictionsOption))));
                    }
                    else {
                        _cardFilter = Filters.and(_cardFilter,  Filters.deployableToSystemSimultaneouslyWith(_action.getActionSource(), _cardToDeployWith, _forFree, _changeInCost, _targetSystem, _forFree, _changeInCost, _deploymentOption, _deploymentRestrictionsOption));
                    }
                }
                _cardToDeploy = (_cardToDeploy != null && Filters.deployableToSystemSimultaneouslyWith(_action.getActionSource(), _cardToDeployWith, _forFree, _changeInCost, _targetSystem, _forFree, _changeInCost, _deploymentOption, _deploymentRestrictionsOption).accepts(game, _cardToDeploy)) ? _cardToDeploy : null;
            }
        }
        else if (_targetFilter != null) {
            if (_cardFilter != null) {
                if (_forFreeCardFilter != null) {
                    _cardFilter = Filters.and(_cardFilter, Filters.or(Filters.deployableToTargetSimultaneouslyWith(_action.getActionSource(), _cardToDeployWith, _forFree, _changeInCost, _targetFilter, _forFree, _changeInCost, _deploymentOption, _deploymentRestrictionsOption),
                            Filters.and(_forFreeCardFilter, Filters.deployableToTargetSimultaneouslyWith(_action.getActionSource(), _cardToDeployWith, _forFree, _changeInCost, _targetFilter, true, 0, _deploymentOption, _deploymentRestrictionsOption))));
                }
                else {
                    _cardFilter = Filters.and(_cardFilter, Filters.deployableToTargetSimultaneouslyWith(_action.getActionSource(), _cardToDeployWith, _forFree, _changeInCost, _targetFilter, _forFree, _changeInCost, _deploymentOption, _deploymentRestrictionsOption));
                }
            }
            _cardToDeploy = (_cardToDeploy != null && Filters.deployableToTargetSimultaneouslyWith(_action.getActionSource(), _cardToDeployWith, _forFree, _changeInCost, _targetFilter, _forFree, _changeInCost, _deploymentOption, _deploymentRestrictionsOption).accepts(game, _cardToDeploy)) ? _cardToDeploy : null;
        }
        else {
            _targetFilter = Filters.any;
            if (_cardFilter != null) {
                if (_forFreeCardFilter != null) {
                    _cardFilter = Filters.and(_cardFilter, Filters.or(Filters.deployableSimultaneouslyWith(_action.getActionSource(), _cardToDeployWith, _forFree, _changeInCost, _forFree, _changeInCost, _deploymentOption, _deploymentRestrictionsOption),
                            Filters.and(_forFreeCardFilter, Filters.deployableSimultaneouslyWith(_action.getActionSource(), _cardToDeployWith, _forFree, _changeInCost, true, 0, _deploymentOption, _deploymentRestrictionsOption))));
                }
                else {
                    _cardFilter = Filters.and(_cardFilter, Filters.deployableSimultaneouslyWith(_action.getActionSource(), _cardToDeployWith, _forFree, _changeInCost, _forFree, _changeInCost, _deploymentOption, _deploymentRestrictionsOption));
                }
            }
            _cardToDeploy = (_cardToDeploy != null && Filters.deployableSimultaneouslyWith(_action.getActionSource(), _cardToDeployWith, _forFree, _changeInCost, _forFree, _changeInCost, _deploymentOption, _deploymentRestrictionsOption).accepts(game, _cardToDeploy)) ? _cardToDeploy : null;
        }

        final SubAction subAction = new SubAction(_action);
        if (_cardFilter != null) {
            subAction.appendEffect(
                    new ChooseCardFromPileEffect(subAction, subAction.getPerformingPlayer(), _cardPile, _cardPileOwner, _cardFilter) {
                        @Override
                        protected void cardSelected(final SwccgGame game, final PhysicalCard selectedCard) {
                            _cardSelected = true;
                            if (_forFreeCardFilter != null) {
                                if (!_forFree) {
                                    _forFree = _forFreeCardFilter.accepts(game, selectedCard);
                                }
                            }

                            PlayCardAction playCardAction = selectedCard.getBlueprint().getPlayCardAction(_action.getPerformingPlayer(), game, selectedCard, _action.getActionSource(), _forFree, _changeInCost, _deploymentOption, _deploymentRestrictionsOption, null, null, _cardToDeployWith, _forFree, _changeInCost, _targetFilter, null);
                            playCardAction.setReshuffle(_reshuffle);
                            subAction.insertEffect(
                                    new StackActionEffect(subAction, playCardAction));
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
                            _cardSelected = true;

                            PlayCardAction playCardAction = _cardToDeploy.getBlueprint().getPlayCardAction(_action.getPerformingPlayer(), game, _cardToDeploy, _action.getActionSource(), _forFree, _changeInCost, _deploymentOption, _deploymentRestrictionsOption, null, null, _cardToDeployWith, _forFree, _changeInCost, _targetFilter, null);
                            playCardAction.setReshuffle(_reshuffle);
                            subAction.insertEffect(
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
