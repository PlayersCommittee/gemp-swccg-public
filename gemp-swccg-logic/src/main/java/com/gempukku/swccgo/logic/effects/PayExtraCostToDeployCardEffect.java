package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect for paying extra costs for deploying a card.
 */
public class PayExtraCostToDeployCardEffect extends AbstractSubActionEffect {
    private PhysicalCard _cardToDeploy;
    private PhysicalCard _target;
    private boolean _forFree;

    /**
     * Creates an effect for paying extra costs for deploying a card.
     * @param action the action performing this effect
     * @param cardToDeploy the card to deploy
     * @param target the deploy target, or null
     * @param forFree true if deploying for free, otherwise false
     */
    public PayExtraCostToDeployCardEffect(Action action, PhysicalCard cardToDeploy, PhysicalCard target, boolean forFree) {
        super(action);
        _cardToDeploy = cardToDeploy;
        _target = target;
        _forFree = forFree;
    }

    /**
     * Checks whether this effect can be played in full. This is required to check
     * for example for cards that give a choice of effects to carry out and one
     * that can be played in full has to be chosen.
     *
     * @param game the game
     * @return true if (based on current info) the effect should be able to be fully carried out, otherwise false
     */
    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        GameState gameState = game.getGameState();
        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

        int extraCost = modifiersQuerying.getExtraForceRequiredToDeployToTarget(gameState, _cardToDeploy, _target, null, _action.getActionSource(), _forFree);
        if (extraCost == 0)
            return true;

        int forceAvailableToUse = modifiersQuerying.getForceAvailableToUse(gameState, _cardToDeploy.getOwner());
        if (forceAvailableToUse < extraCost)
            return false;

        return true;
    }

    /**
     * Gets the sub-action to perform.
     * @param game the game
     * @return the sub-action to perform.
     */
    @Override
    protected SubAction getSubAction(SwccgGame game) {
        GameState gameState = game.getGameState();
        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

        int extraCost = modifiersQuerying.getExtraForceRequiredToDeployToTarget(gameState, _cardToDeploy, _target, null, _action.getActionSource(), _forFree);

        SubAction subAction = new SubAction(_action);
        if (extraCost > 0) {
            subAction.appendEffect(new UseForceEffect(subAction, _cardToDeploy.getOwner(), extraCost));
        }
        return subAction;
    }

    /**
     * Determines if the action process was fully carried out.
     * The class that implements the getSubAction method should implement this method to do any additional checking as to
     * if the action was fully carried out.
     * @return true if the action was fully carried out, otherwise false
     */
    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }
}
