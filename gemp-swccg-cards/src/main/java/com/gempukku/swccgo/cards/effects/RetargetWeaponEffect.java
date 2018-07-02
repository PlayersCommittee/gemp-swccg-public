package com.gempukku.swccgo.cards.effects;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.game.state.WeaponFiringState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.effects.RespondableWeaponFiringEffect;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * An effect that re-targets the currently targeting weapon to a new target.
 */
public class RetargetWeaponEffect extends AbstractSuccessfulEffect {
    private PhysicalCard _oldTarget;
    private PhysicalCard _newTarget;

    /**
     * Creates an effect that re-targets the currently targeting weapon a new target.
     * @param action the action performing this effect
     * @param oldTarget the old target
     * @param newTarget the new target
     */
    public RetargetWeaponEffect(Action action, PhysicalCard oldTarget, PhysicalCard newTarget) {
        super(action);
        _oldTarget = oldTarget;
        _newTarget = newTarget;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();
        WeaponFiringState weaponFiringState = gameState.getWeaponFiringState();
        if (weaponFiringState != null) {
            RespondableWeaponFiringEffect respondableWeaponFiringEffect = (RespondableWeaponFiringEffect) weaponFiringState.getWeaponFiringEffect();
            if (!respondableWeaponFiringEffect.isCanceled()) {

                final Action targetingAction = respondableWeaponFiringEffect.getTargetingAction();
                Map<Integer, Map<PhysicalCard, Set<TargetingReason>>> targetingMap = targetingAction.getAllPrimaryTargetCards();

                for (Integer targetGroupId : targetingMap.keySet()) {
                    final Map<PhysicalCard, Set<TargetingReason>> targetingCardMap = targetingMap.get(targetGroupId);
                    final Set<TargetingReason> targetingReasons = targetingCardMap.get(_oldTarget);
                    if (targetingReasons != null) {

                        // Remove the card as a target
                        targetingCardMap.remove(_oldTarget);
                        // Determine targeting reasons for new target
                        Map<TargetingReason, Filterable> targetFilterMap = targetingAction.getPrimaryTargetFilter(targetGroupId);
                        Set<TargetingReason> newTargetingReasons = new HashSet<TargetingReason>();
                        for (TargetingReason targetingReason : targetFilterMap.keySet()) {
                            if (Filters.and(targetFilterMap.get(targetingReason)).acceptsIgnoringOwner(gameState, game.getModifiersQuerying(), _newTarget)) {
                                newTargetingReasons.add(targetingReason);
                            }
                        }
                        // Add new target
                        targetingCardMap.put(_newTarget, newTargetingReasons);
                        weaponFiringState.replaceTarget(_oldTarget, _newTarget);
                        PhysicalCard cardFiring = weaponFiringState.getPermanentWeaponFiring() != null ? weaponFiringState.getCardFiringWeapon() : weaponFiringState.getCardFiring();
                        gameState.sendMessage(_action.getPerformingPlayer() + " re-targets " + GameUtils.getCardLink(cardFiring) + " to target " + GameUtils.getCardLink(_newTarget) + " using " + GameUtils.getCardLink(_action.getActionSource()));
                        break;
                    }
                }
            }
        }
    }
}
