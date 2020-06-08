package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;


/**
 * This effect records the result of a sabacc game for the purposes of the game keeping track of which cards
 * won a sabacc game during the game.
 */
class RecordSabaccVictoryEffect extends AbstractSuccessfulEffect {
    private PhysicalCard _winningCharacter;

    /**
     * Creates an effect that records the result of a sabacc game for the purposes of the game keeping track of which cards
     * won a sabacc game during the game.
     * @param action the action performing this effect
     * @param winningCharacter the character that won sabacc, or null if winning player did not choose a character to play sabacc
     */
    public RecordSabaccVictoryEffect(Action action, PhysicalCard winningCharacter) {
        super(action);
        _winningCharacter = winningCharacter;
    }

    @Override
    public void doPlayEffect(SwccgGame game) {
        if (_winningCharacter != null) {
            game.getModifiersQuerying().wonSabaccGame(_winningCharacter);
        }
    }
}
