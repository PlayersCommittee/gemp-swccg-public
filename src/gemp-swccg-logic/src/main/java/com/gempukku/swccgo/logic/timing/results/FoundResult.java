package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * An effect result that is emitted when a 'missing' character is found.
 */
public class FoundResult extends EffectResult {
    private PhysicalCard _characterFound;

    /**
     * Creates an effect result that is emitted when a 'missing' character is found.
     * @param performingPlayerId the performing player
     * @param characterFound the character found
     */
    public FoundResult(String performingPlayerId, PhysicalCard characterFound) {
        super(Type.FOUND, performingPlayerId);
        _characterFound = characterFound;
    }

    /**
     * Gets the character that was found.
     * @return the character
     */
    public PhysicalCard getCharacterFound() {
        return _characterFound;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "Found " + GameUtils.getCardLink(_characterFound);
    }
}
