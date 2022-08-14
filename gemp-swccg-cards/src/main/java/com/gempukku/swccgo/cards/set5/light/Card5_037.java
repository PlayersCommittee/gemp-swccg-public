package com.gempukku.swccgo.cards.set5.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleActionProxyEffect;
import com.gempukku.swccgo.logic.effects.ReleaseCaptiveEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Cloud City
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Captive Pursuit
 */
public class Card5_037 extends AbstractUsedOrLostInterrupt {
    public Card5_037() {
        super(Side.LIGHT, 4, Title.Captive_Pursuit);
        setLore("Han could have used a hand. Fortunately, Luke still had one to give.");
        setGameText("USED: If a battle was just initiated at a site, target a captive present. Captive released if you win battle. LOST: Cancel Aiiii! Aaa! Aggggggggggggg!, Sonic Bombardment, Interrogation Array, This Is Some Rescue or Special Delivery.");
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        final int gameTextSourceCardId = self.getCardId();
        Filter captiveFilter = Filters.and(Filters.captive, Filters.presentAt(Filters.battleLocation));

        // Check condition(s)
        if (TriggerConditions.battleInitiatedAt(game, effectResult, Filters.site)
                && GameConditions.canTarget(game, self, SpotOverride.INCLUDE_CAPTIVE, captiveFilter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Target captive");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose character", SpotOverride.INCLUDE_CAPTIVE, captiveFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard captive) {
                            action.addAnimationGroup(captive);
                            // Allow response(s)
                            action.allowResponses("Target " + GameUtils.getCardLink(captive),
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            final PhysicalCard finalTarget = targetingAction.getPrimaryTargetCard(targetGroupId);

                                            // Perform result(s)
                                            final int permCardId = self.getPermanentCardId();
                                            action.appendEffect(
                                                    new AddUntilEndOfBattleActionProxyEffect(action,
                                                            new AbstractActionProxy() {
                                                                @Override
                                                                public List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult effectResult) {
                                                                    List<TriggerAction> actions = new LinkedList<TriggerAction>();
                                                                    final PhysicalCard self = game.findCardByPermanentId(permCardId);

                                                                    // Check condition(s)
                                                                    if (TriggerConditions.wonBattle(game, effectResult, playerId)
                                                                            && Filters.captive.accepts(game, finalTarget)) {

                                                                        final RequiredGameTextTriggerAction action2 = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                                                                        action2.setText("Release " + GameUtils.getFullName(finalTarget));
                                                                        // Build action using common utility
                                                                        action2.appendEffect(
                                                                                new ReleaseCaptiveEffect(action2, finalTarget));
                                                                        actions.add(action2);
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
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        Filter filter = Filters.or(Filters.Aiiii_Aaa_Agggggggggg, Filters.Sonic_Bombardment, Filters.Interrogation_Array, Filters.This_Is_Some_Rescue, Filters.Special_Delivery);

        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, filter)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Aiiii_Aaa_Agggggggggg)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Aiiii_Aaa_Agggggggggg, Title.Aiiii_Aaa_Agggggggggg);
            actions.add(action);
        }
        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Sonic_Bombardment)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Sonic_Bombardment, Title.Sonic_Bombardment);
            actions.add(action);
        }
        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Interrogation_Array)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Interrogation_Array, Title.Interrogation_Array);
            actions.add(action);
        }
        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.This_Is_Some_Rescue)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.This_Is_Some_Rescue, Title.This_Is_Some_Rescue);
            actions.add(action);
        }
        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Special_Delivery)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Special_Delivery, Title.Special_Delivery);
            actions.add(action);
        }
        return actions;
    }
}