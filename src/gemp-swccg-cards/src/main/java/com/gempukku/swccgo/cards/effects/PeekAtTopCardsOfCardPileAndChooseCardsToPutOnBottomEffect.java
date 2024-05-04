package com.gempukku.swccgo.cards.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.decisions.ArbitraryCardsSelectionDecision;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.effects.PutCardFromCardPileOnBottomOfCardPileEffect;
import com.gempukku.swccgo.logic.effects.TriggeringResultEffect;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.LookedAtCardsInCardPileResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * An effect for peeking at the top cards of a deck or pile and choosing a specified number to put to bottom.
 */
public class PeekAtTopCardsOfCardPileAndChooseCardsToPutOnBottomEffect extends AbstractSubActionEffect {
    private String _playerId;
    private String _cardPileOwner;
    private Zone _cardPile;
    private int _count;
    private int _minCountToBottom;
    private int _maxCountToBottom;

    /**
     * Creates an effect for peeking at the top cards of a deck or pile and choosing a specified number of them to be put to bottom.
     *
     * @param action           the action performing this effect
     * @param cardPile         the deck or pile
     * @param cardPileOwner    the owner of the deck or pile
     * @param count            the number of cards to peek at
     * @param minCountToBottom the minimum number of cards to put to bottom
     * @param maxCountToBottom the maximum number of cards to put to bottom
     */
    public PeekAtTopCardsOfCardPileAndChooseCardsToPutOnBottomEffect(Action action, Zone cardPile, String cardPileOwner, int count, int minCountToBottom, int maxCountToBottom) {
        super(action);
        _playerId = action.getPerformingPlayer();
        _cardPileOwner = cardPileOwner;
        _cardPile = cardPile;
        _count = count;
        _maxCountToBottom = Math.min(count, maxCountToBottom);
        _minCountToBottom = Math.min(maxCountToBottom, minCountToBottom);
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

                            int maxToTakeIntoHand = Math.min(_maxCountToBottom, topCards.size());
                            int minToTakeIntoHand = Math.min(_minCountToBottom, maxToTakeIntoHand);

                            if (topCards.size() < _count) {
                                game.getUserFeedback().sendAwaitingDecision(_playerId,
                                        new ArbitraryCardsSelectionDecision("Top card" + GameUtils.s(topCards) + " of " + cardPileText, topCards, Collections.<PhysicalCard>emptyList(), 0, 0) {
                                            @Override
                                            public void decisionMade(String result) throws DecisionResultInvalidException {
                                                    subAction.appendAfterEffect(new TriggeringResultEffect(_action, new LookedAtCardsInCardPileResult(_playerId, _cardPileOwner, _cardPile, _action.getActionSource())));
                                            }
                                        });
                            } else {
                                game.getUserFeedback().sendAwaitingDecision(_playerId,
                                        new ArbitraryCardsSelectionDecision("Top card" + GameUtils.s(topCards) + " of " + cardPileText + ". Choose card" + GameUtils.s(maxToTakeIntoHand) + " to put on bottom of "+_cardPile.getHumanReadable(), topCards, topCards, minToTakeIntoHand, maxToTakeIntoHand) {
                                            @Override
                                            public void decisionMade(String result) throws DecisionResultInvalidException {
                                                List<PhysicalCard> selectedCards = getSelectedCardsByResponse(result);
                                                for (PhysicalCard card : selectedCards) {
                                                    subAction.appendEffect(
                                                            new PutCardFromCardPileOnBottomOfCardPileEffect(subAction, _cardPileOwner, card, _cardPile,true));
                                                }

                                                subAction.appendAfterEffect(new TriggeringResultEffect(_action, new LookedAtCardsInCardPileResult(_playerId, _cardPileOwner, _cardPile, _action.getActionSource())));
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
