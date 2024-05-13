package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that causes the specified player to choose a character on table to be captured.
 */
public class ChooseCharacterOnTableToCaptureEffect extends ChooseCharactersOnTableToCaptureEffect {

    /**
     * Creates an effect that causes the specified player to choose a character on table to be captured.
     * @param action the action performing this effect
     * @param playerId the player
     * @param characterFilter the character filter
     * @param cardFiringWeapon the card that fired weapon that caused capture, or null
     */
    public ChooseCharacterOnTableToCaptureEffect(Action action, String playerId, Filterable characterFilter, PhysicalCard cardFiringWeapon) {
        super(action, playerId, 1, 1, characterFilter, cardFiringWeapon);
    }
}
