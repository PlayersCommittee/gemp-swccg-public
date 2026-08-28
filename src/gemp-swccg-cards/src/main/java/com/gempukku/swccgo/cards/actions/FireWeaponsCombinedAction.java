package com.gempukku.swccgo.cards.actions;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.UseDeviceEffect;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.CombinedAttackFiringState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.FireWeaponEffect;
import com.gempukku.swccgo.logic.effects.HitCardEffect;
import com.gempukku.swccgo.logic.effects.IonizeStarshipEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Combined Attack result: fire the chosen starship weapons one at a time at the shared target, collect destiny
 * DRAWS, then apply the added total separately for each weapon (AR per-weapon total modifiers).
 *
 * The old stub applied one combined total-modifier pass and reused that number for every weapon. That is weaker
 * than AR (Heavy Turbolaser -6 vs starfighter must apply only when resolving HT). This rewrite prefers AR:
 * shared draw-sum + each weapon's own total modifiers, applied after all fire (Gergall).
 *
 * Pending option A: costs paid as each shot initiates; if a later weapon cannot pay, skip it; completed
 * destinies still combine and still get applied to the weapons that did fire.
 */
public class FireWeaponsCombinedAction extends AbstractSubActionEffect {
    private final PhysicalCard _source;
    private final List<PhysicalCard> _weaponsInOrder;
    private final PhysicalCard _target;

    public FireWeaponsCombinedAction(Action action, PhysicalCard source, List<PhysicalCard> weaponsInOrder, PhysicalCard target) {
        super(action);
        _source = source;
        _weaponsInOrder = weaponsInOrder;
        _target = target;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(final SwccgGame game) {
        final SubAction subAction = new SubAction(_action);
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        game.getGameState().beginCombinedAttackFiring(_source, _target, _weaponsInOrder);
                        appendNextWeapon(subAction, game, 0);
                    }
                }
        );
        return subAction;
    }

    private void appendNextWeapon(final SubAction subAction, final SwccgGame game, final int index) {
        if (index >= _weaponsInOrder.size()) {
            subAction.appendEffect(
                    new PassthruEffect(subAction) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            resolveCombinedAttack(subAction, game);
                            game.getGameState().finishCombinedAttackFiring();
                        }
                    }
            );
            return;
        }

        final PhysicalCard weapon = _weaponsInOrder.get(index);
        final Filter targetFilter = Filters.sameCardId(_target);
        final PhysicalCard targetingComputer = findUsableTargetingComputer(game, weapon);

        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        if (targetingComputer != null) {
                            // Both TC shots consecutive and both inside Combined Attack (forum p=1111897).
                            // Cannot split one TC shot in CA and one outside.
                            subAction.appendEffect(
                                    new PlayoutDecisionEffect(subAction, _source.getOwner(),
                                            new YesNoDecision("Use " + GameUtils.getCardLink(targetingComputer)
                                                    + " to fire " + GameUtils.getCardLink(weapon)
                                                    + " twice as part of Combined Attack?") {
                                                @Override
                                                protected void yes() {
                                                    subAction.appendEffect(new UseDeviceEffect(subAction, targetingComputer));
                                                    subAction.appendEffect(createFireEffect(weapon, targetFilter, false));
                                                    subAction.appendEffect(createFireEffect(weapon, targetFilter, true));
                                                    appendNextWeapon(subAction, game, index + 1);
                                                }

                                                @Override
                                                protected void no() {
                                                    subAction.appendEffect(createFireEffect(weapon, targetFilter, false));
                                                    appendNextWeapon(subAction, game, index + 1);
                                                }
                                            })
                            );
                        }
                        else {
                            subAction.appendEffect(createFireEffect(weapon, targetFilter, false));
                            appendNextWeapon(subAction, game, index + 1);
                        }
                    }
                }
        );
    }

    private FireWeaponEffect createFireEffect(PhysicalCard weapon, Filter targetFilter, final boolean ignorePerBattleLimit) {
        return new FireWeaponEffect(_action, weapon, false, targetFilter) {
            @Override
            protected boolean isignorePerAttackOrBattleLimit() {
                return ignorePerBattleLimit;
            }
        };
    }

    /**
     * Targeting Computer on the starship firing this weapon, unused this battle, and currently usable.
     */
    private PhysicalCard findUsableTargetingComputer(SwccgGame game, PhysicalCard weapon) {
        PhysicalCard starship = weapon.getAttachedTo();
        if (starship == null && Filters.starship.accepts(game, weapon)) {
            starship = weapon;
        }
        if (starship == null) {
            return null;
        }
        Collection<PhysicalCard> computers = Filters.filterActive(game, _source,
                Filters.and(Filters.title(Title.Targeting_Computer), Filters.attachedTo(starship)));
        for (PhysicalCard tc : computers) {
            if (GameConditions.canUseDevice(game, tc)
                    && GameConditions.isOncePerBattle(game, tc, tc.getCardId())) {
                return tc;
            }
        }
        return null;
    }

    /**
     * After all fire: shared draw-sum applied separately per completed weapon, with that weapon's snapshotted
     * total modifiers. Hits/ionize happen now, not during the individual destiny draws.
     */
    private void resolveCombinedAttack(Action action, SwccgGame game) {
        CombinedAttackFiringState ca = game.getGameState().getCombinedAttackFiringState();
        if (ca == null || ca.isResolved() || ca.getCompletedDrawCount() == 0) {
            return;
        }
        PhysicalCard target = ca.getTarget();
        if (target == null) {
            return;
        }
        float drawSum = ca.getDrawSum();
        game.getGameState().sendMessage(ca.getAddedDestiniesMessage(GuiUtils.formatAsString(drawSum)));
        float defenseValue = game.getModifiersQuerying().getDefenseValue(game.getGameState(), target);
        game.getGameState().sendMessage("Defense value: " + GuiUtils.formatAsString(defenseValue));

        for (CombinedAttackFiringState.WeaponRecord record : ca.getCompletedWeaponRecordsInOrder()) {
            PhysicalCard weapon = record.getWeapon();
            float total = Math.max(0, drawSum + record.getTotalModifierSnapshot());
            game.getGameState().sendMessage(ca.getPerWeaponTotalMessage(weapon, GuiUtils.formatAsString(total)));
            if (total > defenseValue) {
                game.getGameState().sendMessage("Result for " + GameUtils.getCardLink(weapon) + ": Succeeded");
                if (weapon.getBlueprint().hasKeyword(Keyword.ION_CANNON)) {
                    action.appendEffect(new IonizeStarshipEffect(action, target, weapon, false, true, true));
                }
                else {
                    action.appendEffect(new HitCardEffect(action, target, weapon, record.getPermanentWeapon(), record.getCardFiringWeapon()));
                }
            }
            else {
                game.getGameState().sendMessage("Result for " + GameUtils.getCardLink(weapon) + ": Failed");
            }
        }
        ca.markResolved();
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }
}
