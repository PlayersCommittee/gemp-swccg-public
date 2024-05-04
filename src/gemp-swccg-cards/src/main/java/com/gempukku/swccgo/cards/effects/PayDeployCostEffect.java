package com.gempukku.swccgo.cards.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.PlayCardOption;
import com.gempukku.swccgo.game.ReactActionOption;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect for paying the deploy cost for deploying a card.
 */
public class PayDeployCostEffect extends AbstractSubActionEffect {
    private PhysicalCard _cardToDeploy;
    private PhysicalCard _targetCard;
    private PlayCardOption _playCardOption;
    private float _changeInCost;
    private ReactActionOption _reactActionOption;
    private boolean _isDejarikRules;

    /**
     * Creates an effect for paying the deploy cost to deploy the specified card to the specified target.
     * @param action the action performing this effect
     * @param cardToDeploy the card being deployed
     * @param targetCard the target to where the card is being deployed, or null if not being deployed to a target (e.g. side of table)
     * @param playCardOption the play card option chosen
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @param reactActionOption a 'react' action option, or null if not a 'react'
     */
    public PayDeployCostEffect(Action action, PhysicalCard cardToDeploy, PhysicalCard targetCard, PlayCardOption playCardOption, float changeInCost, ReactActionOption reactActionOption) {
        this(action, cardToDeploy, targetCard, playCardOption, changeInCost, reactActionOption, false);
    }

    /**
     * Creates an effect for paying the deploy cost to deploy the specified card to the specified target.
     * @param action the action performing this effect
     * @param cardToDeploy the card being deployed
     * @param targetCard the target to where the card is being deployed, or null if not being deployed to a target (e.g. side of table)
     * @param playCardOption the play card option chosen
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @param reactActionOption a 'react' action option, or null if not a 'react'
     * @param isDejarikRules true if deploying using 'Dejarik Rules', otherwise false
     */
    public PayDeployCostEffect(Action action, PhysicalCard cardToDeploy, PhysicalCard targetCard, PlayCardOption playCardOption, float changeInCost, ReactActionOption reactActionOption, boolean isDejarikRules) {
        super(action);
        _cardToDeploy = cardToDeploy;
        _targetCard = targetCard;
        _playCardOption = playCardOption;
        _changeInCost = changeInCost;
        _reactActionOption = reactActionOption;
        _isDejarikRules = isDejarikRules;
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

        float deployCost = modifiersQuerying.getDeployCost(gameState, _action.getActionSource(), _cardToDeploy, _targetCard, _isDejarikRules, _playCardOption, false, _changeInCost, _reactActionOption, false);
        if (deployCost > 0) {
            boolean useBothForcePiles = !_isDejarikRules && modifiersQuerying.isDeployUsingBothForcePiles(gameState, _cardToDeploy, _targetCard);

            // Have opponent use Force first, so Beggar won't see those Force as available for use.
            if (useBothForcePiles) {
                subAction.appendEffect(new UseForceEffect(subAction, opponent, deployCost));
            }

            subAction.appendEffect(new UseForceEffect(subAction, playerId, deployCost));
        }

        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }
}
