package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.results.FoundResult;

/**
 * An effect that finds the specified 'missing' character.
 */
public class FindMissingCharacterEffect extends AbstractSuccessfulEffect {
    private PhysicalCard _character;
    private boolean _bySearchParty;

    /**
     * Creates an effect that finds the specified 'missing' character.
     * @param action the action performing this effect
     * @param character the character to find
     * @param bySearchParty true if found by a search party, otherwise false
     */
    public FindMissingCharacterEffect(Action action, PhysicalCard character, boolean bySearchParty) {
        super(action);
        _character = character;
        _bySearchParty = bySearchParty;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        if (!_character.isMissing())
            return;

        GameState gameState = game.getGameState();

        String foundByText = _bySearchParty ? " by search party" : (" using " + GameUtils.getCardLink(_action.getActionSource()));
        gameState.sendMessage(_action.getPerformingPlayer() + " finds 'missing' character " + GameUtils.getCardLink(_character) + foundByText);
        gameState.findMissingCharacter(game, _character);

        // Emit effect result that character was found
        game.getActionsEnvironment().emitEffectResult(new FoundResult(_action.getPerformingPlayer(), _character));
    }
}
