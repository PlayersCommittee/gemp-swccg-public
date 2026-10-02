package com.gempukku.swccgo.logic.modifiers.querying;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.ModifierType;

/**
 * Optional (non-replacing) deploy-for-free queries.
 */
public interface MayDeploy extends BaseQuery {

    /**
     * Determines if a card is granted the option to deploy for free (in addition to paying its printed cost).
     * Unlike grantedDeployForFree, this does not replace the printed cost.
     */
    default boolean mayDeployForFree(GameState gameState, PhysicalCard card) {
        return !getModifiersAffectingCard(gameState, ModifierType.MAY_DEPLOY_FOR_FREE, card).isEmpty();
    }
}
