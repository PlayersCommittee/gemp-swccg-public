package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.results.FrozenResult;

/**
 * An effect that freezes a character.
 */
public class FreezeCharacterEffect extends AbstractSuccessfulEffect {
    private PhysicalCard _character;

    /**
     * Creates an effect that freezes a character.
     * @param action the action performing this effect
     * @param character the character to be frozen
     */
    public FreezeCharacterEffect(Action action, PhysicalCard character) {
        super(action);
        _character = character;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        if (_character.isFrozen())
            return;

        String performingPlayer = _action.getPerformingPlayer();
        PhysicalCard source = _action.getActionSource();
        GameState gameState = game.getGameState();

        if (performingPlayer == null)
            gameState.sendMessage(GameUtils.getCardLink(source) + " causes " + GameUtils.getCardLink(_character) + " to be 'frozen'");
        else
            gameState.sendMessage(performingPlayer + " causes " + GameUtils.getCardLink(_character) + " to be 'frozen' using " + GameUtils.getCardLink(_action.getActionSource()));
        gameState.cardAffectsCard(performingPlayer, source, _character);
        gameState.freezeCharacter(_character);

        game.getActionsEnvironment().emitEffectResult(new FrozenResult(performingPlayer, _character));
    }
}
