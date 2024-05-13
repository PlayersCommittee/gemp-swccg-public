package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that causes the specified player to choose a character on table to go missing.
 */
public class ChooseCharacterToGoMissingEffect extends ChooseCharactersToGoMissingEffect {

    /**
     * Creates an effect that causes the specified player to choose a character on table to go missing.
     * @param action the action performing this effect
     * @param playerId the player
     * @param characterFilter the character filter
     */
    public ChooseCharacterToGoMissingEffect(Action action, String playerId, Filterable characterFilter) {
        super(action, playerId, 1, 1, characterFilter);
    }
}
