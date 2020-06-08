package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.InactiveReason;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * An effect that causes the 'highest ability character' for Sense or Alter to be re-chosen if needed.
 */
public class RetargetHighestAbilityCharacterForSenseAlterIfNeededEffect extends AbstractSubActionEffect {
    private RespondablePlayingCardEffect _playCardEffect;

    /**
     * Creates an effect 'hit' a card.
     * @param action the action performing this effect
     * @param playCardEffect the play card effect for Sense or Alter
     */
    public RetargetHighestAbilityCharacterForSenseAlterIfNeededEffect(Action action, RespondablePlayingCardEffect playCardEffect) {
        super(action);
        _playCardEffect = playCardEffect;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        final GameState gameState = game.getGameState();

        final SubAction subAction = new SubAction(_action);
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        final Action targetingAction = _playCardEffect.getTargetingAction();
                        Map<Integer, Map<PhysicalCard, Set<TargetingReason>>> targetingMap = targetingAction.getAllPrimaryTargetCards();

                        for (Integer targetGroupId : targetingMap.keySet()) {
                            final Map<PhysicalCard, Set<TargetingReason>> targetingCardMap = targetingMap.get(targetGroupId);

                            for (PhysicalCard targetCard : targetingCardMap.keySet()) {
                                if (Filters.character.accepts(game, targetCard)) {
                                    String targetingText = targetingAction.getPrimaryTargetingText(targetGroupId);
                                    Map<InactiveReason, Boolean> spotOverrides = targetingAction.getPrimaryTargetSpotOverrides(targetGroupId);
                                    Map<TargetingReason, Filterable> targetFilter = targetingAction.getPrimaryTargetFilter(targetGroupId);
                                    Collection<PhysicalCard> validTargets = Filters.filterActive(game, targetingAction.getActionSource(), spotOverrides, targetFilter);

                                    // Check if current target is no longer a valid target
                                    if (!validTargets.contains(targetCard)) {
                                        // Remember the targeting reason
                                        final Set<TargetingReason> targetingReasons = targetingCardMap.get(targetCard);
                                        // Remove the card as a target
                                        targetingCardMap.remove(targetCard);

                                        if (!validTargets.isEmpty()) {
                                            gameState.sendMessage(targetingAction.getPerformingPlayer() + " must select new 'highest ability character' for " + GameUtils.getCardLink(targetingAction.getActionSource()));
                                            subAction.appendEffect(
                                                    new ChooseCardOnTableEffect(subAction, targetingAction.getPerformingPlayer(), targetingText, validTargets) {
                                                        @Override
                                                        protected void cardSelected(PhysicalCard newTarget) {
                                                            // Add new target
                                                            targetingCardMap.put(newTarget, targetingReasons);
                                                            gameState.sendMessage(targetingAction.getPerformingPlayer() + " selects " + GameUtils.getCardLink(newTarget) + " as 'highest ability character' for " + GameUtils.getCardLink(targetingAction.getActionSource()));
                                                            gameState.cardAffectsCard(targetingAction.getPerformingPlayer(), targetingAction.getActionSource(), newTarget);
                                                        }
                                                    });
                                        }
                                        else {
                                            gameState.sendMessage("No valid 'highest ability character' available for " + targetingAction.getPerformingPlayer() +  " to select for " + GameUtils.getCardLink(targetingAction.getActionSource()));
                                        }
                                    }
                                    break;
                                }
                            }
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
}
