package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * The effect result that is emitted when a character is crossed-over.
 */
public class CrossedOverResult extends EffectResult {
    private PhysicalCard _character;

    public CrossedOverResult(String performingPlayerId, PhysicalCard character) {
        super(Type.CROSSED_OVER_CHARACTER, performingPlayerId);
        _character = character;
    }

    /**
     * Gets the character that was crossed-over.
     * @return the character
     */
    public PhysicalCard getCharacter() {
        return _character;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "Crossed " + GameUtils.getCardLink(_character) + " over to the " + game.getSide(_character.getOwner()).getHumanReadable() + " Side";
    }
}
