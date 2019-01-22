package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.List;
import java.util.Set;


/**
 * An effect to exchange a card from hand with a card from Lost Pile.
 */
public class ExchangeCardInHandWithCardInLostPileEffect extends ExchangeCardsInHandWithCardInCardPileEffect {

    /**
     * Creates an effect to exchange a card from hand with a card from Lost Pile.
     * @param action the action performing this effect
     * @param playerId the player
     */
    public ExchangeCardInHandWithCardInLostPileEffect(Action action, String playerId) {
        super(action, playerId, Zone.LOST_PILE, 1, 1, false);
    }

    /**
     * Creates an effect to exchange a card from hand with a card from Lost Pile.
     * @param action the action performing this effect
     * @param playerId the player
     * @param cardInHandFilter the card in hand filter
     * @param cardInPileFilter the card in pile filter
     */
    public ExchangeCardInHandWithCardInLostPileEffect(Action action, String playerId, Filterable cardInHandFilter, Filterable cardInPileFilter) {
        super(action, playerId, Zone.LOST_PILE, 1, 1, cardInHandFilter, cardInPileFilter, false);
    }

    public static List<OptionalGameTextTriggerAction> exchangeCardInHandWIthCardOfSameTypeInLostPile(final String playerId, final OptionalGameTextTriggerAction action) {
        action.appendEffect(new ChooseCardFromHandEffect(action, playerId, Filters.any) {
            @Override
            public String getChoiceText(int numCardsToChoose) {
                return "Choose card to exchange";
            }
            @Override
            protected void cardSelected(SwccgGame game, final PhysicalCard cardInHandSelected) {
                Set<CardType> cardInLostPile = cardInHandSelected.getBlueprint().getCardTypes();
                Filter filterForCardInLostPile = Filters.none;
                for (CardType cardType : cardInLostPile) {
                    filterForCardInLostPile = Filters.or(filterForCardInLostPile, Filters.type(cardType));
                }
                action.appendEffect(new ExchangeCardInHandWithCardInLostPileEffect(action, playerId, cardInHandSelected, filterForCardInLostPile));
            }
        });
        return Collections.singletonList(action);
    }
}
