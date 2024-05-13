package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.TargetId;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.results.RetargetedEffectResult;

import java.util.Map;

/**
 * An effect re-target an Effect on table.
 */
public class RetargetEffectEffect extends AbstractSuccessfulEffect {
    private PhysicalCard _effectToRetarget;
    private PhysicalCard _cardToTarget;

    /**
     * Creates an effect that re-targets an Effect on table.
     * @param action the action performing this effect
     * @param effectToRetarget the Effect to re-target
     * @param cardToTarget the new target to target
     */
    public RetargetEffectEffect(Action action, PhysicalCard effectToRetarget, PhysicalCard cardToTarget) {
        super(action);
        _effectToRetarget = effectToRetarget;
        _cardToTarget = cardToTarget;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();
        String performingPlayerId = _action.getPerformingPlayer();
        Map<TargetId, PhysicalCard> targetedCardsMap = _effectToRetarget.getTargetedCards(gameState);
        for (TargetId targetId : targetedCardsMap.keySet()) {
            if (_effectToRetarget.getValidTargetedFilter(targetId).accepts(game, _cardToTarget)) {
                PhysicalCard prevTarget = _effectToRetarget.getTargetedCard(gameState, targetId);

                gameState.sendMessage(performingPlayerId + " re-targets " + GameUtils.getCardLink(_effectToRetarget) + " from " + GameUtils.getCardLink(prevTarget) + " to " + GameUtils.getCardLink(_cardToTarget));
                gameState.cardAffectsCard(performingPlayerId, _effectToRetarget, _cardToTarget);

                // Re-target Effect to target cards stored in the Effect itself
                _effectToRetarget.setTargetedCard(targetId, _effectToRetarget.getTargetGroupId(targetId), _cardToTarget, _effectToRetarget.getValidTargetedFilter(targetId));
                game.getActionsEnvironment().emitEffectResult(new RetargetedEffectResult(_effectToRetarget, performingPlayerId));
                return;
            }
        }
    }
}
