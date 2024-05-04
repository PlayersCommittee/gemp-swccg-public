package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that causes affected starship to not be lost from asteroid destiny if asteroid sector is drawn for destiny.
 */
public class NotLostIfAsteroidSectorDrawnForAsteroidDestinyModifier extends AbstractModifier {

    /**
     * Creates a modifier that causes affected starship to not be lost from asteroid destiny if asteroid sector is drawn for destiny.
     * @param source the source of the modifier
     * @param affectFilter the filter
     */
    public NotLostIfAsteroidSectorDrawnForAsteroidDestinyModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null);
    }

    /**
     * Creates a modifier that causes affected starship to not be lost from asteroid destiny if asteroid sector is drawn for destiny.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public NotLostIfAsteroidSectorDrawnForAsteroidDestinyModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "Not lost if asteroid sector drawn for asteroid destiny", affectFilter, condition, ModifierType.NOT_LOST_IF_ASTEROID_SECTOR_DRAWN_FOR_ASTEROID_DESTINY);
    }
}
