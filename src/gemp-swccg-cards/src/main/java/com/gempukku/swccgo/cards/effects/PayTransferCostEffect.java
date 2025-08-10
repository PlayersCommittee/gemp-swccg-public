package com.gempukku.swccgo.cards.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.PlayCardOption;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect for paying the transfer cost for transferring a card.
 */
public class PayTransferCostEffect extends AbstractSubActionEffect {
    private PhysicalCard _cardToDeploy;
    private PhysicalCard _targetCard;
    private PlayCardOption _playCardOption;

    /**
     * Creates an effect for paying the transfer cost to transfer the specified card to the specified target.
     * @param action the action performing this effect
     * @param cardToDeploy the card being transferred
     * @param targetCard the target to where the card is being transferred
     * @param playCardOption the play card option chosen
     */
    public PayTransferCostEffect(Action action, PhysicalCard cardToDeploy, PhysicalCard targetCard, PlayCardOption playCardOption) {
        super(action);
        _cardToDeploy = cardToDeploy;
        _targetCard = targetCard;
        _playCardOption = playCardOption;
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
        GameState gameState = game.getGameState();
        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

        SubAction subAction = new SubAction(_action);

        float transferCost = modifiersQuerying.getTransferCost(gameState, _cardToDeploy, _targetCard, _playCardOption);
        if (transferCost > 0) {
            subAction.appendEffect(new UseForceEffect(subAction, playerId, transferCost));
        }

        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }
}
