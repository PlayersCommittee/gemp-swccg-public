package com.gempukku.swccgo.cards.set223.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfEpicEventModifierEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.StackOneCardFromHandEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.RestoreFreedomToTheGalaxyTotalModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/*
 * Set: Set 23
 * Type: Interrupt
 * Subtype: Used
 * Title: Probe Telemetry (V)
 */
public class Card223_021 extends AbstractUsedInterrupt {
    public Card223_021() {
        super(Side.DARK, 4, Title.Probe_Telemetry, Uniqueness.UNRESTRICTED, ExpansionSet.SET_23, Rarity.V);
        setLore("Probe droids use electromagnetic, seismic, acoustic, olfactory and optical sensors. They report their findings using an omnisignal unicode.");
        setGameText("If Systems Will Slip Through Your Fingers on table, may reveal from hand and place face down under a system to 'probe' there. Take a probe droid into hand from Reserve Deck; reshuffle. OR Cancel Alternatives To Fighting or It Can Wait. OR Subtract 3 from an attempt to 'liberate' a system.");
        addIcons(Icon.HOTH, Icon.VIRTUAL_SET_23);
        setVirtualSuffix(true);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelInHandActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        Filter systemsFilter = Filters.and(Filters.system, Filters.not(Filters.hasStacked(Filters.probeCard)));

        if (GameConditions.canSpot(game, self, Filters.Systems_Will_Slip_Through_Your_Fingers)
                && GameConditions.canSpotLocation(game, systemsFilter)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("'Probe' a system");
            action.setActionMsg("Reveal to probe for opponent's Hidden Base");
            // Perform result(s)
            action.appendTargeting(
                new TargetCardOnTableEffect(action, playerId, "Choose system to 'probe'", systemsFilter) {
                    @Override
                    protected void cardTargeted(int targetGroupId, final PhysicalCard targetedCard) {
                        action.addAnimationGroup(targetedCard);
                        // Allow response(s)
                        action.allowResponses("'Probe' " + GameUtils.getCardLink(targetedCard),
                                new UnrespondableEffect(action) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        PhysicalCard finalSystem = action.getPrimaryTargetCard(targetGroupId);
                                        action.appendEffect(
                                                new StackOneCardFromHandEffect(action, self, finalSystem, true, true, false, false, false));
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
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        GameTextActionId gameTextActionId = GameTextActionId.PROBE_TELEMETRY__UPLOAD_PROBE_DROID;

        // Check condition(s)
        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Take a probe droid into hand from Reserve Deck");
            // Allow response(s)
            action.allowResponses("Take a probe droid into hand from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.probe_droid, true));
                        }
                    }
            );
            actions.add(action);
        }
        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Alternatives_To_Fighting)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Alternatives_To_Fighting, Title.Alternatives_To_Fighting);
            actions.add(action);
        }
        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.It_Can_Wait)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.It_Can_Wait, Title.It_Can_Wait);
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.calculatingEpicEventTotal(game, effectResult, Filters.Restore_Freedom_To_The_Galaxy)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Subtract 3 from attempt to 'liberate' a system");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new AddUntilEndOfEpicEventModifierEffect(action,
                                            new RestoreFreedomToTheGalaxyTotalModifier(self, -3), "Subtracts 3 from attempt to 'liberate' a system"));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.or(Filters.Alternatives_To_Fighting, Filters.It_Can_Wait))
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }

}
