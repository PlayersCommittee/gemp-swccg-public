package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.CancelCardBeingPlayedEffect;
import com.gempukku.swccgo.logic.effects.CancelCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.CaptureCharacterOnTableEffect;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.ReleaseCaptiveEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayingCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardBeingPlayedForCancelingEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: A New Hope
 * Type: Interrupt
 * Subtype: Used
 * Title: I'm Here To Rescue You
 */
public class Card2_052 extends AbstractUsedInterrupt {
    public Card2_052() {
        super(Side.LIGHT, 6, Title.Im_Here_To_Rescue_You, Uniqueness.UNRESTRICTED, ExpansionSet.A_NEW_HOPE, Rarity.U1);
        setLore("'Huh?'");
        setGameText("If you have a spy present at the Detention Block Corridor, target a captive there. Draw destiny. If destiny + ability of spy > ability of captive, target is released. Otherwise, spy is captured. OR Cancel Spice Mines Of Kessel (releasing targeted captive).");
        addIcons(Icon.A_NEW_HOPE);
        addKeyword(Keyword.CAN_RELEASE_CAPTIVES);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        final Filter spyFilter = Filters.and(Filters.your(playerId), Filters.spy, Filters.presentAt(Filters.Detention_Block_Corridor));
        final Filter captiveFilter = Filters.and(Filters.your(playerId), Filters.captive, Filters.at(Filters.Detention_Block_Corridor));

        // Check condition(s)
        if (GameConditions.canSpot(game, self, spyFilter)
                && GameConditions.canTarget(game, self, SpotOverride.INCLUDE_CAPTIVE, captiveFilter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Attempt to release captive");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose spy", spyFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId1, final PhysicalCard spy) {
                            action.appendTargeting(
                                    new TargetCardOnTableEffect(action, playerId, "Choose captive", SpotOverride.INCLUDE_CAPTIVE, captiveFilter) {
                                        @Override
                                        protected void cardTargeted(final int targetGroupId2, final PhysicalCard captive) {
                                            // Allow response(s)
                                            action.allowResponses("Have " + GameUtils.getCardLink(spy) + " attempt to release " + GameUtils.getCardLink(captive),
                                                    new RespondablePlayCardEffect(action) {
                                                        @Override
                                                        protected void performActionResults(Action targetingAction) {
                                                            // Perform result(s)
                                                            action.appendEffect(
                                                                    new DrawDestinyEffect(action, playerId) {
                                                                        @Override
                                                                        protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                                                            return Arrays.asList(spy, captive);
                                                                        }
                                                                        @Override
                                                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                                            GameState gameState = game.getGameState();
                                                                            float spyAbility = game.getModifiersQuerying().getAbility(game.getGameState(), spy);
                                                                            float captiveAbility = game.getModifiersQuerying().getAbility(game.getGameState(), captive);

                                                                            gameState.sendMessage("Destiny: " + (totalDestiny != null ? GuiUtils.formatAsString(totalDestiny) : "Failed destiny draw"));
                                                                            gameState.sendMessage("Spy's ability: " + GuiUtils.formatAsString(spyAbility));
                                                                            gameState.sendMessage("Captives's ability: " + GuiUtils.formatAsString(captiveAbility));

                                                                            if (((totalDestiny != null ? totalDestiny : 0) + spyAbility) > captiveAbility) {
                                                                                gameState.sendMessage("Result: Succeeded");
                                                                                action.appendEffect(
                                                                                        new ReleaseCaptiveEffect(action, captive));
                                                                            }
                                                                            else {
                                                                                gameState.sendMessage("Result: Failed");
                                                                                action.appendEffect(
                                                                                        new CaptureCharacterOnTableEffect(action, spy));
                                                                            }
                                                                        }
                                                                    }
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
            actions.add(action);
        }
        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Spice_Mines_Of_Kessel)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Cancel " + Title.Spice_Mines_Of_Kessel);
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, action.getPerformingPlayer(), "Choose card to cancel", TargetingReason.TO_BE_CANCELED, Filters.Spice_Mines_Of_Kessel) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard cardTargeted) {
                            action.addAnimationGroup(cardTargeted);
                            // Allow response(s)
                            action.allowResponses("Cancel " + GameUtils.getCardLink(cardTargeted),
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            PhysicalCard cardToCancel = targetingAction.getPrimaryTargetCard(targetGroupId);
                                            PhysicalCard captiveToRelease = Filters.findFirstActive(game, self,
                                                    SpotOverride.INCLUDE_CAPTIVE, Filters.and(Filters.captive, Filters.targetedByCardOnTable(cardToCancel)));

                                            // Perform result(s)
                                            action.appendEffect(
                                                    new CancelCardOnTableEffect(action, cardToCancel));
                                            if (captiveToRelease != null) {
                                                action.appendEffect(
                                                        new ReleaseCaptiveEffect(action, captiveToRelease));
                                            }
                                        }
                                    });
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, final SwccgGame game, Effect effect, final PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Spice_Mines_Of_Kessel)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            final RespondablePlayingCardEffect respondableEffect = (RespondablePlayingCardEffect) effect;

            String ownerText = (((RespondablePlayingCardEffect) effect).getCard().getOwner().equals(action.getPerformingPlayer()) ? "your " : "");
            action.setText("Cancel " + ownerText + GameUtils.getFullName(respondableEffect.getCard()));
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardBeingPlayedForCancelingEffect(action, respondableEffect) {
                        @Override
                        protected void cardTargetedToBeCanceled(final PhysicalCard targetedCard) {
                            // Allow response(s)
                            action.allowResponses("Cancel " + GameUtils.getFullName(targetedCard),
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            PhysicalCard captiveToRelease = Filters.findFirstActive(game, self,
                                                    SpotOverride.INCLUDE_CAPTIVE, Filters.and(Filters.captive, Filters.targetedByCardBeingPlayed(self)));

                                            // Perform result(s)
                                            action.appendEffect(
                                                    new CancelCardBeingPlayedEffect(action, respondableEffect));
                                            if (captiveToRelease != null) {
                                                action.appendEffect(
                                                        new ReleaseCaptiveEffect(action, captiveToRelease));
                                            }
                                        }
                                    });
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}