package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.TargetId;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

/**
 * An effect re-target a card being played.
 */
public class RetargetCardBeingPlayedEffect extends AbstractSuccessfulEffect {
    private RespondablePlayingCardEffect _playingCardEffect;
    private int _targetGroupId;
    private Collection<PhysicalCard> _cardsToTarget;

    /**
     * Creates an effect that re-targets a card being played.
     * @param action the action performing this effect
     * @param playingCardEffect the playing card effect
     * @param targetGroupId the id of the group to re-target
     * @param cardsToTarget the new cards to target
     */
    public RetargetCardBeingPlayedEffect(Action action, RespondablePlayingCardEffect playingCardEffect, int targetGroupId, Collection<PhysicalCard> cardsToTarget) {
        super(action);
        _playingCardEffect = playingCardEffect;
        _targetGroupId = targetGroupId;
        _cardsToTarget = cardsToTarget;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();
        String performingPlayerId = _action.getPerformingPlayer();
        PhysicalCard cardBeingPlayed = _playingCardEffect.getCard();
        Action playingCardAction = _playingCardEffect.getTargetingAction();
        Collection<PhysicalCard> prevTargets = new LinkedList<PhysicalCard>(playingCardAction.getPrimaryTargetCards(_targetGroupId));

        gameState.sendMessage(performingPlayerId + " re-targets " + GameUtils.getCardLink(cardBeingPlayed) + " from " + GameUtils.getAppendedNames(prevTargets) + " to " + GameUtils.getAppendedNames(_cardsToTarget));
        gameState.cardAffectsCards(performingPlayerId, cardBeingPlayed, _cardsToTarget);
        playingCardAction.updatePrimaryTargetCards(game, _targetGroupId, _cardsToTarget);

        // Also re-target Effect to target cards stored in the Effect itself
        if (Filters.Effect_of_any_Kind.accepts(game, cardBeingPlayed) && _cardsToTarget.size() == 1) {
            Map<TargetId, PhysicalCard> targetedCards = cardBeingPlayed.getTargetedCards(gameState);
            for (TargetId targetId : targetedCards.keySet()) {
                if (prevTargets.contains(targetedCards.get(targetId))) {
                    cardBeingPlayed.setTargetedCard(targetId, cardBeingPlayed.getTargetGroupId(targetId), _cardsToTarget.iterator().next(), cardBeingPlayed.getValidTargetedFilter(targetId));
                    return;
                }
            }
        }
    }
}
