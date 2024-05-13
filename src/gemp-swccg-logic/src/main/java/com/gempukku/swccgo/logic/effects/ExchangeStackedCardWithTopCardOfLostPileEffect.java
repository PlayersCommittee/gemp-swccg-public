package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that causes the player to exchange a stacked card with the top card of a card pile.
 */
public class ExchangeStackedCardWithTopCardOfLostPileEffect extends ExchangeStackedCardWithTopCardOfCardPileEffect {

    /**
     * Creates an effect that causes the player to exchange a stacked accepted by the stacked card filter with the top card
     * of Lost Pile.
     * @param action the action performing this effect
     * @param stackedCardFilter the stacked card filter
     */
    public ExchangeStackedCardWithTopCardOfLostPileEffect(Action action, Filterable stackedCardFilter) {
        super(action, Filters.any, stackedCardFilter, Zone.LOST_PILE);
    }

    /**
     * Creates an effect that causes the player to exchange a stacked accepted by the stacked card filter with the top card
     * of Lost Pile.
     * @param action the action performing this effect
     * @param stackedCardFilter the stacked card filter
     * @param isRaceDestiny true if the stacked card is a race destiny, false if not
     */
    public ExchangeStackedCardWithTopCardOfLostPileEffect(Action action, Filterable stackedCardFilter, boolean isRaceDestiny) {
        super(action, Filters.any, stackedCardFilter, Zone.LOST_PILE, isRaceDestiny);
    }
}
