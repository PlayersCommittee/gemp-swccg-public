package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect to place Force Pile on top of Used Pile.
 */
public class PlaceForcePileOnUsedPileEffect extends PlaceCardPileOnTopOfCardPileEffect {

    /**
     * Creates an effect to place Force Pile on top of Used Pile.
     * @param action the action performing this effect
     * @param cardPileOwner the owner of the card piles
     */
    public PlaceForcePileOnUsedPileEffect(Action action, String cardPileOwner) {
        super(action, cardPileOwner, Zone.FORCE_PILE, Zone.USED_PILE);
    }
}
