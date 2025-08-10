package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier that allows affected Interrupts to be played to cancel a card being played or on table.
 */
public class MayPlayToCancelCardModifier extends AbstractModifier {
    private Filter _cardToCancelFilter;

    /**
     * Creates a modifier that allows an Interrupt accepted by the filter to be played to cancel a card accepted by
     * cardToCancelFilter being played or on table.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param cardToCancelFilter the card to cancel filter
     */
    public MayPlayToCancelCardModifier(PhysicalCard source, Filterable affectFilter, Filterable cardToCancelFilter) {
        this(source, affectFilter, null, cardToCancelFilter);
    }

    /**
     * Creates a modifier that allows an Interrupt accepted by the filter to be played to cancel a card accepted by
     * cardToCancelFilter being played or on table.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param cardToCancelFilter the card to cancel filter
     */
    public MayPlayToCancelCardModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Filterable cardToCancelFilter) {
        super(source, null, Filters.and(Filters.Interrupt, affectFilter), condition, ModifierType.MAY_PLAY_TO_CANCEL_CARD, true);
        _cardToCancelFilter = Filters.and(cardToCancelFilter);
    }

    @Override
    public boolean isAffectedTarget(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard targetCard) {
        return Filters.and(_cardToCancelFilter).accepts(gameState, modifiersQuerying, targetCard);
    }
}
