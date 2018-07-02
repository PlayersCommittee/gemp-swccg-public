package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.InactiveReason;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.TargetingType;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.TargetingEffect;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * An abstract class for choosing cards that the card being played is to be re-targeted from the specified group to cards
 * that are the same side of the Force.
 */
public abstract class TargetGroupOnSameSideOfForceToRetargetBeingPlayedToEffect extends AbstractSubActionEffect implements TargetingEffect {
    private final RespondablePlayingCardEffect _playingCardEffect;
    private final int _targetGroupIdToRetarget;

    /**
     * Creates an effect to choose cards that the card being played is to be re-targeted from the specified group to cards
     * that are the same side of the Force.
     * @param action the action performing this effect
     * @param playingCardEffect the playing card effect
     * @param targetGroupId the id of the group to re-target
     */
    public TargetGroupOnSameSideOfForceToRetargetBeingPlayedToEffect(Action action, RespondablePlayingCardEffect playingCardEffect, int targetGroupId) {
        super(action);
        _playingCardEffect = playingCardEffect;
        _targetGroupIdToRetarget = targetGroupId;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(final SwccgGame game) {
        final PhysicalCard sourceCard = _action.getActionSource();
        final String performingPlayerId = _action.getPerformingPlayer();
        final Action playCardAction = _playingCardEffect.getTargetingAction();
        final Action parentAction = _action;

        final SubAction subAction = new SubAction(_action);
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        final Collection<PhysicalCard> cardsToRetargetTo = Filters.filterAllOnTable(game, Filters.cardThatCardBeingPlayedCanBeRetargetedFromSameSideOfForceTo(sourceCard, _targetGroupIdToRetarget));
                        int minCardsToTarget = playCardAction.getPrimaryMinimumCardsToTarget(_targetGroupIdToRetarget);
                        int maxCardsToTarget = playCardAction.getPrimaryMaximumCardsToTarget(_targetGroupIdToRetarget);
                        int maxAcceptsCountToTarget = playCardAction.getPrimaryMaximumAcceptsCountToTarget(_targetGroupIdToRetarget);
                        boolean matchPartialModelType = playCardAction.getPrimaryTargetMatchPartialModelType(_targetGroupIdToRetarget);
                        final boolean isTargetAll = playCardAction.getPrimaryTargetingAll(_targetGroupIdToRetarget);
                        TargetingType targetingType = playCardAction.getPrimaryTargetingType(_targetGroupIdToRetarget);
                        Map<InactiveReason, Boolean> spotOverrides = playCardAction.getPrimaryTargetSpotOverrides(_targetGroupIdToRetarget);
                        Map<TargetingReason, Filterable> oldTargetFilters = playCardAction.getPrimaryTargetFilter(_targetGroupIdToRetarget);
                        Map<TargetingReason, Filterable> newTargetFilters = new HashMap<TargetingReason, Filterable>();
                        for (TargetingReason targetingReason : oldTargetFilters.keySet()) {
                            newTargetFilters.put(targetingReason, Filters.and(Filters.in(cardsToRetargetTo), oldTargetFilters.get(targetingReason)));
                        }

                        // Perform choosing of targets based on targeting type to use
                        if (targetingType == TargetingType.TARGET_CARDS_AT_SAME_LOCATION) {
                            parentAction.appendTargeting(
                                    new TargetCardsAtSameLocationEffect(parentAction, performingPlayerId, "Choose card" + GameUtils.s(minCardsToTarget) + " to re-target to",
                                            minCardsToTarget, maxCardsToTarget, maxAcceptsCountToTarget, matchPartialModelType, spotOverrides, newTargetFilters) {
                                        @Override
                                        protected void cardsTargeted(int targetGroupId, Collection<PhysicalCard> targetedCards) {
                                            cardsRetargetedTo(targetGroupId, targetedCards);
                                        }
                                        @Override
                                        protected boolean isTargetAll() {
                                            return isTargetAll;
                                        }
                                    });
                        }
                        else {
                            parentAction.appendTargeting(
                                    new TargetCardsOnTableEffect(parentAction, performingPlayerId, "Choose card" + GameUtils.s(minCardsToTarget) + " to re-target to",
                                            minCardsToTarget, maxCardsToTarget, maxAcceptsCountToTarget, matchPartialModelType, spotOverrides, newTargetFilters) {
                                        @Override
                                        protected void cardsTargeted(int targetGroupId, Collection<PhysicalCard> targetedCards) {
                                            cardsRetargetedTo(targetGroupId, targetedCards);
                                        }
                                    });
                        }
                    }
                }
        );
        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }

    /**
     * This method is called when the cards to re-target to have been chosen.
     * @param targetGroupId the target group id
     * @param cardsRetargetedTo the cards to re-target to
     */
    public abstract void cardsRetargetedTo(int targetGroupId, Collection<PhysicalCard> cardsRetargetedTo);
}
