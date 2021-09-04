package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.logic.timing.Action;


/**
 * An effect to place the top card of Used Pile on top of Force Pile.
 */
public class PlaceTopCardOfUsedPileOnTopOfForcePileEffect extends PlaceTopCardFromCardPileOnTopOfCardPileEffect {

    /**
     * Creates an effect to place the top card of Used Pile on top of Force Pile.
     *
     * @param action the action performing this effect
     */
    public PlaceTopCardOfUsedPileOnTopOfForcePileEffect(Action action, String cardPileOwner) {
        super(action, cardPileOwner, Zone.USED_PILE, Zone.FORCE_PILE);
    }
}
