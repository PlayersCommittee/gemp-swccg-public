package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.InactiveReason;
import com.gempukku.swccgo.common.TargetId;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.TargetingEffect;

import java.util.Collection;
import java.util.Map;

/**
 * An abstract class for choosing card that the Effect is to be re-targeted from the target to a card that is on the same
 * side of the Force.
 */
public abstract class TargetTargetOnSameSideOfForceToRetargetEffectToEffect extends AbstractSubActionEffect implements TargetingEffect {
    private final PhysicalCard _effectToRetarget;
    private final TargetId _targetIdToRetarget;

    /**
     * Creates an effect to choose cards that the card being played is to be re-targeted from the specified group to cards
     * that are the same side of the Force.
     * @param action the action performing this effect
     * @param effectToRetarget the Effect to re-target
     * @param targetId the target id of the target to re-target
     */
    public TargetTargetOnSameSideOfForceToRetargetEffectToEffect(Action action, PhysicalCard effectToRetarget, TargetId targetId) {
        super(action);
        _effectToRetarget = effectToRetarget;
        _targetIdToRetarget = targetId;
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
                        final Collection<PhysicalCard> cardsToRetargetTo = Filters.filterAllOnTable(game, Filters.cardThatEffectCanBeRetargetedToSameSideOfForceTo(sourceCard, _effectToRetarget, _targetIdToRetarget));
                        Map<InactiveReason, Boolean> spotOverrides = _effectToRetarget.getBlueprint().getTargetSpotOverride(_targetIdToRetarget);
                        parentAction.appendTargeting(
                                new TargetCardOnTableEffect(parentAction, performingPlayerId, "Choose card to re-target to", spotOverrides, Filters.in(cardsToRetargetTo)) {
                                    @Override
                                    protected void cardTargeted(int targetGroupId, PhysicalCard targetedCard) {
                                        cardRetargetedTo(targetGroupId, targetedCard);
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
     * This method is called when the card to re-target to has been chosen.
     * @param targetGroupId the target group id
     * @param cardRetargetedTo the card to re-target to
     */
    public abstract void cardRetargetedTo(int targetGroupId, PhysicalCard cardRetargetedTo);
}
