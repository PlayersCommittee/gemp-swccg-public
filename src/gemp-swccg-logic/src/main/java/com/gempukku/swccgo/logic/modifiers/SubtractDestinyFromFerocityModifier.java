package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;

/**
 * A modifier that causes a destiny to be drawn and subtracted from ferocity
 * each time ferocity is calculated for the affected creature (Disarming Creature).
 */
public class SubtractDestinyFromFerocityModifier extends AbstractModifier {

    /**
     * Creates a modifier that causes destiny to be subtracted from ferocity when calculated.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards affected by this modifier
     */
    public SubtractDestinyFromFerocityModifier(PhysicalCard source, Filterable affectFilter) {
        super(source, "Disarmed (subtract destiny from ferocity)", affectFilter, null, ModifierType.SUBTRACT_DESTINY_FROM_FEROCITY, false);
    }
}
