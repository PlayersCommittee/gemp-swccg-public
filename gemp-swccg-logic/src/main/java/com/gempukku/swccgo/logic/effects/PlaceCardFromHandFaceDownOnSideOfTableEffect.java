package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardsFromHandEffect;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.StandardEffect;

import java.util.Collection;

/**
 * An effect to place a card from hand face down on side of table.
 */
public class PlaceCardFromHandFaceDownOnSideOfTableEffect extends AbstractSubActionEffect {
    private String _playerId;
    private PlaceCardFromHandFaceDownOnSideOfTableEffect _that;

    /**
     * Creates an effect that causes the player to put all cards from hand into the specified card pile.
     * @param action the action performing this effect
     * @param playerId the player
     */
    protected PlaceCardFromHandFaceDownOnSideOfTableEffect(Action action, String playerId) {
        super(action);
        _playerId = playerId;
        _that = this;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return !game.getGameState().getHand(_playerId).isEmpty();
    }

    @Override
    protected SubAction getSubAction(final SwccgGame game) {
        SubAction subAction = new SubAction(_action);
        subAction.appendEffect(getChooseOneCardToPutInCardPileEffect(subAction));
        return subAction;
    }

    public String getChoiceText(int numCardsToChoose) {
        return "Choose card" + GameUtils.s(numCardsToChoose) + " to place face down on side of table";
    }

    private StandardEffect getChooseOneCardToPutInCardPileEffect(final SubAction subAction) {
        return new ChooseCardsFromHandEffect(subAction, _playerId, _playerId, 1, 1, Filters.any, false, false) {
            @Override
            public String getChoiceText(int numCardsToChoose) {
                return _that.getChoiceText(1);
            }
            @Override
            protected void cardsSelected(final SwccgGame game, Collection<PhysicalCard> cards) {
                GameState gameState = game.getGameState();
                if (!cards.isEmpty()) {
                    PhysicalCard card = cards.iterator().next();
                    gameState.sendMessage(_playerId + " places a card from hand face down on side of table");
                    gameState.removeCardFromZone(card);
                    card.setFlipped(true);
                    gameState.addCardToZone(card, Zone.SIDE_OF_TABLE_FACE_DOWN_NOT_IN_PLAY, _playerId);

                    cardPlacedFaceDownOnSideOfTable(card);
                }
            }
        };
    }

    /**
     * A callback method for the card placed face down on side of table.
     * @param card the card placed face down on side of table
     */
    protected void cardPlacedFaceDownOnSideOfTable(PhysicalCard card) {
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }
}
