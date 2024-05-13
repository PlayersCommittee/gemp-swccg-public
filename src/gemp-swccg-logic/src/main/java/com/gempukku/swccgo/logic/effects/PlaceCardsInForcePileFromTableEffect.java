package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;

/**
 * An effect that causes the specified cards on table to be placed in Force Pile.
 */
public class PlaceCardsInForcePileFromTableEffect extends PlaceCardsInCardPileFromTableEffect {

    /**
     * Creates an effect that causes the specified cards on table to be placed in Force Pile.
     * @param action the action performing this effect
     * @param cards the cards
     */
    public PlaceCardsInForcePileFromTableEffect(Action action, Collection<PhysicalCard> cards) {
        this(action, cards, false, Zone.LOST_PILE);
    }

    /**
     * Creates an effect that causes the specified cards on table to be placed in Force Pile.
     * @param action the action performing this effect
     * @param cards the cards
     * @param toBottomOfPile true if cards are placed on the bottom of the card pile, otherwise false
     */
    public PlaceCardsInForcePileFromTableEffect(Action action, Collection<PhysicalCard> cards, boolean toBottomOfPile) {
        this(action, cards, toBottomOfPile, Zone.LOST_PILE);
    }

    /**
     * Creates an effect that causes the specified cards on table to be placed in Force Pile.
     * @param action the action performing this effect
     * @param cards the cards
     * @param toBottomOfPile true if cards are placed on the bottom of the card pile, otherwise false
     * @param attachedCardsGoToZone the zone that any attached cards go to (instead of Lost Pile)
     */
    public PlaceCardsInForcePileFromTableEffect(Action action, Collection<PhysicalCard> cards, boolean toBottomOfPile, Zone attachedCardsGoToZone) {
        super(action, action.getPerformingPlayer(), cards, Zone.FORCE_PILE, toBottomOfPile, attachedCardsGoToZone);
    }
}
