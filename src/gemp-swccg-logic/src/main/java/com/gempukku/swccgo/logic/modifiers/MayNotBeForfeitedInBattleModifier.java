package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that causes affect cards to not be able to be forfeited in battle.
 */
public class MayNotBeForfeitedInBattleModifier extends KeywordModifier {

    /**
     * Creates a modifier that causes cards accepted by the filter to not be able to be forfeited in battle.
     * @param source the source of the modifier
     * @param affectFilter the filter
     */
    public MayNotBeForfeitedInBattleModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null);
    }

    /**
     * Creates a modifier that causes cards accepted by the filter to not be able to be forfeited in battle.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayNotBeForfeitedInBattleModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, affectFilter, condition, Keyword.MAY_NOT_BE_FORFEITED_IN_BATTLE);
    }
}
