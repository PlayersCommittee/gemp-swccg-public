package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.game.PhysicalCard;

/**
 * A modifier that causes the affected cards to deploy and move like an undercover spy.
 */
public class DeploysAndMovesLikeUndercoverSpyModifier extends AbstractModifier {

    /**
     * Creates a modifier that causes the source card to deploy and move like an undercover spy.
     * @param source the source of the modifier
     */
    public DeploysAndMovesLikeUndercoverSpyModifier(PhysicalCard source) {
        super(source, "Deploys and moves like undercover spy", source, ModifierType.DEPLOYS_AND_MOVES_LIKE_UNDERCOVER_SPY);
    }
}
