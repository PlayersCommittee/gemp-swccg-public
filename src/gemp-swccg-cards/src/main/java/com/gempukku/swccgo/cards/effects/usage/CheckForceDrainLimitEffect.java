package com.gempukku.swccgo.cards.effects.usage;

import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.GameTextAction;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.effects.AbstractUsageLimitEffect;

/**
 * An effect that checks if a usage limit per Force drain has been reached. The effect will be successful if the limit had
 * not yet been reached.
 */
class CheckForceDrainLimitEffect extends AbstractUsageLimitEffect {
    private PhysicalCard _card;
    private GameTextActionId _gameTextActionId;
    private int _limit;

    /**
     * Creates an effect that checks if a usage limit per Force drain for an action of the specified card has been reached.
     * @param action the action performing this effect
     * @param limit the limit
     */
    protected CheckForceDrainLimitEffect(GameTextAction action, int limit) {
        super(action);
        _card = action.getActionSource();
        _gameTextActionId = action.getGameTextActionId();
        _limit = limit;
    }

    /**
     * Creates an effect that checks if a usage limit per Force drain for an action of the specified card has been reached.
     * @param action the action performing this effect
     * @param gameTextActionId the game text action id
     * @param limit the limit
     */
    protected CheckForceDrainLimitEffect(PlayCardAction action, GameTextActionId gameTextActionId, int limit) {
        super(action);
        _card = action.getActionAttachedToCard();
        _gameTextActionId = gameTextActionId;
        _limit = limit;
    }

    @Override
    public FullEffectResult playEffectReturningResult(SwccgGame game) {
        int incrementedBy = 1;
        for (String title : _card.getTitles()) {
            incrementedBy = Math.min(incrementedBy, game.getModifiersQuerying().getUntilEndOfForceDrainLimitCounter(title, _gameTextActionId).incrementToLimit(_limit, 1));
        }
        return new FullEffectResult(incrementedBy > 0);
    }
}