package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * Emitted after the new persona is on table and before the replaced character is placed in Lost Pile.
 * "When replacing" responses (See-Threepio retrieve) use this. "Just persona replaced" stays after Lost Pile.
 */
public class AboutToPersonaReplaceCharacterResult extends EffectResult {
    private PhysicalCard _oldCharacter;
    private PhysicalCard _newCharacter;

    public AboutToPersonaReplaceCharacterResult(String playerId, PhysicalCard oldCharacter, PhysicalCard newCharacter) {
        super(Type.ABOUT_TO_PERSONA_REPLACE_CHARACTER, playerId);
        _oldCharacter = oldCharacter;
        _newCharacter = newCharacter;
    }

    public PhysicalCard getOldCharacter() {
        return _oldCharacter;
    }

    public PhysicalCard getNewCharacter() {
        return _newCharacter;
    }

    @Override
    public String getText(SwccgGame game) {
        return GameUtils.getCardLink(_newCharacter) + " is replacing " + GameUtils.getCardLink(_oldCharacter);
    }
}
