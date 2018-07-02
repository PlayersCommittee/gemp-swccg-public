package com.gempukku.swccgo.cards.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;

/**
 * An effect for peeking at a specified card (face down on table or stacked).
 */
public class PeekAtCardEffect extends PeekAtCardsEffect {

    /**
     * Creates an effect for peeking at a specified card (face down on table or stacked).
     * @param action the action performing this effect
     * @param playerId the player to peek
     * @param card the card to peek at
     */
    public PeekAtCardEffect(Action action, String playerId, PhysicalCard card) {
        super(action, playerId, Collections.singleton(card));
    }
}
