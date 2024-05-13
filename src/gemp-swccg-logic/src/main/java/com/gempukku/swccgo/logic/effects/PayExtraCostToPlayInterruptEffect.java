package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect for paying extra costs for playing an interrupt card.
 */
public class PayExtraCostToPlayInterruptEffect extends AbstractSubActionEffect {
    private PhysicalCard _physicalCard;

    /**
     * Creates an effect for paying extra costs for playing an interrupt card.
     * @param action the action performing this effect
     * @param physicalCard the interrupt being played
     */
    public PayExtraCostToPlayInterruptEffect(Action action, PhysicalCard physicalCard) {
        super(action);
        _physicalCard = physicalCard;
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
        int extraCost = game.getModifiersQuerying().getExtraForceRequiredToPlayInterrupt(game.getGameState(), _physicalCard);
        if (extraCost == 0)
            return true;

        int forceAvailableToUse = game.getModifiersQuerying().getForceAvailableToUse(game.getGameState(), _physicalCard.getOwner());
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
        int extraCost = game.getModifiersQuerying().getExtraForceRequiredToPlayInterrupt(game.getGameState(), _physicalCard);

        SubAction subAction = new SubAction(_action);
        if (extraCost > 0) {
            subAction.appendEffect(new UseForceEffect(subAction, _physicalCard.getOwner(), extraCost));
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
