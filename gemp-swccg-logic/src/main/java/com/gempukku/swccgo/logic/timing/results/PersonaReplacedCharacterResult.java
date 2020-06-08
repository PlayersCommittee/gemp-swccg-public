package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * The effect result that is emitted when a character is persona replaced with another character of the same persona.
 */
public class PersonaReplacedCharacterResult extends EffectResult {
    private PhysicalCard _oldCharacter;
    private PhysicalCard _newCharacter;

    /**
     * Creates an effect result that is emitted when a character is persona replaced with another character of the same persona.
     * @param playerId the performing player
     * @param oldCharacter the old character
     * @param newCharacter the new character
     */
    public PersonaReplacedCharacterResult(String playerId, PhysicalCard oldCharacter, PhysicalCard newCharacter) {
        super(Type.PERSONA_REPLACED_CHARACTER, playerId);
        _oldCharacter = oldCharacter;
        _newCharacter = newCharacter;
    }

    /**
     * Gets the old character.
     * @return the old character
     */
    public PhysicalCard getOldCharacter() {
        return _oldCharacter;
    }

    /**
     * Gets the new character.
     * @return the new character
     */
    public PhysicalCard getNewCharacter() {
        return _newCharacter;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return GameUtils.getCardLink(_newCharacter) + " just persona replaced " + GameUtils.getCardLink(_oldCharacter);
    }
}
