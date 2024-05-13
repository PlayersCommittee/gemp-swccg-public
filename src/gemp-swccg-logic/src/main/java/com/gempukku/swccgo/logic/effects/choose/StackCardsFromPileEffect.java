package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.StackedFromCardPileResult;

import java.util.Collection;

/**
 * An effect that causes the player performing the action to stack a card from the specified card pile.
 */
public class StackCardsFromPileEffect extends AbstractSubActionEffect {
    private String _playerId;
    private PhysicalCard _stackOn;
    private Zone _cardPile;
    private Collection<PhysicalCard> _cardsToStack;
    private boolean _faceDown;
    private boolean cardsStacked;

    /**
     * Creates an effect that causes the specified player to stack the specified cards from the specified card pile
     * on a specified card.
     * @param action the action performing this effect
     * @param playerId the player to stack the card
     * @param zone the card pile
     * @param cardsToStack the cards to stack
     * @param stackOn the card to stack a card on
     * @param faceDown true if face down, otherwise face up
     */
    public StackCardsFromPileEffect(Action action, String playerId, Zone zone, Collection<PhysicalCard> cardsToStack, PhysicalCard stackOn, boolean faceDown) {
        super(action);
        _playerId = playerId;
        _stackOn = stackOn;
        _cardPile = zone;
        _cardsToStack = cardsToStack;
        _faceDown = faceDown;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        final GameState gameState = game.getGameState();

        final SubAction subAction = new SubAction(_action, _playerId);
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        Collection<PhysicalCard> cardsToStack = Filters.filter(_cardsToStack, game, Filters.zoneOfPlayer(_cardPile, _playerId));
                        if (cardsToStack.isEmpty())
                            return;

                        // If hidden is specified, then check if card pile is actually face up and update value of hidden
                        boolean hidden = _faceDown;
                        if (hidden) {
                            hidden = !gameState.isCardPileFaceUp(_playerId, _cardPile);
                        }
                        String cardInfo = hidden ? (GameUtils.numCards(cardsToStack) + " card" + GameUtils.s(cardsToStack)) : GameUtils.getAppendedNames(cardsToStack);
                        String facing = _faceDown ? " face down" : "";

                        gameState.sendMessage(_playerId + " stacks " + cardInfo + " from " + _cardPile.getHumanReadable() + facing + " on " + GameUtils.getCardLink(_stackOn));
                        gameState.removeCardsFromZone(cardsToStack);
                        for (PhysicalCard cardToStack : cardsToStack) {
                            gameState.stackCard(cardToStack, _stackOn, _faceDown, false, false);
                            game.getActionsEnvironment().emitEffectResult(new StackedFromCardPileResult(_action, cardToStack, _stackOn));
                        }

                        cardsStacked = true;
                    }
                }
        );
        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return cardsStacked;
    }
}
