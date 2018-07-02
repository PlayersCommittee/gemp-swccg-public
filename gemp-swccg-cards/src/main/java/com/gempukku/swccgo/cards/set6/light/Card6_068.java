package com.gempukku.swccgo.cards.set6.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelForceDrainEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Type: Interrupt
 * Subtype: Used
 * Title: Jedi Mind Trick
 */
public class Card6_068 extends AbstractUsedOrLostInterrupt {
    public Card6_068() {
        super(Side.LIGHT, 3, "Jedi Mind Trick", Uniqueness.UNIQUE);
        setLore("'You will bring Captain Solo and the Wookiee to me.'");
        setGameText("USED: Cancel a Force drain at a site if Luke is at an adjacent site. LOST: If your character of ability > 4 is present with an opponent's leader, release a captive from a related prison (canceled if opponent loses 2 Force).");
        addIcons(Icon.JABBAS_PALACE);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.forceDrainInitiatedAt(game, effectResult, Filters.adjacentSiteTo(self, Filters.Luke))
                && GameConditions.canCancelForceDrain(game, self)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Cancel Force drain");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new CancelForceDrainEffect(action));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);
        Filter filter = Filters.and(Filters.your(self), Filters.character, Filters.abilityMoreThan(4), Filters.presentWith(self, Filters.and(Filters.opponents(self), Filters.leader)));

        // Check condition(s)
        if (GameConditions.canSpot(game, self, filter)) {
            Filter captiveFilter = Filters.and(Filters.captive, Filters.at(Filters.and(Filters.prison, Filters.relatedLocationTo(self, filter))));
            if (GameConditions.canSpot(game, self, SpotOverride.INCLUDE_CAPTIVE, captiveFilter)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
                action.setText("Release captive");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose captive", SpotOverride.INCLUDE_CAPTIVE, captiveFilter) {
                            @Override
                            protected void cardTargeted(final int targetGroupId, PhysicalCard captive) {
                                action.addAnimationGroup(captive);
                                // Allow response(s)
                                action.allowResponses("Release " + GameUtils.getCardLink(captive),
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Get the targeted card(s) from the action using the targetGroupId.
                                                // This needs to be done in case the target(s) were changed during the responses.
                                                final PhysicalCard finalCaptive = action.getPrimaryTargetCard(targetGroupId);

                                                // Perform result(s)
                                                // Ask opponent to lose 2 Force to cancel
                                                action.appendEffect(
                                                        new PlayoutDecisionEffect(action, playerId,
                                                                new YesNoDecision("Do you want to lose 2 Force to cancel captive's release?") {
                                                                    @Override
                                                                    protected void yes() {
                                                                        action.appendEffect(
                                                                                new LoseForceEffect(action, opponent, 2, true));
                                                                    }

                                                                    @Override
                                                                    protected void no() {
                                                                        action.appendEffect(
                                                                                new ReleaseCaptiveEffect(action, finalCaptive));
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
        }
        return null;
    }
}