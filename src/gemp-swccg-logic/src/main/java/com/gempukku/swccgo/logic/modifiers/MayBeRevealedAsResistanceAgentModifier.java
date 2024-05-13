package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.InBattleCondition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;

/**
 * A modifier that allows a card to be revealed as a Resistance Agent with I Want That Map
 */
public class MayBeRevealedAsResistanceAgentModifier extends AbstractModifier {
    /**
     * Creates a modifier that allows a card to be revealed as a Resistance Agent
     * @param source the source of the modifier
     */
    public MayBeRevealedAsResistanceAgentModifier(PhysicalCard source, Filterable affectFilter) {
        super(source, "May be revealed as a Resistance Agent", Filters.and(affectFilter), ModifierType.MAY_BE_REVEALED_AS_RESISTANCE_AGENT, false);
    }
}
