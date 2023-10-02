package com.gempukku.swccgo.cards.set222.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
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
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.CancelCardBeingPlayedEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayingCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardBeingPlayedForCancelingEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.ManeuverModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 22
 * Type: Interrupt
 * Subtype: Lost
 * Title: Attack Pattern Delta (V)
 */
public class Card222_019 extends AbstractUsedOrLostInterrupt {
    public Card222_019() {
        super(Side.LIGHT, 3, "Attack Pattern Delta", Uniqueness.UNRESTRICTED, ExpansionSet.SET_22, Rarity.V);
        setVirtualSuffix(true);
        setLore("Snowspeeder attack plan devised by Commander Skywalker and Rebel tactician Beryl Chifonage. Single-file formation protects the squadron as the leader draws fire.");
        setGameText("USED: Cancel Crash Landing or High-speed Tactics. " +
                "LOST: Target a T-47. " +
                "For remainder of turn, target is maneuver +1 and immune to attrition. " +
                "OR If you occupy three Hoth battlegrounds with T-47s, cancel a non-[Immune to Sense] Interrupt just played.");
        addIcons(Icon.HOTH, Icon.VIRTUAL_SET_22);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (GameConditions.isDuringYourTurn(game, self)
                && GameConditions.canTarget(game, self, Filters.T_47)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Target a T-47");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose T-47", Filters.T_47) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Allow response(s)
                            action.allowResponses("Make " + GameUtils.getCardLink(targetedCard) + " maneuver +1 and immune to attrition",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            final PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                            // Perform result(s)
                                            action.appendEffect(
                                                    new AddUntilEndOfTurnModifierEffect(action,
                                                            new ManeuverModifier(self, finalTarget, 1),
                                                            "Makes " + GameUtils.getCardLink(finalTarget) + " maneuver +1"
                                                    ));
                                            action.appendEffect(
                                                    new AddUntilEndOfTurnModifierEffect(action,
                                                            new ImmuneToAttritionModifier(self, finalTarget),
                                                            "Makes " + GameUtils.getCardLink(finalTarget) + " immune to attrition"));
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(action);
        }

        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Crash_Landing)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Crash_Landing, Title.Crash_Landing);
            actions.add(action);
        }
        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Highspeed_Tactics)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Highspeed_Tactics, Title.Highspeed_Tactics);
            actions.add(action);
        }
        return actions;
    }


    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(final String playerId, SwccgGame game, Effect effect, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new ArrayList<>();

        Filter interruptFilter = Filters.and(Filters.Interrupt, Filters.not(Filters.dejarikHologramAtHolosite), Filters.not(Filters.immune_to_Sense));

        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, interruptFilter)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)
                && GameConditions.occupiesWith(game, self, playerId, 3, Filters.and(Filters.Hoth_site, Filters.battleground), Filters.and(Filters.T_47, Filters.canBeTargetedBy(self)))) {

            final RespondablePlayingCardEffect respondableEffect = (RespondablePlayingCardEffect) effect;

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Cancel " + GameUtils.getFullName(respondableEffect.getCard()));
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardBeingPlayedForCancelingEffect(action, respondableEffect) {
                        @Override
                        protected void cardTargetedToBeCanceled(final PhysicalCard targetedEffect) {
                            action.allowResponses("Cancel " + GameUtils.getCardLink(targetedEffect),
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            action.appendEffect(
                                                    new CancelCardBeingPlayedEffect(action, respondableEffect));
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(action);
        }

        if (TriggerConditions.isPlayingCard(game, effect, Filters.or(Filters.Crash_Landing, Filters.Highspeed_Tactics))
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            actions.add(action);
        }
        return actions;
    }
}