package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect to put cards from Reserve Deck on bottom of Used Pile.
 */
public class PutCardsFromReserveDeckOnBottomOfUsedPileEffect extends PutCardsFromCardPileInCardPileEffect {

    /**
     * Creates an effect that causes the player to put cards from Reserve Deck on bottom of Used Pile.
     * @param action the action performing this effect
     * @param playerId the player
     */
    public PutCardsFromReserveDeckOnBottomOfUsedPileEffect(Action action, String playerId, String cardPileOwner, Filterable filters) {
        super(action, playerId, cardPileOwner, Zone.RESERVE_DECK, Zone.USED_PILE, true, filters);
    }
}
