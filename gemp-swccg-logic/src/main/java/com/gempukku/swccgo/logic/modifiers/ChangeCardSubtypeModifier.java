package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that changes the subtype of a card
 */
public class ChangeCardSubtypeModifier extends AbstractModifier {
    private CardSubtype _subtype;

    /**
     * Creates a modifier that changes the subtype of a card
     * @param source the source of the modifier
     * @param affectFilter the affected cards filter
     * @param subtype the new CardSubtype
     */
    public ChangeCardSubtypeModifier(PhysicalCard source, Filterable affectFilter, CardSubtype subtype) {
        super(source, "Changed subtype", affectFilter, ModifierType.MODIFY_CARD_SUBTYPE);
        _subtype = subtype;
    }

    public CardSubtype getSubtype() {
        return _subtype;
    }
}
