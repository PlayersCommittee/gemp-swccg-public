package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier that causes affect cards to be able to move.
 */
public class MayMoveModifier extends KeywordModifier {

    /**
     * Creates a modifier that causes cards accepted by the filter to be able to move.
     * @param source the source of the modifier
     * @param affectFilter the filter
     */
    public MayMoveModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null);
    }

    /**
     * Creates a modifier that causes cards accepted by the filter to be able to move.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayMoveModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, affectFilter, condition, Keyword.MAY_MOVE);
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return "May move";
    }
}
