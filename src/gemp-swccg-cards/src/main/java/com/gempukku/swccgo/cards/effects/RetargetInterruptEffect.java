package com.gempukku.swccgo.cards.effects;

import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * An effect that re-targets the Interrupt to new targets.
 */
public class RetargetInterruptEffect extends AbstractSuccessfulEffect {
    private Action _targetingAction;
    private Collection<PhysicalCard> _oldTargets;
    private Collection<PhysicalCard> _newTargets;

    /**
     * Creates an effect that re-targets the Interrupt to new target.
     * @param action the action performing this effect
     * @param targetingAction the targeting action
     * @param oldTargets the old targets
     * @param newTargets the new targets
     */
    public RetargetInterruptEffect(Action action, Action targetingAction, Collection<PhysicalCard> oldTargets, Collection<PhysicalCard> newTargets) {
        super(action);
        _targetingAction = targetingAction;
        _oldTargets = oldTargets;
        _newTargets = newTargets;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();
        Map<Integer, Map<PhysicalCard, Set<TargetingReason>>> targetingMap = _targetingAction.getAllPrimaryTargetCards();
        boolean newTargetsTargeted = false;

        for (Integer targetGroupId : targetingMap.keySet()) {
            final Map<PhysicalCard, Set<TargetingReason>> targetingCardMap = targetingMap.get(targetGroupId);
            final Set<TargetingReason> allTargetingReasons = new HashSet<TargetingReason>();
            boolean foundOldTarget = false;

            // Remove the old cards as targets (and gather targeting reasons)
            for (PhysicalCard oldTarget : _oldTargets) {
                if (targetingCardMap.containsKey(oldTarget)) {
                    foundOldTarget = true;
                    Set<TargetingReason> targetingReasons = targetingCardMap.get(oldTarget);
                    if (targetingReasons != null) {
                        allTargetingReasons.addAll(targetingReasons);
                    }
                    targetingCardMap.remove(oldTarget);
                }
            }

            if (foundOldTarget && !newTargetsTargeted) {
                newTargetsTargeted = true;
                // Add new targets
                for (PhysicalCard newTarget : _newTargets) {
                    targetingCardMap.put(newTarget, new HashSet<TargetingReason>(allTargetingReasons));
                }
                gameState.sendMessage(_action.getPerformingPlayer() + " re-targets " + GameUtils.getCardLink(_targetingAction.getActionSource()) + " to target " + GameUtils.getAppendedNames(_newTargets) + " using " + GameUtils.getCardLink(_action.getActionSource()));
                gameState.cardAffectsCards(_action.getPerformingPlayer(), _targetingAction.getActionSource(), _newTargets);
            }
        }
    }
}
