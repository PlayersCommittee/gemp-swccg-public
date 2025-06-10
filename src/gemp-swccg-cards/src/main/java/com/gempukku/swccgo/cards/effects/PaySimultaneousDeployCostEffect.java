package com.gempukku.swccgo.cards.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.ReactActionOption;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect for paying the deploy cost for deploying two cards simultaneously.
 */
public class PaySimultaneousDeployCostEffect extends AbstractSubActionEffect {
    private PhysicalCard _cardToDeploy;
    private boolean _cardToDeployForFree;
    private float _cardToDeployChangeInCost;
    private PhysicalCard _attachedCardToDeploy;
    private boolean _attachedCardToDeployForFree;
    private float _attachedCardToDeployChangeInCost;
    private PhysicalCard _targetCard;
    private ReactActionOption _reactActionOption;

    /**
     * Creates an effect for paying the deploy cost to deploy the specified cards to the specified target simultaneously.
     * @param action the action performing this effect
     * @param cardToDeploy the card being deployed
     * @param cardToDeployForFree true if the card deploys for free, otherwise false
     * @param cardToDeployChangeInCost change in amount of Force (can be positive or negative) required for the card being deployed
     * @param attachedCardToDeploy the attached card being deployed
     * @param attachedCardToDeployForFree true if the attached card deploys for free, otherwise false
     * @param attachedCardToDeployChangeInCost change in amount of Force (can be positive or negative) required for the attached card being deployed
     * @param targetCard the target to where the card is being deployed
     * @param reactActionOption a 'react' action option, or null if not a 'react'
     */
    public PaySimultaneousDeployCostEffect(Action action, PhysicalCard cardToDeploy, boolean cardToDeployForFree, float cardToDeployChangeInCost, PhysicalCard attachedCardToDeploy, boolean attachedCardToDeployForFree, float attachedCardToDeployChangeInCost, PhysicalCard targetCard, ReactActionOption reactActionOption) {
        super(action);
        _cardToDeploy = cardToDeploy;
        _cardToDeployForFree = cardToDeployForFree;
        _cardToDeployChangeInCost = cardToDeployChangeInCost;
        _attachedCardToDeploy = attachedCardToDeploy;
        _attachedCardToDeployForFree = attachedCardToDeployForFree;
        _attachedCardToDeployChangeInCost = attachedCardToDeployChangeInCost;
        _targetCard = targetCard;
        _reactActionOption = reactActionOption;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    /**
     * Gets the sub-action to perform.
     * @param game the game
     * @return the sub-action to perform.
     */
    @Override
    protected SubAction getSubAction(final SwccgGame game) {
        String playerId = _cardToDeploy.getOwner();
        String opponent = game.getOpponent(playerId);
        GameState gameState = game.getGameState();
        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

        SubAction subAction = new SubAction(_action);

        float deployCostForPlayer = modifiersQuerying.getSimultaneousDeployCost(gameState, _action.getActionSource(), _cardToDeploy, _cardToDeployForFree, _cardToDeployChangeInCost, _attachedCardToDeploy, _attachedCardToDeployForFree, _attachedCardToDeployChangeInCost, _targetCard, _reactActionOption, false);
        float deployCostForOpponent = 0;
        boolean useBothForcePiles = modifiersQuerying.isDeployUsingBothForcePiles(gameState, _cardToDeploy, _targetCard)
                || modifiersQuerying.isDeployUsingBothForcePiles(gameState, _attachedCardToDeploy, _targetCard);
        if (useBothForcePiles) {
            deployCostForOpponent = modifiersQuerying.getSimultaneousDeployCost(gameState, _action.getActionSource(), _cardToDeploy, _cardToDeployForFree, _cardToDeployChangeInCost, _attachedCardToDeploy, _attachedCardToDeployForFree, _attachedCardToDeployChangeInCost, _targetCard, _reactActionOption, false);
        }

        // Have opponent use Force first, so Beggar won't see those Force as available for use.
        if (deployCostForOpponent > 0) {
            subAction.appendEffect(new UseForceEffect(subAction, opponent, deployCostForOpponent));
        }

        if (deployCostForPlayer > 0) {
            subAction.appendEffect(new UseForceEffect(subAction, playerId, deployCostForPlayer));
        }

        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }
}
