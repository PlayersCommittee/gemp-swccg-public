package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that prevents specified cards from being played unless they are immune to a specific title.
 */
public class MayNotPlayUnlessImmuneToSpecificTitleModifier extends AbstractModifier {
    private String _title;
    /**
     * Creates a modifier that prevents the source card from being played unless it is immune to a specific title.
     * @param affectFilter the filter
     * @param source the source of the modifier
     */
    public MayNotPlayUnlessImmuneToSpecificTitleModifier(PhysicalCard source, Filterable affectFilter, String title) {
        super(source, "May not be played unless immune to "+title, Filters.and(affectFilter, Filters.not(Filters.in_play), Filters.canBeTargetedBy(source)), ModifierType.MAY_NOT_BE_PLAYED_UNLESS_IMMUNE_TO_SPECIFIC_TITLE);
        _title = title;
    }

    /**
     * Creates a modifier that prevents the specified player from playing cards accepted by the filter unless they are immune to a specific title.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayNotPlayUnlessImmuneToSpecificTitleModifier(PhysicalCard source, Filterable affectFilter, Condition condition, String title) {
        super(source, "May not be played unless immune to "+title, Filters.and(affectFilter, Filters.not(Filters.in_play), Filters.canBeTargetedBy(source)), condition, ModifierType.MAY_NOT_BE_PLAYED_UNLESS_IMMUNE_TO_SPECIFIC_TITLE);
        _title = title;
    }

    public String getTitle() {
        return _title;
    }
}
