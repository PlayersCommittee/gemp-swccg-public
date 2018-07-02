package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that causes the specified player to ignore location deployment restrictions from the specified cards.
 */
public class IgnoresLocationDeploymentRestrictionsFromCardModifier extends AbstractModifier {

    /**
     * Creates a modifier that causes the specified player to ignore location deployment restrictions from cards accepted by the filter.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param playerId the player
     */
    public IgnoresLocationDeploymentRestrictionsFromCardModifier(PhysicalCard source, Filterable affectFilter, String playerId) {
        this(source, affectFilter, null, playerId);
    }

    /**
     * Creates a modifier that causes the specified player to ignore location deployment restrictions from cards accepted by the filter.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param playerId the player
     */
    public IgnoresLocationDeploymentRestrictionsFromCardModifier(PhysicalCard source, Filterable affectFilter, Condition condition, String playerId) {
        super(source, playerId + " ignores location deployment restrictions", affectFilter, condition, ModifierType.IGNORES_LOCATION_DEPLOYMENT_RESTRICTIONS_FROM_CARD);
        _playerId = playerId;
    }
}
