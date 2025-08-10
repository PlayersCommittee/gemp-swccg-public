package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier that allows a character to use combat cards attached to another character.
 */
public class MayUseOtherCharactersCombatCardsModifier extends AbstractModifier {
    private Filter _otherCharacterFilter;

    /**
     * Creates modifier that allows a character to use combat cards attached to another character.
     * @param source the source of the modifier
     * @param characterFilter the filter for characters that may use another character's combat cards
     * @param otherCharacterFilter the filter for other character
     */
    public MayUseOtherCharactersCombatCardsModifier(PhysicalCard source, Filterable characterFilter, Filterable otherCharacterFilter) {
        this(source, characterFilter, null, otherCharacterFilter);
    }

    /**
     * Creates modifier that allows cards of a specified title to target cards.
     * @param source the source of the modifier
     * @param characterFilter the filter for characters that may use another character's combat cards
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param otherCharacterFilter the filter for other character
     */
    public MayUseOtherCharactersCombatCardsModifier(PhysicalCard source, Filterable characterFilter, Condition condition, Filterable otherCharacterFilter) {
        super(source, null, characterFilter, condition, ModifierType.MAY_USE_OTHER_CHARACTERS_COMBAT_CARDS, true);
        _otherCharacterFilter = Filters.and(otherCharacterFilter);
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return "May use another character's combat cards";
    }

    @Override
    public boolean isAffectedTarget(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard target) {
        return Filters.and(_otherCharacterFilter).accepts(gameState, modifiersQuerying, target);
    }
}
