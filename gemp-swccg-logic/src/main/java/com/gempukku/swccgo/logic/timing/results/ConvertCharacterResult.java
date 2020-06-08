package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * The effect result that is emitted when a character is converted.
 */
public class ConvertCharacterResult extends EffectResult {
    private PhysicalCard _characterConverted;
    private PhysicalCard _characterReplacedWith;

    /**
     * Creates an effect result that is emitted when a character is converted.
     * @param performingPlayerId the player that performed the action
     * @param characterConverted the character that was converted
     * @param characterReplacedWith the character that was character was replaced with, or null
     */
    public ConvertCharacterResult(String performingPlayerId, PhysicalCard characterConverted, PhysicalCard characterReplacedWith) {
        super(Type.CONVERT_CHARACTER, performingPlayerId);
        _characterConverted = characterConverted;
        _characterReplacedWith = characterReplacedWith;
    }

    /**
     * Gets the character that was converted.
     * @return the character
     */
    public PhysicalCard getConvertedCharacter() {
        return _characterConverted;
    }

    /**
     * Gets the character that was character was replaced with, or null.
     * @return the character
     */
    public PhysicalCard getCharacterReplacedWith() {
        return _characterReplacedWith;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return GameUtils.getCardLink(_characterConverted) + " just converted" + (_characterReplacedWith != null ? (" to " + GameUtils.getCardLink(_characterReplacedWith)) : "");
    }
}
