package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.ActionsEnvironment;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.effects.choose.ChooseStackedCardsEffect;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.results.ExchangedCardsInCardPileResult;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * An effect that causes the player to exchange a stacked card with the top card of a card pile.
 */
public class ExchangeStackedCardWithTopCardOfCardPileEffect extends AbstractSubActionEffect {
    private final String _playerId;
    private Filterable _stackedOnFilter;
    private Filterable _stackedCardFilter;
    private final Zone _cardPile;
    private ExchangeStackedCardWithTopCardOfCardPileEffect _that;

    /**
     * Creates an effect that causes the player to exchange a stacked card filter that is stacked on a card accepted by
     * the stacked on filter with the top card of the specified card pile.
     * @param action the action performing this effect
     * @param stackedOnFilter the stacked on filter
     * @param stackedCardFilter the stacked card filter
     * @param cardPile the card pile
     */
    protected ExchangeStackedCardWithTopCardOfCardPileEffect(Action action, Filterable stackedOnFilter, Filterable stackedCardFilter, Zone cardPile) {
        super(action);
        _playerId = action.getPerformingPlayer();
        _stackedOnFilter = Filters.and(stackedOnFilter);
        _stackedCardFilter = Filters.and(stackedCardFilter);
        _cardPile = cardPile;
        _that = this;
    }

    /**
     * Determines whether selection are automatically made is number of cards to select is the same as the minimum number to choose
     */
    protected boolean getUseShortcut() {
        return false;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        final GameState gameState = game.getGameState();
        final ActionsEnvironment actionsEnvironment = game.getActionsEnvironment();

        final SubAction subAction = new SubAction(_action);
        subAction.appendEffect(
                new ChooseStackedCardsEffect(subAction, _playerId, _stackedOnFilter, 1, 1, _stackedCardFilter) {
                    @Override
                    protected boolean getUseShortcut() {
                        return _that.getUseShortcut();
                    }
                    @Override
                    protected void cardsSelected(SwccgGame game, Collection<PhysicalCard> selectedStackedCards) {
                        final PhysicalCard topCardOfCardPile = gameState.getTopOfCardPile(_playerId, _cardPile);
                        if (topCardOfCardPile != null) {
                            PhysicalCard stackedCard = selectedStackedCards.iterator().next();
                            boolean stackedAsInactive = stackedCard.isStackedAsInactive();
                            PhysicalCard stackedOn = stackedCard.getStackedOn();
                            Set<PhysicalCard> cardsToRemove = new HashSet<PhysicalCard>();
                            cardsToRemove.add(topCardOfCardPile);
                            cardsToRemove.add(stackedCard);
                            gameState.sendMessage(_playerId + " exchanges " + GameUtils.getCardLink(stackedCard) + " stacked on " + GameUtils.getCardLink(stackedOn) + " with " + GameUtils.getCardLink(topCardOfCardPile) + " from top of " + _cardPile.getHumanReadable());
                            gameState.removeCardsFromZone(cardsToRemove);
                            gameState.addCardToTopOfZone(stackedCard, _cardPile, _playerId);
                            gameState.stackCard(topCardOfCardPile, stackedOn, false, stackedAsInactive, false);

                            actionsEnvironment.emitEffectResult(
                                    new ExchangedCardsInCardPileResult(subAction));
                        }
                    }
                });
        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }
}
