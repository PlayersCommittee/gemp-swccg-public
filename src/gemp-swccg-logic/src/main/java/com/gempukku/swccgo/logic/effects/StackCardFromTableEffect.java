package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;

/**
 * An effect that causes the specified cards on table to be stacked on a card.
 */
public class StackCardFromTableEffect extends StackCardsFromTableEffect {

    /**
     * Creates an effect that causes the specified card on table to be stacked on a card.
     * @param action the action performing this effect
     * @param card the card
     * @param stackOn the card to stack on
     */
    public StackCardFromTableEffect(Action action, PhysicalCard card, PhysicalCard stackOn) {
        this(action, card, stackOn, false);
    }

    /**
     * Creates an effect that causes the specified card on table to be stacked on a card.
     * @param action the action performing this effect
     * @param card the card
     * @param stackOn the card to stack on
     * @param faceDown true if cards are stacked face down, otherwise false
     */
    public StackCardFromTableEffect(Action action, PhysicalCard card, PhysicalCard stackOn, boolean faceDown) {
        super(action, Collections.singleton(card), stackOn, faceDown);
    }
}
