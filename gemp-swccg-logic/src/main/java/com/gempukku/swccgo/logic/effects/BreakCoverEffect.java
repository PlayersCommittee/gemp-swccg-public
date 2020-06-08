package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.results.CoverBrokenResult;

/**
 * An effect break an undercover spy's cover.
 */
public class BreakCoverEffect extends AbstractSuccessfulEffect {
    private PhysicalCard _card;

    /**
     * Creates an effect that break an undercover spy's cover.
     * @param action the action performing this effect
     * @param card the undercover spy's whose cover to break
     */
    public BreakCoverEffect(Action action, PhysicalCard card) {
        super(action);
        _card = card;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        if (!_card.isUndercover())
            return;

        GameState gameState = game.getGameState();
        gameState.sendMessage(GameUtils.getCardLink(_card) + "'s 'cover is broken'");
        gameState.breakCover(_card);

        game.getActionsEnvironment().emitEffectResult(new CoverBrokenResult(_action.getPerformingPlayer(), _card));
    }
}
