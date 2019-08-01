package com.gempukku.swccgo.cards.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Arrays;
import java.util.Collections;

/**
 * An effect for peeking at the top card of Force Pile and Reserve Deck and Used Pile and choosing card to return to each.
 */
public class PeekAtTopCardOfForcePileAndReserveDeckAndReturnThemToOnePile extends PeekAtTopCardOfCardPilesAndReturnCardsToPilesEffect {

    /**
     * Creates an effect for peeking at the top card of Force Pile and Reserve Deck and Used Pile and choose card to return to each.
     * @param action the action performing this effect
     * @param cardPileOwner the owner of the card piles
     */
    public PeekAtTopCardOfForcePileAndReserveDeckAndReturnThemToOnePile(Action action, String cardPileOwner, Zone targetPile) {
        super(action, cardPileOwner, Arrays.asList(Zone.FORCE_PILE, Zone.RESERVE_DECK), Collections.singletonList(targetPile));
    }
}