package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.TargetId;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.TargetingEffect;

import java.util.Collection;
import java.util.Map;

/**
 * An abstract class for choosing a card group (by selecting a card in that group) that the specified Effect is to be
 * re-targeted from.
 */
public abstract class ChooseTargetGroupToRetargetEffectFromToSameSideOfForceEffect extends AbstractSubActionEffect implements TargetingEffect {
    private final PhysicalCard _effectToRetarget;

    /**
     * Creates an effect to choose a card group (by selecting a card in that group) that the specified Effect is to be
     * re-targeted from.
     * @param action the action performing this effect
     * @param effectToRetarget the Effect to re-target
     */
    public ChooseTargetGroupToRetargetEffectFromToSameSideOfForceEffect(Action action, PhysicalCard effectToRetarget) {
        super(action);
        _effectToRetarget = effectToRetarget;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(final SwccgGame game) {
        final GameState gameState = game.getGameState();
        final PhysicalCard sourceCard = _action.getActionSource();
        final String performingPlayerId = _action.getPerformingPlayer();
        final Action parentAction = _action;

        final SubAction subAction = new SubAction(_action);
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        Collection<PhysicalCard> cardsToRetargetFrom = Filters.filterAllOnTable(game, Filters.cardThatEffectCanBeRetargetedToSameSideOfForceFrom(sourceCard, Filters.samePermanentCardId(_effectToRetarget)));
                        parentAction.appendTargeting(
                                new ChooseCardOnTableEffect(parentAction, performingPlayerId, "Choose card to re-target from", cardsToRetargetFrom) {
                                    @Override
                                    protected void cardSelected(PhysicalCard selectedCard) {
                                        Map<TargetId, PhysicalCard> targetCardsMap = _effectToRetarget.getTargetedCards(gameState);
                                        for (TargetId targetId : targetCardsMap.keySet()) {
                                            if (targetCardsMap.get(targetId).getPermanentCardId() == selectedCard.getPermanentCardId()) {
                                                targetToRetarget(targetId);
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
     * This method is called when the target to re-target has been chosen.
     * @param targetIdToRetarget the id of the target to re-target
     */
    public abstract void targetToRetarget(TargetId targetIdToRetarget);
}
