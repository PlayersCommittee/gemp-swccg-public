package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.DrawDestinyState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that causes the player performing the action to place the just drawn destiny card on top of a specified card pile.
 */
class PlaceDestinyCardOnTopOfCardPileEffect extends AbstractSubActionEffect {
    private Zone _cardPile;

    /**
     * Creates an effect that causes the player performing the action to place the just drawn destiny card on top of a specified card pile.
     * @param action the action performing this effect
     * @param cardPile the card pile
     */
    protected PlaceDestinyCardOnTopOfCardPileEffect(Action action, Zone cardPile) {
        super(action);
        _cardPile = cardPile;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        final SubAction subAction = new SubAction(_action);

        DrawDestinyState drawDestinyState = game.getGameState().getTopDrawDestinyState();
        if (drawDestinyState != null) {
            PhysicalCard destinyCard = drawDestinyState.getDrawDestinyEffect().getDrawnDestinyCard();
            if (destinyCard != null
                    && destinyCard.getZone().isUnresolvedDestinyDraw()) {

                String msgText = GameUtils.getCardLink(_action.getActionSource()) + " causes just drawn destiny card, " + GameUtils.getCardLink(destinyCard) + ", to be placed on top of " + _cardPile.getHumanReadable();
                subAction.appendEffect(
                        new PutOneCardFromCardPileInCardPileEffect(subAction, destinyCard, destinyCard.getZone(), _cardPile, destinyCard.getOwner(), false, msgText) {
                            @Override
                            protected void scheduleNextStep() {
                            }
                        });
            }
        }

        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }
}
