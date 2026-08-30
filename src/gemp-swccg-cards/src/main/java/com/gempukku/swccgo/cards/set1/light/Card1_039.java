package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractDevice;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.UseDeviceEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.game.state.SeparatelyOrCombinedFiringState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.FireWeaponAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.FireWeaponEffect;
import com.gempukku.swccgo.logic.effects.HitCardEffect;
import com.gempukku.swccgo.logic.effects.IonizeStarshipEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextDeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.EachWeaponDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.ManeuverModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.AbstractStandardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.TargetingEffect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premiere
 * Type: Device
 * Title: Targeting Computer
 */
public class Card1_039 extends AbstractDevice {
    public Card1_039() {
        super(Side.LIGHT, 3, PlayCardZoneOption.ATTACHED, Title.Targeting_Computer, Uniqueness.UNRESTRICTED, ExpansionSet.PREMIERE, Rarity.U1);
        setLore("Specially designed for use on Rebel starfighters. Assists pilots on torpedo runs. Automatically locks on pre-programmed target points.");
        setGameText("Use 2 Force to deploy on any starship. Adds 1 to starship's maneuver. If this starship is using a weapon during a battle, you may fire that weapon twice, separately or combined. Subtract 1 from each destiny draw when firing.");
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextDeployCostModifier(self, 2));
        return modifiers;
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        // "any starship" = any of your starships (fighter or capital), not opponent's
        return Filters.and(Filters.your(self), Filters.starship);
    }

    @Override
    protected Filter getGameTextValidToUseDeviceFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.starship;
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedTo(final SwccgGame game, final PhysicalCard self) {
        return Filters.starship;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter hasAttached = Filters.hasAttached(self);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ManeuverModifier(self, hasAttached, 1));
        // Subtract 1 from each destiny draw only while this copy is used to fire a weapon twice.
        // Attached but unused Targeting Computer does not modify ordinary weapon fire.
        Condition usingThisDeviceToFire = new Condition() {
            @Override
            public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
                SeparatelyOrCombinedFiringState soc = gameState.getSeparatelyOrCombinedFiringState();
                return soc != null && soc.getDevice() != null && soc.getDevice().getCardId() == self.getCardId();
            }
        };
        modifiers.add(new EachWeaponDestinyModifier(self, Filters.any, usingThisDeviceToFire, hasAttached, -1));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        final Filter weaponFilter = Filters.and(
                Filters.your(self),
                Filters.weapon,
                Filters.attachedTo(Filters.and(Filters.hasAttached(self), Filters.participatingInBattle)),
                Filters.canBeFired(self, 0, Filters.canBeTargetedBy(self)));

        // Only usable during battle (forum p=1195641). Each copy is limited to one usage per battle (forum p=961994).
        if (GameConditions.isDuringBattle(game)
                && GameConditions.isOncePerBattle(game, self, gameTextSourceCardId)
                && GameConditions.canUseDevice(game, self)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.hasAttached(self))
                && GameConditions.canSpot(game, self, weaponFilter)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Fire a weapon twice");
            action.setActionMsg("use " + GameUtils.getCardLink(self) + " to fire a weapon twice");
            // OncePerBattle / UseDevice wait until Separately or Combined so Don't Fire can abort unused.
            action.appendTargeting(
                    new ChooseCardOnTableEffect(action, playerId, "Choose weapon to fire twice", weaponFilter) {
                        @Override
                        protected boolean getUseShortcut() {
                            // Auto-select the only legal weapon even though top-level actions allow abort.
                            return true;
                        }
                        @Override
                        protected void cardSelected(final PhysicalCard weapon) {
                            action.appendTargeting(
                                    new FireTwiceModeEffect(action, playerId) {
                                        @Override
                                        protected void modeChosen(String result) {
                                            if (result != null && result.startsWith("Don't Fire")) {
                                                action.setActionMsg(null);
                                                cancel();
                                                return;
                                            }
                                            final boolean combined = "Combined".equals(result);
                                            action.appendUsage(
                                                    new OncePerBattleEffect(action));
                                            action.appendUsage(
                                                    new UseDeviceEffect(action, self));
                                            game.getGameState().beginSeparatelyOrCombinedFiring(self, weapon, combined);
                                            final String mode = result.toLowerCase();
                                            action.appendEffect(
                                                    new PassthruEffect(action) {
                                                        @Override
                                                        protected void doPlayEffect(SwccgGame game) {
                                                            game.getGameState().sendMessage(playerId + " uses " + GameUtils.getCardLink(self)
                                                                    + " to fire " + GameUtils.getCardLink(weapon) + " twice: " + mode);
                                                        }
                                                    }
                                            );
                                            appendFireTwice(action, game, self, weapon, combined);
                                        }
                                    }
                            );
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    /**
     * Fires the chosen weapon twice as sub-actions of this device action (no top-level action between shots).
     * Appendix B option A: Force is used for BOTH firings, but costs are paid as EACH shot initiates
     * (not all upfront). After shot 1 completes, attempt shot 2; if the fire action cannot be built
     * (typically remaining Force cannot pay that shot's cost), skip shot 2. Do not rewind shot 1 and
     * do not fail the overall action. Combined: destinies from completed firings still combine, with
     * total-modifiers applied once vs the single target.
     */
    private void appendFireTwice(final TopLevelGameTextAction action, final SwccgGame game, final PhysicalCard self,
                                 final PhysicalCard weapon, final boolean combined) {
        action.appendEffect(createFireTwiceShotEffect(action, weapon, Filters.any));
        action.appendEffect(
                new PassthruEffect(action) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        Filter secondTargetFilter = Filters.any;
                        SeparatelyOrCombinedFiringState soc = game.getGameState().getSeparatelyOrCombinedFiringState();
                        if (combined && soc != null && soc.getCombinedTarget() != null) {
                            secondTargetFilter = Filters.sameCardId(soc.getCombinedTarget());
                        }
                        // Same check FireWeaponEffect uses: builder returns null when this shot cannot initiate.
                        // Nested FireWeaponEffect already no-ops in that case (does not abort the parent);
                        // skip appending shot 2 so combined can resolve from completed firings only.
                        if (!canInitiateSocShot(game, self, weapon, secondTargetFilter)) {
                            game.getGameState().sendMessage("Second firing does not initiate (cannot pay fire cost). Completed firings still count.");
                            resolveCombinedIfStillPending(action, game, weapon);
                            game.getGameState().finishSeparatelyOrCombinedFiring();
                            return;
                        }
                        action.appendEffect(createFireTwiceShotEffect(action, weapon, secondTargetFilter));
                        action.appendEffect(
                                new PassthruEffect(action) {
                                    @Override
                                    protected void doPlayEffect(SwccgGame game) {
                                        resolveCombinedIfStillPending(action, game, weapon);
                                        game.getGameState().finishSeparatelyOrCombinedFiring();
                                    }
                                }
                        );
                    }
                }
        );
    }

    /**
     * True if the weapon can currently begin a fire-weapon action for this SOC shot
     * (Force remaining, legal target, per-battle limit ignored).
     */
    private boolean canInitiateSocShot(SwccgGame game, PhysicalCard self, PhysicalCard weapon, Filter fireAtTargetFilter) {
        FireWeaponAction fireWeaponAction = weapon.getBlueprint().getFireWeaponAction(
                weapon.getOwner(), game, weapon, false, 0, self, false,
                Filters.none, null, fireAtTargetFilter, true);
        return fireWeaponAction != null;
    }

    private FireWeaponEffect createFireTwiceShotEffect(Action action, PhysicalCard weapon, Filter fireAtTargetFilter) {
        return new FireWeaponEffect(action, weapon, false, fireAtTargetFilter) {
            @Override
            protected boolean isignorePerAttackOrBattleLimit() {
                // Second shot of the same overall action must ignore the weapon's once-per-battle firing.
                return true;
            }
        };
    }

    /**
     * If combined firing stored destinies but never resolved (second shot unpaid/canceled), apply total modifiers
     * once to the completed firings and resolve against the stored target.
     */
    private void resolveCombinedIfStillPending(Action action, SwccgGame game, PhysicalCard weapon) {
        SeparatelyOrCombinedFiringState soc = game.getGameState().getSeparatelyOrCombinedFiringState();
        if (soc == null || !soc.isCombined() || soc.isResolved() || soc.getCompletedFiringCount() == 0) {
            return;
        }
        PhysicalCard target = soc.getCombinedTarget();
        if (target == null) {
            return;
        }
        float combined = soc.getCombinedFiringDestinySum();
        float totalDestiny = game.getModifiersQuerying().getTotalWeaponDestiny(
                game.getGameState(), soc.getCardFiringWeapon(), weapon, null,
                Collections.singletonList(target), combined);
        soc.markResolved();
        float defenseValue = game.getModifiersQuerying().getDefenseValue(game.getGameState(), target);
        game.getGameState().sendMessage(soc.getCombinedDestiniesMathMessage(
                GuiUtils.formatAsString(combined), GuiUtils.formatAsString(totalDestiny),
                GuiUtils.formatAsString(defenseValue)));
        if (soc.getDrawDestinyEffect() != null) {
            soc.getDrawDestinyEffect().applyDeferredWeaponResult(game, action, weapon,
                    soc.getCardFiringWeapon(), null, target, soc.getVariableXSnapshot(), totalDestiny);
        }
        else if (totalDestiny > defenseValue) {
            game.getGameState().sendMessage("Result: Succeeded");
            if (weapon.getBlueprint().hasKeyword(Keyword.ION_CANNON)) {
                action.appendEffect(new IonizeStarshipEffect(action, target, weapon, false, true, true));
            }
            else {
                action.appendEffect(new HitCardEffect(action, target, weapon));
            }
        }
        else {
            game.getGameState().sendMessage("Result: Failed");
        }
    }

    /**
     * Targeting decision for Separately / Combined / Don't Fire.
     * Don't Fire fails this cost so the parent action aborts unused (same as clicking Done).
     */
    private abstract static class FireTwiceModeEffect extends AbstractStandardEffect implements TargetingEffect {
        private final String _playerId;
        private boolean _cancelled;

        private FireTwiceModeEffect(Action action, String playerId) {
            super(action);
            _playerId = playerId;
        }

        protected void cancel() {
            _cancelled = true;
        }

        protected abstract void modeChosen(String result);

        @Override
        public boolean isPlayableInFull(SwccgGame game) {
            return true;
        }

        @Override
        public boolean wasCarriedOut() {
            return super.wasCarriedOut() && !_cancelled;
        }

        @Override
        protected FullEffectResult playEffectReturningResult(SwccgGame game) {
            game.getUserFeedback().sendAwaitingDecision(_playerId,
                    new MultipleChoiceAwaitingDecision("Fire a weapon twice", new String[]{"Separately", "Combined", "Don't Fire (Cancel)"}, true) {
                        @Override
                        protected void validDecisionMade(int index, String result) {
                            modeChosen(result);
                        }
                    });
            return new FullEffectResult(true);
        }
    }
}
