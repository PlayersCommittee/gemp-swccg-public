package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.DrawDestinyState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collection;

/**
 * An effect that causes the player performing the action to stack the just drawn destiny card on a card.
 */
public class StackDestinyCardEffect extends AbstractSubActionEffect {
    private PhysicalCard _stackOn;
    private boolean _faceDown;
    private boolean _asInactive;

    /**
     * Creates an effect that causes the player performing the action to stack the just drawn destiny card on a card.
     * @param action the action performing this effect
     * @param stackOn the card to stack on
     */
    public StackDestinyCardEffect(Action action, PhysicalCard stackOn) {
        this(action, stackOn, false);
    }

    /**
     * Creates an effect that causes the player performing the action to stack the just drawn destiny card on a card.
     * @param action the action performing this effect
     * @param stackOn the card to stack on
     * @param faceDown true if card is to be stacked face down, otherwise false
     */
    public StackDestinyCardEffect(Action action, PhysicalCard stackOn, boolean faceDown) {
        super(action);
        _stackOn = stackOn;
        _faceDown = faceDown;
        _asInactive = false;
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
                        GameState gameState = game.getGameState();
                        DrawDestinyState drawDestinyState = gameState.getTopDrawDestinyState();
                        if (drawDestinyState != null) {
                            PhysicalCard destinyCard = drawDestinyState.getDrawDestinyEffect().getDrawnDestinyCard();
                            if (destinyCard != null
                                    && destinyCard.getZone().isUnresolvedDestinyDraw()) {
                                float destinyToUse = destinyCard.getDestinyValueToUse();
                                gameState.removeCardFromZone(destinyCard);
                                destinyCard.setDestinyValueToUse(destinyToUse);
                                gameState.stackCard(destinyCard, _stackOn, _faceDown, _asInactive, false);
                                gameState.sendMessage(_action.getPerformingPlayer() + " stacks just drawn destiny card, " + GameUtils.getCardLink(destinyCard) + ", on " + GameUtils.getCardLink(_stackOn));
                            }
                        }
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
