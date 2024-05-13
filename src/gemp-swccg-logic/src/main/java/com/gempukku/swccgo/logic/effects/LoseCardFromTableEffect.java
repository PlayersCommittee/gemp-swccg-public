package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;

/**
 * An effect that causes the specified card on table to be lost.
 */
public class LoseCardFromTableEffect extends LoseCardsFromTableEffect {

    /**
     * Creates an effect that causes the specified card on table to be lost.
     * @param action the action performing this effect
     * @param card the card
     */
    public LoseCardFromTableEffect(Action action, PhysicalCard card) {
        this(action, card, false, false);
    }

    /**
     * Creates an effect that causes the specified card on table to be lost.
     * @param action the action performing this effect
     * @param card the card
     * @param allCardsSituation true if this is an "all cards situation", otherwise false
     */
    public LoseCardFromTableEffect(Action action, PhysicalCard card, boolean allCardsSituation) {
        this(action, card, allCardsSituation, false);
    }

    /**
     * Creates an effect that causes the specified card on table to be lost.
     * @param action the action performing this effect
     * @param card the card
     * @param allCardsSituation true if this is an "all cards situation", otherwise false
     * @param toBottomOfPile true if cards are placed on the bottom of the Lost Pile, otherwise false
     */
    public LoseCardFromTableEffect(Action action, PhysicalCard card, boolean allCardsSituation, boolean toBottomOfPile) {
        super(action, Collections.singleton(card), allCardsSituation, toBottomOfPile);
    }
}
