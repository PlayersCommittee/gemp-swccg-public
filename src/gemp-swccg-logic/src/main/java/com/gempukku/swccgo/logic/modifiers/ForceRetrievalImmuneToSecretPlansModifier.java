package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier that causes Force retrieve from specified cards to be immune to Secret Plans.
 */
public class ForceRetrievalImmuneToSecretPlansModifier extends AbstractModifier {

    /**
     * Creates a modifier that causes Force retrieve from affected cards to be immune to Secret Plans.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that land for free
     */
    public ForceRetrievalImmuneToSecretPlansModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null);
    }

    /**
     * Creates a modifier that causes affected cards to land for free.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that land for free
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    private ForceRetrievalImmuneToSecretPlansModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, null, affectFilter, condition, ModifierType.FORCE_RETRIEVAL_IMMUNE_TO_SECRET_PLANS, true);
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return "Force retrieval immune to Secret Plans";
    }
}
