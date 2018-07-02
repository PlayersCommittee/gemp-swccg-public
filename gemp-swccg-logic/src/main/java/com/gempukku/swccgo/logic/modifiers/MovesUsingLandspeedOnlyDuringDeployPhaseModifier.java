package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.game.PhysicalCard;

/**
 * A modifier that causes affected cards to move using landspeed only during deploy phase (instead of move phase).
 */
public class MovesUsingLandspeedOnlyDuringDeployPhaseModifier extends AbstractModifier {

    /**
     * Creates a modifier that causes the source card to be move using landspeed only during deploy phase (instead of move phase).
     * @param source the source of the modifier
     */
    public MovesUsingLandspeedOnlyDuringDeployPhaseModifier(PhysicalCard source) {
        super(source, "Moves using landspeed only during Deploy phase", source, null, ModifierType.MOVES_USING_LANDSPEED_ONLY_DURING_DEPLOY_PHASE);
    }
}
