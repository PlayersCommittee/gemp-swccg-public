package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddBattleDestinyEffect;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
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
import com.gempukku.swccgo.logic.effects.CancelCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premiere
 * Type: Interrupt
 * Subtype: Lost
 * Title: Skywalkers
 */
public class Card1_110 extends AbstractLostInterrupt {
    public Card1_110() {
        super(Side.LIGHT, 5, Title.Skywalkers, Uniqueness.UNIQUE);
        setLore("Luke and Leia escaped to an unused portion of the Death Star, evading security checkpoints. At a retracted bridge, they swung across on a grappling line through enemy fire.");
        setGameText("If Luke and Leia are in a battle together, you may add two battle destiny OR Cancel Imperial Barrier or Wrong Turn or Retract The Bridge.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (GameConditions.isDuringBattleWithParticipant(game, Filters.Luke)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.Leia)
                && GameConditions.canAddBattleDestinyDraws(game, self)) {
            final Filter lukeFilter = Filters.and(Filters.Luke, Filters.participatingInBattle);
            final Filter hanFilter = Filters.and(Filters.Leia, Filters.participatingInBattle);
            if (GameConditions.canTarget(game, self, lukeFilter)
                    && GameConditions.canTarget(game, self, hanFilter)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Add two battle destiny");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose Luke", lukeFilter) {
                            @Override
                            protected boolean getUseShortcut() {
                                return true;
                            }
                            @Override
                            protected void cardTargeted(final int targetGroupId1, final PhysicalCard lukeTargeted) {
                                // Choose target(s)
                                action.appendTargeting(
                                        new TargetCardOnTableEffect(action, playerId, "Choose Leia", hanFilter) {
                                            @Override
                                            protected boolean getUseShortcut() {
                                                return true;
                                            }
                                            @Override
                                            protected void cardTargeted(final int targetGroupId2, final PhysicalCard leiaTargeted) {
                                                action.addAnimationGroup(lukeTargeted, leiaTargeted);
                                                // Allow response(s)
                                                action.allowResponses("Add two battle destiny by targeting " + GameUtils.getAppendedNames(Arrays.asList(lukeTargeted, leiaTargeted)),
                                                        new RespondablePlayCardEffect(action) {
                                                            @Override
                                                            protected void performActionResults(Action targetingAction) {
                                                                // Perform result(s)
                                                                action.appendEffect(
                                                                        new AddBattleDestinyEffect(action, 2));
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
        }

        Filter cardToCancelFilter = Filters.or(Filters.Imperial_Barrier, Filters.Wrong_Turn, Filters.Retract_The_Bridge);

        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, cardToCancelFilter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Cancel Imperial Barrier or Wrong Turn or Retract The Bridge");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose card to cancel", TargetingReason.TO_BE_CANCELED, cardToCancelFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId1, PhysicalCard cardToCancelTargeted) {
                            action.addAnimationGroup(cardToCancelTargeted);
                            // Allow response(s)
                            action.allowResponses("Cancel " + GameUtils.getCardLink(cardToCancelTargeted),
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the final targeted card(s)
                                            PhysicalCard finalCardToCancel = targetingAction.getPrimaryTargetCard(targetGroupId1);
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new CancelCardOnTableEffect(action, finalCardToCancel));
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
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(final String playerId, SwccgGame game, final Effect effect, final PhysicalCard self) {
        Filter filter = Filters.or(Filters.Imperial_Barrier, Filters.Wrong_Turn, Filters.Retract_The_Bridge);

        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, filter)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }
}