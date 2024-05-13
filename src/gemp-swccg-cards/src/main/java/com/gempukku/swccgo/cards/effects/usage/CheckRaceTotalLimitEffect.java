package com.gempukku.swccgo.cards.effects.usage;

import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.GameTextAction;
import com.gempukku.swccgo.logic.effects.AbstractUsageLimitEffect;

import java.util.List;

/**
 * An effect that checks if a usage limit per race total has been reached. The effect will be successful if the limit had
 * not yet been reached.
 */
class CheckRaceTotalLimitEffect extends AbstractUsageLimitEffect {
    private PhysicalCard _card;
    private GameTextActionId _gameTextActionId;
    private float _raceTotal;
    private int _limit;

    /**
     * Creates an effect that checks if a usage limit per race total for an action of the specified card has been reached.
     * @param action the action performing this effect
     * @param raceTotal the race total
     * @param limit the limit
     */
    protected CheckRaceTotalLimitEffect(GameTextAction action, float raceTotal, int limit) {
        super(action);
        _card = action.getActionSource();
        _gameTextActionId = action.getGameTextActionId();
        _raceTotal = raceTotal;
        _limit = limit;
    }

    @Override
    public FullEffectResult playEffectReturningResult(SwccgGame game) {
        int incrementedBy = 1;
        List<String> titles = _card.getTitles();
        for (String title : titles) {
            incrementedBy = Math.min(incrementedBy, game.getModifiersQuerying().getPerRaceTotalLimitCounter(title, _gameTextActionId, _raceTotal).incrementToLimit(_limit, 1));
        }
        return new FullEffectResult(incrementedBy > 0);
    }
}