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
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.ChooseArbitraryCardsEffect;
import com.gempukku.swccgo.logic.effects.FireWeaponEffect;
import com.gempukku.swccgo.logic.effects.HitCardEffect;
import com.gempukku.swccgo.logic.effects.IonizeStarshipEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Combined Attack result: fire the chosen starship weapons one at a time at the shared target, collect destiny
 * DRAWS, then apply the added total separately for each weapon (AR per-weapon total modifiers) using each
 * weapon's own destinyDraws path (X-wing Laser Cannon can lose the target; Ion Cannon ionizes; others hit).
 *
 * Pending option A: costs paid as each shot initiates; if a later weapon cannot pay, skip it; completed
 * destinies still combine and still get applied to the weapons that did fire.
 *
 * After all destinies: if 2+ weapons completed, the player chooses which weapon result applies first.
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
                        game.getGameState().sendMessage(_source.getOwner() + " plays Combined Attack targeting "
                                + GameUtils.getCardLink(_target) + " with " + GameUtils.getAppendedNames(_weaponsInOrder));
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
                            subAction.appendEffect(
                                    new PlayoutDecisionEffect(subAction, _source.getOwner(),
                                            new YesNoDecision("Use " + GameUtils.getCardLink(targetingComputer)
                                                    + " to fire " + GameUtils.getCardLink(weapon)
                                                    + " twice as part of Combined Attack?") {
                                                @Override
                                                protected void yes() {
                                                    subAction.appendEffect(new UseDeviceEffect(subAction, targetingComputer));
                                                    subAction.appendEffect(
                                                            new PlayoutDecisionEffect(subAction, _source.getOwner(),
                                                                    new MultipleChoiceAwaitingDecision("Fire twice separately or combined?", new String[]{"Separately", "Combined"}) {
                                                                        @Override
                                                                        protected void validDecisionMade(int choiceIndex, String result) {
                                                                            final boolean combined = "Combined".equals(result);
                                                                            game.getGameState().beginSeparatelyOrCombinedFiring(targetingComputer, weapon, combined);
                                                                            game.getGameState().sendMessage(_source.getOwner() + " uses " + GameUtils.getCardLink(targetingComputer)
                                                                                    + " to fire " + GameUtils.getCardLink(weapon) + " twice " + result
                                                                                    + " as part of Combined Attack");
                                                                            subAction.appendEffect(createFireEffect(weapon, targetFilter, false));
                                                                            subAction.appendEffect(createFireEffect(weapon, targetFilter, true));
                                                                            subAction.appendEffect(
                                                                                    new PassthruEffect(subAction) {
                                                                                        @Override
                                                                                        protected void doPlayEffect(SwccgGame game) {
                                                                                            game.getGameState().finishSeparatelyOrCombinedFiring();
                                                                                        }
                                                                                    }
                                                                            );
                                                                            appendNextWeapon(subAction, game, index + 1);
                                                                        }
                                                                    })
                                                    );
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
     * After all fire: shared draw-sum applied separately per completed weapon via that weapon's own
     * destinyDraws path. Player chooses apply order when 2+ weapons completed.
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
        List<CombinedAttackFiringState.WeaponRecord> records =
                new ArrayList<CombinedAttackFiringState.WeaponRecord>(ca.getCompletedWeaponRecordsInOrder());
        ca.markResolved();
        promptApplyOrder(action, game, records, drawSum, target);
    }

    private void promptApplyOrder(final Action action, final SwccgGame game,
                                  final List<CombinedAttackFiringState.WeaponRecord> remaining,
                                  final float drawSum, final PhysicalCard target) {
        if (remaining.isEmpty()) {
            return;
        }
        if (remaining.size() == 1) {
            applyWeaponRecord(action, game, remaining.get(0), drawSum, target);
            return;
        }
        List<PhysicalCard> weapons = new ArrayList<PhysicalCard>();
        for (CombinedAttackFiringState.WeaponRecord record : remaining) {
            weapons.add(record.getWeapon());
        }
        action.appendEffect(
                new ChooseArbitraryCardsEffect(action, _source.getOwner(),
                        "Choose which weapon result applies first", weapons, 1, 1) {
                    @Override
                    protected void cardsSelected(SwccgGame game, Collection<PhysicalCard> selectedCards) {
                        PhysicalCard chosen = selectedCards.iterator().next();
                        CombinedAttackFiringState.WeaponRecord chosenRecord = null;
                        List<CombinedAttackFiringState.WeaponRecord> leftover =
                                new ArrayList<CombinedAttackFiringState.WeaponRecord>();
                        for (CombinedAttackFiringState.WeaponRecord record : remaining) {
                            if (chosenRecord == null && record.getWeapon().getCardId() == chosen.getCardId()) {
                                chosenRecord = record;
                            }
                            else {
                                leftover.add(record);
                            }
                        }
                        if (chosenRecord != null) {
                            applyWeaponRecord(action, game, chosenRecord, drawSum, target);
                        }
                        if (!leftover.isEmpty()) {
                            final List<CombinedAttackFiringState.WeaponRecord> leftoverFinal = leftover;
                            action.appendEffect(
                                    new PassthruEffect(action) {
                                        @Override
                                        protected void doPlayEffect(SwccgGame game) {
                                            promptApplyOrder(action, game, leftoverFinal, drawSum, target);
                                        }
                                    }
                            );
                        }
                    }
                }
        );
    }

    private void applyWeaponRecord(Action action, SwccgGame game, CombinedAttackFiringState.WeaponRecord record,
                                   float drawSum, PhysicalCard target) {
        PhysicalCard weapon = record.getWeapon();
        float total = Math.max(0, drawSum + record.getTotalModifierSnapshot());
        game.getGameState().sendMessage(CombinedAttackFiringState.getPerWeaponTotalMessage(
                weapon, drawSum, record.getTotalModifierSnapshot(), total));
        boolean queued = false;
        if (record.getDrawDestinyEffect() != null) {
            queued = record.getDrawDestinyEffect().applyDeferredWeaponResult(game, action, weapon,
                    record.getCardFiringWeapon(), record.getPermanentWeapon(), target,
                    record.getVariableXSnapshot(), total);
        }
        if (!queued) {
            float defenseValue = game.getModifiersQuerying().getDefenseValue(game.getGameState(), target);
            float valueForX = record.getVariableXSnapshot();
            if ((total + valueForX) > defenseValue) {
                game.getGameState().sendMessage("Result: Succeeded");
                if (weapon.getBlueprint().hasKeyword(Keyword.ION_CANNON)) {
                    action.appendEffect(new IonizeStarshipEffect(action, target, weapon, false, true, true));
                }
                else if (valueForX == 3f) {
                    action.appendEffect(new LoseCardFromTableEffect(action, target));
                }
                else {
                    action.appendEffect(new HitCardEffect(action, target, weapon,
                            record.getPermanentWeapon(), record.getCardFiringWeapon()));
                }
            }
            else {
                game.getGameState().sendMessage("Result: Failed");
            }
        }
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }
}
