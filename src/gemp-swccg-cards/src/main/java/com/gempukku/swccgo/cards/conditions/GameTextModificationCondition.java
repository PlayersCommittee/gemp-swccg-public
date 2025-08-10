package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;

/**
 * A condition that is fulfilled when the specified card has the specified game text modification active.
 */
public class GameTextModificationCondition implements Condition {
    private int _permCardId;
    private ModifyGameTextType _type;

    /**
     * Creates a condition that is fulfilled when the specified card has the specified game text modification active.
     * @param card the card
     * @param type the game text modification type
     */
    public GameTextModificationCondition(PhysicalCard card, ModifyGameTextType type) {
        _permCardId = card.getPermanentCardId();
        _type = type;
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        PhysicalCard card = gameState.findCardByPermanentId(_permCardId);

        return modifiersQuerying.hasGameTextModification(gameState, card, _type);
    }
}
