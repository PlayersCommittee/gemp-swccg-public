package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardsFromLostPileEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseStackedCardsEffect;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * An effect that causes the player to exchange a card in lost pile with a stacked card.
 */
public class ExchangeCardFromLostPileWithStackedCardEffect extends AbstractSubActionEffect {
    private final String _playerId;
    private final String _zoneOwner;
    private Filterable _cardInLostPileFilter;
    private Filterable _stackedOnFilter;
    private Filterable _stackedCardFilter;
    private boolean _stackFaceDown;

    /**
     * Creates an effect that causes the player to exchange a card in lost pile accepted by the card in lost pile filter with a card
     * accepted by the stacked card filter that is stacked on a card accepted by the stacked on filter.
     * @param action the action performing this effect
     * @param cardInLostPileFilter the card in lost pile filter
     * @param stackedOnFilter the stacked on filter
     * @param stackedCardFilter the stacked card filter
     */
    public ExchangeCardFromLostPileWithStackedCardEffect(Action action, String zoneOwner, Filterable cardInLostPileFilter, Filterable stackedOnFilter, Filterable stackedCardFilter, boolean stackFaceDown) {
        super(action);
        _playerId = action.getPerformingPlayer();
        _zoneOwner = zoneOwner;
        _cardInLostPileFilter = Filters.and(cardInLostPileFilter);
        _stackedOnFilter = Filters.and(stackedOnFilter);
        _stackedCardFilter = Filters.and(stackedCardFilter);
        _stackFaceDown = stackFaceDown;
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
                new ChooseCardsFromLostPileEffect(subAction, _playerId, _zoneOwner, 1, 1, _cardInLostPileFilter) {
                    @Override
                    protected void cardsSelected(SwccgGame game, Collection<PhysicalCard> selectedCards) {
                        final PhysicalCard cardFromLostPile = selectedCards.iterator().next();

                        subAction.appendEffect(
                                new ChooseStackedCardsEffect(subAction, _playerId, _stackedOnFilter, 1, 1, _stackedCardFilter) {
                                    @Override
                                    protected void cardsSelected(SwccgGame game, Collection<PhysicalCard> selectedStackedCards) {
                                        PhysicalCard stackedCard = selectedStackedCards.iterator().next();
                                        boolean stackedAsInactive = stackedCard.isStackedAsInactive();
                                        PhysicalCard stackedOn = stackedCard.getStackedOn();
                                        Set<PhysicalCard> cardsToRemove = new HashSet<PhysicalCard>();
                                        cardsToRemove.add(cardFromLostPile);
                                        cardsToRemove.add(stackedCard);
                                        gameState.sendMessage(_playerId + " exchanges " + GameUtils.getCardLink(cardFromLostPile) + " from Lost Pile with " + GameUtils.getCardLink(stackedCard) + " stacked on " + GameUtils.getCardLink(stackedOn));
                                        gameState.removeCardsFromZone(cardsToRemove);
                                        gameState.addCardToZone(stackedCard, Zone.LOST_PILE, _zoneOwner);
                                        gameState.stackCard(cardFromLostPile, stackedOn, _stackFaceDown, stackedAsInactive, false);
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
