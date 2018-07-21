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
import com.gempukku.swccgo.logic.effects.choose.TakeCardsIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.LookedAtCardsInOwnCardPileResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * An effect for peeking at the top cards of the Force Pile and choosing to take a specified number of them into hand.
 */
public class PeekAtTopCardsOfForcePileAndChooseCardsToTakeIntoHandEffect extends AbstractSubActionEffect {
    private String _playerId;
    private String _cardPileOwner;
    private Zone _cardPile;
    private int _count;
    private int _minCountIntoHand;
    private int _maxCountIntoHand;

    /**
     * Creates an effect for peeking at the top cards of Reserve Deck and choosing a specified number of them to be lost.
     * @param action the action performing this effect
     * @param cardPileOwner the owner of the Force Pile
     * @param count the number of cards to peek at
     * @param minCountIntoHand the minimum number of cards to take into hand
     * @param maxCountIntoHand the maximum number of cards to take into hand
     */
    public PeekAtTopCardsOfForcePileAndChooseCardsToTakeIntoHandEffect(Action action, String cardPileOwner, int count, int minCountIntoHand, int maxCountIntoHand) {
        super(action);
        _playerId = action.getPerformingPlayer();
        _cardPileOwner = cardPileOwner;
        _cardPile = Zone.FORCE_PILE;
        _count = count;
        _maxCountIntoHand = Math.min(count, maxCountIntoHand);
        _minCountIntoHand = Math.min(_maxCountIntoHand, minCountIntoHand);
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

                            int maxToTakeIntoHand = Math.min(_maxCountIntoHand, topCards.size());
                            int minToTakeIntoHand = Math.min(_minCountIntoHand, maxToTakeIntoHand);

                            if (topCards.size() < _count) {
                                game.getUserFeedback().sendAwaitingDecision(_playerId,
                                        new ArbitraryCardsSelectionDecision("Top card" + GameUtils.s(topCards) + " of " + cardPileText, topCards, Collections.<PhysicalCard>emptyList(), 0, 0) {
                                            @Override
                                            public void decisionMade(String result) throws DecisionResultInvalidException {
                                                // Check if player looked at cards in own card pile
                                                if (_cardPileOwner.equals(_playerId)) {
                                                    subAction.appendAfterEffect(new TriggeringResultEffect(_action, new LookedAtCardsInOwnCardPileResult(_cardPileOwner, _cardPile)));
                                                }
                                            }
                                        });
                            }
                            else {
                                game.getUserFeedback().sendAwaitingDecision(_playerId,
                                        new ArbitraryCardsSelectionDecision("Top card" + GameUtils.s(topCards) + " of " + cardPileText + ". Choose card" + GameUtils.s(maxToTakeIntoHand) + " to take into hand", topCards, topCards, minToTakeIntoHand, maxToTakeIntoHand) {
                                            @Override
                                            public void decisionMade(String result) throws DecisionResultInvalidException {
                                                List<PhysicalCard> selectedCards = getSelectedCardsByResponse(result);
                                                subAction.appendEffect(
                                                        new TakeCardsIntoHandFromReserveDeckEffect(subAction, _playerId, selectedCards, true));
                                                // Check if player looked at cards in own card pile
                                                if (_cardPileOwner.equals(_playerId)) {
                                                    subAction.appendAfterEffect(new TriggeringResultEffect(_action, new LookedAtCardsInOwnCardPileResult(_cardPileOwner, _cardPile)));
                                                }
                                            }
                                        });
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
