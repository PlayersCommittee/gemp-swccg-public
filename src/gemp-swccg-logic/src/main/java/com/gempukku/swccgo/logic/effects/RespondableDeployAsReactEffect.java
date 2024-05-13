package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.StandardEffect;

/**
 * An interface to define the methods that deploying as 'react' effects need to implement.
 */
public interface RespondableDeployAsReactEffect extends StandardEffect {

    /**
     * Gets the card that is deploying as a 'react'.
     * @return the card
     */
    PhysicalCard getCard1();

    /**
     * Gets the zone that the card is deploying from as a 'react'.
     * @return the zone
     */
    Zone getFromZone1();

    /**
     * Gets the card that the card deploying as a 'react' was stacked on, or null.
     * @return the card stacked on, or null
     */
    PhysicalCard getFromStackedOn1();

    /**
     * Gets the card that is deploying simultaneously with the card that is deploying as a 'react', or null.
     * @return the card, or null
     */
    PhysicalCard getCard2();

    /**
     * Gets the zone that the simultaneously deploying card is deploying from.
     * @return the zone
     */
    Zone getFromZone2();

    /**
     * Gets the card that the simultaneously deploying card was stacked on, or null.
     * @return the card stacked on, or null
     */
    PhysicalCard getFromStackedOn2();
}
