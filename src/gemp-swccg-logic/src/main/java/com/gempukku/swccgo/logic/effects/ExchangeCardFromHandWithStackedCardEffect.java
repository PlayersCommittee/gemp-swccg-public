package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardsFromHandEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseStackedCardsEffect;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * An effect that causes the player to exchange a card in hand with a stacked card.
 */
public class ExchangeCardFromHandWithStackedCardEffect extends AbstractSubActionEffect {
    private final String _playerId;
    private Filterable _cardInHandFilter;
    private Filterable _stackedOnFilter;
    private Filterable _stackedCardFilter;

    /**
     * Creates an effect that causes the player to exchange a card in hand accepted by the card in hand filter with a card
     * accepted by the stacked card filter that is stacked on a card accepted by the stacked on filter.
     * @param action the action performing this effect
     * @param cardInHandFilter the card in hand filter
     * @param stackedOnFilter the stacked on filter
     * @param stackedCardFilter the stacked card filter
     */
    public ExchangeCardFromHandWithStackedCardEffect(Action action, Filterable cardInHandFilter, Filterable stackedOnFilter, Filterable stackedCardFilter) {
        super(action);
        _playerId = action.getPerformingPlayer();
        _cardInHandFilter = Filters.and(cardInHandFilter);
        _stackedOnFilter = Filters.and(stackedOnFilter);
        _stackedCardFilter = Filters.and(stackedCardFilter);
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
                new ChooseCardsFromHandEffect(subAction, _playerId, 1, 1, _cardInHandFilter) {
                    @Override
                    protected void cardsSelected(SwccgGame game, Collection<PhysicalCard> selectedCards) {
                        final PhysicalCard cardFromHand = selectedCards.iterator().next();

                        subAction.appendEffect(
                                new ChooseStackedCardsEffect(subAction, _playerId, _stackedOnFilter, 1, 1, _stackedCardFilter) {
                                    @Override
                                    protected void cardsSelected(SwccgGame game, Collection<PhysicalCard> selectedStackedCards) {
                                        PhysicalCard stackedCard = selectedStackedCards.iterator().next();
                                        boolean stackedAsInactive = stackedCard.isStackedAsInactive();
                                        PhysicalCard stackedOn = stackedCard.getStackedOn();
                                        Set<PhysicalCard> cardsToRemove = new HashSet<PhysicalCard>();
                                        cardsToRemove.add(cardFromHand);
                                        cardsToRemove.add(stackedCard);
                                        gameState.sendMessage(_playerId + " exchanges " + GameUtils.getCardLink(cardFromHand) + " from hand with " + GameUtils.getCardLink(stackedCard) + " stacked on " + GameUtils.getCardLink(stackedOn));
                                        gameState.removeCardsFromZone(cardsToRemove);
                                        gameState.addCardToZone(stackedCard, Zone.HAND, _playerId);
                                        gameState.stackCard(cardFromHand, stackedOn, false, stackedAsInactive, false);
                                    }
                                });
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
