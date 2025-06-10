package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier that allows cards to be targeted by cards.
 */
public class MayBeTargetedByModifier extends AbstractModifier {
    private String _mayBeTargetedByCardTitle;

    /**
     * Creates a modifier that allows cards of a specified title to target cards.
     * @param source the card that is the source of the modifier and that may be targeted
     * @param cardTitle the card title that may target the affected cards
     */
    public MayBeTargetedByModifier(PhysicalCard source, String cardTitle) {
        this(source, source, null, cardTitle);
    }

    /**
     * Creates modifier that allows cards of a specified title to target cards.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that may be targeted
     * @param cardTitle the card title that may target the affected cards
     */
    public MayBeTargetedByModifier(PhysicalCard source, Filterable affectFilter, String cardTitle) {
        this(source, affectFilter, null, cardTitle);
    }

    /**
     * Creates modifier that allows cards of a specified title to target cards.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that may be targeted
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param cardTitle the card title that may target the affected cards
     */
    public MayBeTargetedByModifier(PhysicalCard source, Filterable affectFilter, Condition condition, String cardTitle) {
        super(source, null, affectFilter, condition, ModifierType.MAY_BE_TARGETED_BY, true);
        _mayBeTargetedByCardTitle = cardTitle;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return "May be targeted by " + _mayBeTargetedByCardTitle;
    }

    @Override
    public boolean grantedToBeTargetedByCard(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card) {
        return Filters.and(Filters.title(_mayBeTargetedByCardTitle)).accepts(gameState, modifiersQuerying, card);
    }
}
