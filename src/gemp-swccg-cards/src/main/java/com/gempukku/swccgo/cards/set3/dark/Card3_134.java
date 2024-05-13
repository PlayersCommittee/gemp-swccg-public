package com.gempukku.swccgo.cards.set3.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.RetargetWeaponEffect;
import com.gempukku.swccgo.cards.effects.SatisfyAllAttritionEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.WeaponFiringState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameModifierEffect;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfWeaponFiringActionProxyEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.TriggeringResultEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextForfeitModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.results.ResetOrModifyCardAttributeResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Hoth
 * Type: Interrupt
 * Subtype: Used
 * Title: Self-Destruct Mechanism
 */
public class Card3_134 extends AbstractUsedInterrupt {
    public Card3_134() {
        super(Side.DARK, 4, "Self-Destruct Mechanism", Uniqueness.UNRESTRICTED, ExpansionSet.HOTH, Rarity.U1);
        setLore("''Fraid there's not much left...I didn't hit it that hard. It musta had a self-destruct.'");
        setGameText("Lose a droid to cancel all attrition against you at same site this turn. OR Re-target an opponent's weapon to one of your droids at same site as target. If droid is 'hit', use original target's forfeit number.");
        addIcons(Icon.HOTH);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, final EffectResult effectResult, PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.isResolvingBattleDamageAndAttrition(game, effectResult, playerId)
                && GameConditions.isAttritionRemaining(game, playerId)) {
            TargetingReason targetingReason = TargetingReason.TO_BE_LOST;
            Filter filter = Filters.and(Filters.your(self), Filters.droid, Filters.at(Filters.and(Filters.site, Filters.battleLocation)));
            if (GameConditions.canTarget(game, self, targetingReason, filter)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Lose droid to cancel all attrition");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose droid", targetingReason, filter) {
                            @Override
                            protected void cardTargeted(int targetGroupId, PhysicalCard targetedCard) {
                                // Pay cost(s)
                                action.appendCost(
                                        new LoseCardFromTableEffect(action, targetedCard, true));
                                // Allow response(s)
                                action.allowResponses("Cancel all attrition",
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Perform result(s)
                                                game.getGameState().getBattleState().getCurrentLoseOrForfeitEffect().setFulfilledByOtherAction();
                                                action.appendEffect(
                                                        new SatisfyAllAttritionEffect(action, playerId));
                                            }
                                        }
                                );
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(final String playerId, SwccgGame game, final Effect effect, final PhysicalCard self) {
        final int gameTextSourceCardId = self.getCardId();

        // Check condition(s)
        if (TriggerConditions.isTargetedByWeapon(game, effect, Filters.your(self), Filters.and(Filters.opponents(self), Filters.weapon))) {
            WeaponFiringState weaponFiringState = game.getGameState().getWeaponFiringState();
            final Collection<PhysicalCard> originalTargets = weaponFiringState.getTargets();
            Filter retargetFilter = Filters.none;
            for (PhysicalCard originalTarget : originalTargets) {
                retargetFilter = Filters.or(retargetFilter, Filters.and(Filters.atSameSite(originalTarget), Filters.weaponMayRetargetTo(originalTarget)));
            }
            final PhysicalCard weapon = weaponFiringState.getPermanentWeaponFiring() != null ? weaponFiringState.getCardFiringWeapon() : weaponFiringState.getCardFiring();
            Filter droidFilter = Filters.and(Filters.your(self), Filters.droid, retargetFilter);
            if (GameConditions.canTarget(game, self, droidFilter)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Re-target weapon to droid");
                // Choose target(s)
                action.appendTargeting(
                        new ChooseCardOnTableEffect(action, playerId, "Choose target to re-target from", Filters.in(originalTargets)) {
                            @Override
                            protected void cardSelected(final PhysicalCard originalTarget) {
                                Filter filter = Filters.and(Filters.your(self), Filters.droid, Filters.atSameSite(originalTarget), Filters.weaponMayRetargetTo(originalTarget));
                                // Choose target(s)
                                action.appendTargeting(
                                        new TargetCardOnTableEffect(action, playerId, "Choose droid to re-target to", filter) {
                                            @Override
                                            protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                                                action.addAnimationGroup(targetedCard);
                                                // Allow response(s)
                                                action.allowResponses("Re-target " + GameUtils.getCardLink(weapon) + " to " + GameUtils.getCardLink(targetedCard),
                                                        new RespondablePlayCardEffect(action) {
                                                            @Override
                                                            protected void performActionResults(Action targetingAction) {
                                                                // Get the targeted card(s) from the action using the targetGroupId.
                                                                // This needs to be done in case the target(s) were changed during the responses.
                                                                final PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                                                // Perform result(s)
                                                                action.appendEffect(
                                                                        new RetargetWeaponEffect(action, originalTarget, finalTarget));
                                                                // Add proxy action for if target is 'hit' during this weapon firing
                                                                action.appendEffect(
                                                                        new AddUntilEndOfWeaponFiringActionProxyEffect(action,
                                                                                new AbstractActionProxy() {
                                                                                    @Override
                                                                                    public List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult effectResult) {
                                                                                        List<TriggerAction> actions = new LinkedList<TriggerAction>();

                                                                                        // Check condition(s)
                                                                                        if (TriggerConditions.justHit(game, effectResult, finalTarget)) {
                                                                                            final float originalForfeitNumber = game.getModifiersQuerying().getForfeit(game.getGameState(), originalTarget);

                                                                                            final RequiredGameTextTriggerAction action2 = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                                                                                            action2.setText("Reset forfeit to " + GuiUtils.formatAsString(originalForfeitNumber));
                                                                                            action2.setText("Use forfeit value of " + GuiUtils.formatAsString(originalForfeitNumber));
                                                                                            if (GameConditions.isDuringBattle(game)) {
                                                                                                action2.appendEffect(new AddUntilEndOfBattleModifierEffect(action2,
                                                                                                        new DefinedByGameTextForfeitModifier(self, finalTarget, originalForfeitNumber), null));
                                                                                            }
                                                                                            else {
                                                                                                action2.appendEffect(new AddUntilEndOfGameModifierEffect(action2,
                                                                                                        new DefinedByGameTextForfeitModifier(self, finalTarget, originalForfeitNumber), null));
                                                                                            }
                                                                                            action2.appendEffect(new TriggeringResultEffect(action2,
                                                                                                    new ResetOrModifyCardAttributeResult(_action.getPerformingPlayer(), finalTarget)));
                                                                                        }
                                                                                        return actions;
                                                                                    }
                                                                                }
                                                                        )
                                                                );
                                                            }
                                                        }
                                                );
                                            }
                                        }
                                );
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}