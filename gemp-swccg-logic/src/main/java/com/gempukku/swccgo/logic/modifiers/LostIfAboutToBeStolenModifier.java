package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;

/**
 * A modifier that cause the card to be lost anytime it is about to be stolen.
 */
public class LostIfAboutToBeStolenModifier extends AbstractModifier {

    /**
     * Creates a modifier that prevents cards accepted by the filter from being 'Disarmed'.
     * @param source the card that is the source of the modifier and that may not be 'Disarmed'
     */
    public LostIfAboutToBeStolenModifier(PhysicalCard source) {
        this(source, source);
    }

    /**
     * Creates a modifier that prevents cards accepted by the filter from being 'Disarmed'.
     * @param source the source of the modifier
     * @param affectFilter the filter
     */
    public LostIfAboutToBeStolenModifier(PhysicalCard source, Filterable affectFilter) {
        super(source, "Lost if about to be stolen", affectFilter, ModifierType.LOST_IF_ABOUT_TO_BE_STOLEN);
    }
}
