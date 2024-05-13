package com.gempukku.swccgo.cards.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Arrays;

/**
 * An effect for peeking at the top card of Force Pile and Reserve Deck and Used Pile and choosing card to return to each.
 */
public class PeekAtTopCardOfForcePileAndReserveDeckAndUsedPileAndReturnOneCardToEachEffect extends PeekAtTopCardOfCardPilesAndReturnOneCardToEachEffect {

    /**
     * Creates an effect for peeking at the top card of Force Pile and Reserve Deck and Used Pile and choose card to return to each.
     * @param action the action performing this effect
     * @param cardPileOwner the owner of the card piles
     */
    public PeekAtTopCardOfForcePileAndReserveDeckAndUsedPileAndReturnOneCardToEachEffect(Action action, String cardPileOwner) {
        super(action, cardPileOwner, Arrays.asList(Zone.FORCE_PILE, Zone.RESERVE_DECK, Zone.USED_PILE));
    }
}