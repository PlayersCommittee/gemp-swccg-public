package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that causes affected cards to be immune to landspeed requirements from specified cards.
 */
public class ImmuneToLandspeedRequirementsModifier extends AbstractModifier {
    private Filter _immuneToFilters;

    /**
     * Creates a modifier that causes the source card to be immune to landspeed requirements cards accepted by immuneToFilter.
     * @param source the source of the modifier
     * @param immuneToFilter the filter for cards that affected cards are immune to their landspeed requirement modifiers
     */
    public ImmuneToLandspeedRequirementsModifier(PhysicalCard source, Filterable immuneToFilter) {
        this(source, source, null, immuneToFilter);
    }

    /**
     * Creates a modifier that causes affected cards to be immune to landspeed requirements cards accepted by immuneToFilter.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards affected by this modifier
     * @param immuneToFilter the filter for cards that affected cards are immune to their deploy cost modifiers
     */
    public ImmuneToLandspeedRequirementsModifier(PhysicalCard source, Filterable affectFilter, Filterable immuneToFilter) {
        this(source, affectFilter, null, immuneToFilter);
    }

    /**
     * Creates a modifier that causes affected cards to be immune to landspeed requirements cards accepted by immuneToFilter.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards affected by this modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param immuneToFilter the filter for cards that affected cards are immune to their deploy cost modifiers
     */
    public ImmuneToLandspeedRequirementsModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Filterable immuneToFilter) {
        super(source, null, affectFilter, condition, ModifierType.IMMUNE_TO_LANDSPEED_REQUIREMENTS, true);
        _immuneToFilters = Filters.and(immuneToFilter);
    }

    @Override
    public boolean isImmuneToLandspeedRequirementModifierFromCard(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard sourceOfModifier) {
        return Filters.and(_immuneToFilters).accepts(gameState, modifiersQuerying, sourceOfModifier);
    }
}
