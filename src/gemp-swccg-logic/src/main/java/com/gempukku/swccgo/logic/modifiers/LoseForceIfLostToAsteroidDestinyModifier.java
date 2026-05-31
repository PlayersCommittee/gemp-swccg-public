package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;

/**
 * A modifier that causes owner to lose force (equal to starship’s forfeit value) if the affected starship is lost due to asteroid destiny.
 */
public class LoseForceIfLostToAsteroidDestinyModifier extends AbstractModifier {

    /**
     * Creates a modifier that causes owner to lose force (equal to starship’s forfeit value) if the affected starship is lost due to asteroid destiny.
     * @param source the source of the modifier
     * @param affectFilter the filter
     */
    public LoseForceIfLostToAsteroidDestinyModifier(PhysicalCard source, Filterable affectFilter) {
        super(source, "If lost to asteroid destiny, also lose force equal to forfeit value", affectFilter, ModifierType.LOSE_FORCE_IF_LOST_TO_ASTEROID_DESTINY);
    }
}

