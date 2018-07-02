package com.gempukku.swccgo.cards.effects.usage;

import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.GameTextAction;
import com.gempukku.swccgo.logic.effects.AbstractUsageLimitEffect;

/**
 * An effect that checks if a usage limit per turn has been reached. The effect will be successful if the limit had
 * not yet been reached.
 */
class CheckTurnLimitEffect extends AbstractUsageLimitEffect {
    private PhysicalCard _card;
    private String _playerId;
    private int _gameTextSourceCardId;
    private GameTextActionId _gameTextActionId;
    private int _limit;

    /**
     * Creates an effect that checks if a usage limit per turn for an action of the specified card has been reached.
     * @param action the action performing this effect
     * @param limit the limit
     */
    protected CheckTurnLimitEffect(GameTextAction action, int limit) {
        super(action);
        _card = action.getActionSource();
        _playerId = action.getPerformingPlayer();
        _gameTextSourceCardId = action.getGameTextSourceCardId();
        _gameTextActionId = action.getGameTextActionId();
        _limit = limit;
    }

    @Override
    public FullEffectResult playEffectReturningResult(SwccgGame game) {
        int incrementedBy = game.getModifiersQuerying().getUntilEndOfTurnLimitCounter(_card, _playerId, _gameTextSourceCardId, _gameTextActionId).incrementToLimit(_limit, 1);
        return new FullEffectResult(incrementedBy > 0);
    }
}