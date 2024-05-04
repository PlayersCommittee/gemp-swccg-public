package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that causes the specified card on table to be placed on bottom of Used Pile.
 */
public class PlaceCardOnBottomOfUsedPileFromTableEffect extends PlaceCardInUsedPileFromTableEffect {

    /**
     * Creates an effect that causes the specified card on table to be placed on bottom of Used Pile.
     * @param action the action performing this effect
     * @param card the card
     */
    public PlaceCardOnBottomOfUsedPileFromTableEffect(Action action, PhysicalCard card) {
        super(action, card, true);
    }
}
