package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier that adds a card type to a card
 */
public class AddCardTypeModifier extends AbstractModifier {
    private CardType _type;

    /**
     * Creates a modifier that adds a card type to a card
     * @param source the source of the modifier
     * @param affectFilter the affected cards filter
     * @param type the CardType to add
     */
    public AddCardTypeModifier(PhysicalCard source, Filterable affectFilter, CardType type) {
        super(source, "Added type", affectFilter, ModifierType.ADD_CARD_TYPE);
        _type = type;
    }

    public CardType getType() {
        return _type;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return "Added "+_type.getHumanReadable()+" type";
    }
}
