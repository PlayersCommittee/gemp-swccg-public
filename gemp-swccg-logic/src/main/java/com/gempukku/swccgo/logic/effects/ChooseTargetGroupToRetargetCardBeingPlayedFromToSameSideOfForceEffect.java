package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.TargetingEffect;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * An abstract class for choosing a card group (by selecting a card in that group) that the card being played is to be
 * re-targeted from.
 */
public abstract class ChooseTargetGroupToRetargetCardBeingPlayedFromToSameSideOfForceEffect extends AbstractSubActionEffect implements TargetingEffect {
    private final RespondablePlayingCardEffect _playingCardEffect;

    /**
     * Creates an effect to choose a card group (by selecting a card in that group) that the card being played is to be
     * re-targeted from.
     * @param action the action performing this effect
     * @param playingCardEffect the playing card effect
     */
    public ChooseTargetGroupToRetargetCardBeingPlayedFromToSameSideOfForceEffect(Action action, RespondablePlayingCardEffect playingCardEffect) {
        super(action);
        _playingCardEffect = playingCardEffect;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(final SwccgGame game) {
        final PhysicalCard sourceCard = _action.getActionSource();
        final String performingPlayerId = _action.getPerformingPlayer();
        final Action parentAction = _action;

        final SubAction subAction = new SubAction(_action);
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        Collection<PhysicalCard> cardsToRetargetFrom = Filters.filterAllOnTable(game, Filters.cardThatCardBeingPlayedCanBeRetargetedToSameSideOfForceFrom(sourceCard));
                        parentAction.appendTargeting(
                                new ChooseCardOnTableEffect(parentAction, performingPlayerId, "Choose card (or card in group) to re-target from", cardsToRetargetFrom) {
                                    @Override
                                    protected void cardSelected(PhysicalCard selectedCard) {
                                        Map<Integer, Map<PhysicalCard, Set<TargetingReason>>> targetCardsMap = _playingCardEffect.getTargetingAction().getAllPrimaryTargetCards();
                                        for (Integer targetGroupId : targetCardsMap.keySet()) {
                                            if (targetCardsMap.get(targetGroupId).containsKey(selectedCard)) {
                                                groupChosenToRetarget(targetGroupId);
                                                return;
                                            }
                                        }
                                    }
                                }
                        );
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
     * This method is called when the group to re-target has been chosen.
     * @param groupIdToRetarget the id of the group to re-target
     */
    public abstract void groupChosenToRetarget(int groupIdToRetarget);
}
