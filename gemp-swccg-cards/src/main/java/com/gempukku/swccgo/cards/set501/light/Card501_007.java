package com.gempukku.swccgo.cards.set501.light;


import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 13
 * Type: Interrupt
 * SubType: Used Or Lost
 * Title: Help Me Obi-Wan Kenobi & Quite A Mercenary
 */
public class Card501_007 extends AbstractUsedOrLostInterrupt {
    public Card501_007() {
        super(Side.LIGHT, 4, "Help Me Obi-Wan Kenobi & Quite A Mercenary");
        addComboCardTitles(Title.Help_Me_Obi_Wan_Kenobi, Title.Quite_A_Mercenary);
        setGameText("If opponent's Audience Chamber on table, opponent loses 1 Force when you play this Interrupt. " +
                "USED: Cancel Elis Helrot or Stunning Leader. [Immune to Sense.] " +
                "OR Cancel a smuggler's game text for remainder of turn. " +
                "LOST: During your move phase, 'break cover' of an Undercover spy. OR " +
                "During battle, target an opponent's character of ability < 2 with your Jedi or smuggler; target is excluded from battle.");
        addIcon(Icon.VIRTUAL_SET_13);
        setTestingText("Help Me Obi-Wan Kenobi & Quite A Mercenary");
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredInterruptPlayedTriggers(SwccgGame game, Effect effect, PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());

        // Check condition(s)
        if (GameConditions.canSpot(game, self, Filters.and(Filters.opponents(self.getOwner()), Filters.Audience_Chamber))) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, self.getCardId());
            action.setText("Make " + opponent + " lose 1 Force");
            // Perform result(s)
            action.appendEffect(
                    new LoseForceEffect(action, opponent, 1));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Elis_Helrot)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setImmuneTo(Title.Sense);

            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            actions.add(action);
        }

        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Stunning_Leader)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setImmuneTo(Title.Sense);

            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            actions.add(action);
        }

        return actions;
    }


    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Elis_Helrot)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setImmuneTo(Title.Sense);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Elis_Helrot, Title.Elis_Helrot);
            actions.add(action);
        }

        if (GameConditions.canTargetToCancel(game, self, Filters.Stunning_Leader)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setImmuneTo(Title.Sense);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Stunning_Leader, Title.Stunning_Leader);
            actions.add(action);
        }

        // USED:  Cancel a smuggler's game text for remainder of turn.
        Filter smugglerFilter = Filters.smuggler;

        // Check condition(s)
        if (GameConditions.canTarget(game, self, smugglerFilter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Cancel smuggler's game text");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose smuggler", smugglerFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Allow response(s)
                            action.allowResponses("Cancel " + GameUtils.getCardLink(targetedCard) + "'s game text",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            final PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                            // Perform result(s)
                                            action.appendEffect(
                                                    new CancelGameTextUntilEndOfTurnEffect(action, finalTarget));
                                        }
                                    }
                            );
                        }
                    });
            actions.add(action);
        }

        Filter targetFilter = Filters.undercover_spy;

        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, self, Phase.MOVE) &&
                GameConditions.canTarget(game, self, SpotOverride.INCLUDE_UNDERCOVER, targetFilter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Break a spy's cover");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose undercover spy", SpotOverride.INCLUDE_UNDERCOVER, targetFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard cardTargeted) {
                            action.addAnimationGroup(cardTargeted);

                            // Allow response(s)
                            action.allowResponses("'Break cover' of " + GameUtils.getCardLink(cardTargeted),
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            PhysicalCard finalSpy = action.getPrimaryTargetCard(targetGroupId);

                                            // Perform result(s)
                                            action.appendEffect(
                                                    new BreakCoverEffect(action, finalSpy));
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(action);
        }

        // Check condition(s)
        if (GameConditions.isDuringBattle(game)) {
            Filter filter = Filters.and(Filters.opponents(self), Filters.character, Filters.abilityLessThan(2), Filters.participatingInBattle,
                    Filters.presentWith(self, Filters.and(Filters.your(self), Filters.or(Filters.Jedi, Filters.smuggler))));
            TargetingReason targetingReason = TargetingReason.TO_BE_EXCLUDED_FROM_BATTLE;
            if (GameConditions.canTarget(game, self, targetingReason, filter)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
                action.setText("Exclude character from battle");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose character", targetingReason, filter) {
                            @Override
                            protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                                action.addAnimationGroup(targetedCard);
                                // Allow response(s)
                                action.allowResponses("Exclude " + GameUtils.getCardLink(targetedCard) + " from battle",
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Get the targeted card(s) from the action using the targetGroupId.
                                                // This needs to be done in case the target(s) were changed during the responses.
                                                PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                                // Perform result(s)
                                                action.appendEffect(
                                                        new ExcludeFromBattleEffect(action, finalTarget));
                                            }
                                        }
                                );
                            }
                        }
                );
                actions.add(action);
            }
        }

        return actions;
    }
}
