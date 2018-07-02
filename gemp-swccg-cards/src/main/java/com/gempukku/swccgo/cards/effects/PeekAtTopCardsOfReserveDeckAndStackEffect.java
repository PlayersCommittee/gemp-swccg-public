package com.gempukku.swccgo.cards.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.decisions.ArbitraryCardsSelectionDecision;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.effects.TriggeringResultEffect;
import com.gempukku.swccgo.logic.effects.choose.StackCardsFromPileEffect;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.LookedAtCardsInOwnCardPileResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * An effect for peeking at the top cards of Reserve Deck and stacking them on a specified card.
 */
public class PeekAtTopCardsOfReserveDeckAndStackEffect extends AbstractSubActionEffect {
    private String _playerId;
    private String _cardPileOwner;
    private Zone _cardPile;
    private int _count;
    private PhysicalCard _stackOn;

    /**
     * Creates an effect for peeking at the top cards of Reserve Deck and stacking them on a specified card.
     * @param action the action performing this effect
     * @param cardPileOwner the owner of the Reserve Deck
     * @param count the number of cards to peek at
     * @param stackOn the card to stack the cards on
     */
    public PeekAtTopCardsOfReserveDeckAndStackEffect(Action action, String cardPileOwner, int count, PhysicalCard stackOn) {
        super(action);
        _playerId = action.getPerformingPlayer();
        _cardPileOwner = cardPileOwner;
        _cardPile = Zone.RESERVE_DECK;
        _count = count;
        _stackOn = stackOn;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        final GameState gameState = game.getGameState();

        final SubAction subAction = new SubAction(_action);
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        List<PhysicalCard> deck = gameState.getCardPile(_cardPileOwner, _cardPile);
                        int count = Math.min(deck.size(), _count);
                        final List<PhysicalCard> topCards = new LinkedList<PhysicalCard>(deck.subList(0, count));
                        if (!topCards.isEmpty()) {

                            String cardPileText = (_playerId.equals(_cardPileOwner) ? "" : (_cardPileOwner + "'s ")) + _cardPile.getHumanReadable();
                            if (topCards.size() == 1)
                                gameState.sendMessage(_playerId + " peeks at the top card of " + cardPileText);
                            else
                                gameState.sendMessage(_playerId + " peeks at the top " + topCards.size() + " cards of " + cardPileText);

                            game.getUserFeedback().sendAwaitingDecision(_playerId,
                                    new ArbitraryCardsSelectionDecision("Top card" + GameUtils.s(topCards) + " of " + cardPileText, topCards, Collections.<PhysicalCard>emptyList(), 0, 0) {
                                        @Override
                                        public void decisionMade(String result) throws DecisionResultInvalidException {
                                            if (topCards.size() >= _count) {
                                                subAction.appendEffect(
                                                        new StackCardsFromPileEffect(subAction, _playerId, Zone.RESERVE_DECK, topCards, _stackOn, false));
                                            }
                                            // Check if player looked at cards in own card pile
                                            if (_cardPileOwner.equals(_playerId)) {
                                                subAction.appendEffect(new TriggeringResultEffect(subAction, new LookedAtCardsInOwnCardPileResult(_cardPileOwner, _cardPile)));
                                            }
                                        }
                                    }
                            );
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
