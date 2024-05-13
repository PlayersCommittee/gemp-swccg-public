package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that causes affect cards to must be forfeited in battle.
 */
public class MustBeForfeitedInBattleModifier extends KeywordModifier {

    /**
     * Creates a modifier that causes the source card to must be forfeited in battle.
     * @param source the card that is the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MustBeForfeitedInBattleModifier(PhysicalCard source, Condition condition) {
        this(source, source, condition);
    }

    /**
     * Creates a modifier that causes cards accepted by the filter to must be forfeited in battle.
     * @param source the source of the modifier
     * @param affectFilter the filter
     */
    public MustBeForfeitedInBattleModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null);
    }

    /**
     * Creates a modifier that causes cards accepted by the filter to must be forfeited in battle.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MustBeForfeitedInBattleModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, affectFilter, condition, Keyword.MUST_BE_FORFEITED_IN_BATTLE);
    }
}
