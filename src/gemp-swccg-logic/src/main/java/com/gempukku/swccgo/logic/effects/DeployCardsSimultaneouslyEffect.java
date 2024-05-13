package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.DeploymentOption;
import com.gempukku.swccgo.game.DeploymentRestrictionsOption;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

/**
 * An effect that causes the player performing the action to deploy the specified cards simultaneously.
 */
public class DeployCardsSimultaneouslyEffect extends AbstractSubActionEffect {
    private PhysicalCard _cardToDeploy;
    private boolean _forFree;
    private float _changeInCost;
    private PhysicalCard _cardToDeployWith;
    private boolean _cardToDeployWithForFree;
    private float _cardToDeployWithChangeInCost;
    private Filter _targetFilter;
    private DeploymentOption _deploymentOption;
    private DeploymentRestrictionsOption _deploymentRestrictionsOption;

    /**
     * Creates an effect that causes the player performing the action to deploy the specified cards simultaneously.
     * @param action the action performing this effect
     * @param cardToDeploy the card to deploy with simultaneously
     * @param forFree true if deploying card for free, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required to deploy card
     * @param cardToDeployWith the card to deploy with
     * @param cardToDeployWithForFree true if card to deploy with deploys for free, otherwise false
     * @param cardToDeployWithChangeInCost change in amount of Force (can be positive or negative) required for card to deploy with
     */
    public DeployCardsSimultaneouslyEffect(Action action, PhysicalCard cardToDeploy, boolean forFree, float changeInCost, PhysicalCard cardToDeployWith, boolean cardToDeployWithForFree, float cardToDeployWithChangeInCost) {
        super(action);
        _cardToDeploy = cardToDeploy;
        _forFree = forFree;
        _changeInCost = changeInCost;
        _cardToDeployWith = cardToDeployWith;
        _cardToDeployWithForFree = cardToDeployWithForFree;
        _cardToDeployWithChangeInCost = cardToDeployWithChangeInCost;
        _targetFilter = Filters.any;
        _deploymentOption = null;
        _deploymentRestrictionsOption = null;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {

        final SubAction subAction = new SubAction(_action);
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        PlayCardAction playCardAction = _cardToDeploy.getBlueprint().getPlayCardAction(_action.getPerformingPlayer(), game, _cardToDeploy, _action.getActionSource(), _forFree, _changeInCost, _deploymentOption, _deploymentRestrictionsOption, null, null, _cardToDeployWith, _cardToDeployWithForFree, _cardToDeployWithChangeInCost, _targetFilter, null);
                        playCardAction.setReshuffle(true);
                        subAction.appendEffect(
                                new StackActionEffect(subAction, playCardAction));
                    }
                }
        );
        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }
}
