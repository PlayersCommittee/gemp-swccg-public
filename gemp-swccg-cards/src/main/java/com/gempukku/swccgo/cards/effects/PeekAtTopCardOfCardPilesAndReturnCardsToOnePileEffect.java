package com.gempukku.swccgo.cards.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.decisions.ArbitraryCardsSelectionDecision;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.PutOneCardFromCardPileInCardPileEffect;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.*;

/**
 * An effect for peeking at the top card of card piles and choosing one pile to return them to.
 */
class PeekAtTopCardOfCardPilesAndReturnCardsToOnePileEffect extends AbstractSubActionEffect {
    private String _playerId;
    private String _cardPileOwner;
    private List<Zone> _cardPiles;
    private Zone _targetZone;

    /**
     * Creates an effect for peeking at the top cards of card piles and choosing a pile to return them to.
     * @param action the action performing this effect
     * @param cardPileOwner the owner of the card piles
     * @param cardPiles the card piles
     */
    protected PeekAtTopCardOfCardPilesAndReturnCardsToOnePileEffect(Action action, String cardPileOwner, List<Zone> cardPiles) {
        super(action);
        _playerId = action.getPerformingPlayer();
        _cardPileOwner = cardPileOwner;
        _cardPiles = cardPiles;
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
                    protected void doPlayEffect(final SwccgGame game) {
                        final Map<PhysicalCard, String> topCardsMap = new HashMap<PhysicalCard, String>();
                        final List<PhysicalCard> topCards = new ArrayList<PhysicalCard>();
                        final String[] cardPileChoices = new String[_cardPiles.size()];
                        StringBuilder cardPileText = new StringBuilder(_playerId.equals(_cardPileOwner) ? "" : (_cardPileOwner + "'s "));

                        for (int i=0; i<_cardPiles.size(); ++i) {
                            Zone cardPile = _cardPiles.get(i);
                            String cardPileName = cardPile.getHumanReadable();
                            cardPileChoices[i] = cardPileName;

                            PhysicalCard topCard = gameState.getTopOfCardPile(_cardPileOwner, cardPile);
                            if (topCard != null) {
                                topCardsMap.put(topCard, cardPileName);
                                topCards.add(topCard);

                                if (i == 0) {
                                    cardPileText.append(cardPileName);
                                }
                                else if (i == _cardPiles.size()-1) {
                                    if (_cardPiles.size() > 2) {
                                        cardPileText.append(",");
                                    }
                                    cardPileText.append(" and ").append(cardPileName);
                                }
                                else {
                                    cardPileText.append(", ").append(cardPileName);
                                }
                            }
                        }

                        if (!topCards.isEmpty()) {
                            game.getUserFeedback().sendAwaitingDecision(_playerId,
                                    new ArbitraryCardsSelectionDecision(_playerId + " peeks at the top card of " + cardPileText, topCards, Collections.<PhysicalCard>emptyList(), 0, 0) {
                                        @Override
                                        public void decisionMade(String result) {
                                            gameState.cardAffectsCards(_playerId, _action.getActionSource(), topCards);
                                        }
                                    });
                            subAction.appendEffect(
                                    new PlayoutDecisionEffect(_action, _playerId,
                                            new MultipleChoiceAwaitingDecision("Choose pile to return cards to:", cardPileChoices) {
                                                @Override
                                                protected void validDecisionMade(int index, String result) {
                                                    _targetZone = _cardPiles.get(index);
                                                    chooseNextCardToPlaceInPile(subAction, game, topCardsMap);
                                                }
                                            })
                            );

                        }
                    }
                }
        );
        return subAction;
    }


    private void chooseNextCardToPlaceInPile(final SubAction subAction, final SwccgGame game, final Map<PhysicalCard, String> topCardsMap) {
        final StringBuilder cardPileOwnerText = new StringBuilder(_playerId.equals(_cardPileOwner) ? "" : (_cardPileOwner + "'s "));
        Set<PhysicalCard> cardsToPlace = topCardsMap.keySet();

        if (cardsToPlace.size() > 1) {
            StringBuilder cardPileText = new StringBuilder();
            List<String> cardPileNames = new ArrayList<String>(topCardsMap.values());
            for (int i = 0; i < cardPileNames.size(); ++i) {
                String cardPileName = cardPileNames.get(i);
                if (i == 0) {
                    cardPileText.append(cardPileName);
                } else if (i == _cardPiles.size() - 1) {
                    if (_cardPiles.size() > 2) {
                        cardPileText.append(",");
                    }
                    cardPileText.append(" and ").append(cardPileName);
                } else {
                    cardPileText.append(", ").append(cardPileName);
                }
            }

            game.getUserFeedback().sendAwaitingDecision(_playerId,
                    new ArbitraryCardsSelectionDecision("Top card" + GameUtils.s(cardsToPlace) + " of " + cardPileOwnerText + cardPileText + ". Choose card to place in " + cardPileOwnerText + _targetZone.getHumanReadable(), cardsToPlace, cardsToPlace, 1, 1) {
                        @Override
                        public void decisionMade(String result) throws DecisionResultInvalidException {
                            final PhysicalCard cardToPlaceInCardPile = getSelectedCardsByResponse(result).get(0);
                            Zone fromCardPile = GameUtils.getZoneFromZoneTop(cardToPlaceInCardPile.getZone());

                            String msgText = _playerId + " places top card of " + cardPileOwnerText + cardToPlaceInCardPile.getZone().getHumanReadable() + " on " + cardPileOwnerText + _targetZone.getHumanReadable();
                            subAction.appendEffect(
                                    new PutOneCardFromCardPileInCardPileEffect(subAction, cardToPlaceInCardPile, fromCardPile, _targetZone, _cardPileOwner, false, msgText) {
                                        @Override
                                        protected void scheduleNextStep() {
                                            topCardsMap.remove(cardToPlaceInCardPile);
                                            if (!topCardsMap.keySet().isEmpty()) {
                                                chooseNextCardToPlaceInPile(subAction, game, topCardsMap);
                                            }
                                        }
                                    }
                            );
                        }
                    }
            );
        }
        else if (!cardsToPlace.isEmpty()) {
            final PhysicalCard cardToPlaceInCardPile = cardsToPlace.iterator().next();
            Zone fromCardPile = GameUtils.getZoneFromZoneTop(cardToPlaceInCardPile.getZone());

            String msgText = _playerId + " places top card of " + cardPileOwnerText + cardToPlaceInCardPile.getZone().getHumanReadable() + " on " + cardPileOwnerText + _targetZone.getHumanReadable();
            subAction.appendEffect(
                    new PutOneCardFromCardPileInCardPileEffect(subAction, cardToPlaceInCardPile, fromCardPile, _targetZone, _cardPileOwner, false, msgText) {
                        @Override
                        protected void scheduleNextStep() {
                            topCardsMap.remove(cardToPlaceInCardPile);
                            if (!topCardsMap.keySet().isEmpty()) {
                                chooseNextCardToPlaceInPile(subAction, game, topCardsMap);
                            }
                        }
                    }
            );
        }
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }
}