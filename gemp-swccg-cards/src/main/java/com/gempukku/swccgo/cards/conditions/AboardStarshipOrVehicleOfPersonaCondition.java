package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

/**
 * A condition that is fulfilled when the specified card is aboard a starship or vehicle of the specified persona.
 */
public class AboardStarshipOrVehicleOfPersonaCondition implements Condition {
    private int _permCardId;
    private Persona _persona;

    /**
     * Creates a condition that is fulfilled when the specified card is aboard a starship or vehicle of the specified
     * persona.
     * @param card the card
     * @param persona the persona
     */
    public AboardStarshipOrVehicleOfPersonaCondition(PhysicalCard card, Persona persona) {
        _permCardId = card.getPermanentCardId();
        _persona = persona;
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        PhysicalCard card = gameState.findCardByPermanentId(_permCardId);

        return Filters.aboardStarshipOrVehicleOfPersona(_persona).accepts(gameState, modifiersQuerying, card);
    }
}
