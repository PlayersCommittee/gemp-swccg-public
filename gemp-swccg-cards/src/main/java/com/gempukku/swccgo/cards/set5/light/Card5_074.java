package com.gempukku.swccgo.cards.set5.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.DeploymentRestrictionsOption;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.ReduceAttritionEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromHandEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Cloud City
 * Type: Interrupt
 * Subtype: Used
 * Title: We'll Find Han
 */
public class Card5_074 extends AbstractUsedInterrupt {
    public Card5_074() {
        super(Side.LIGHT, 4, "We'll Find Han", Uniqueness.UNIQUE);
        setLore("'I promise.' 'Auuuuuug!'");
        setGameText("Deploy Han to same site as Chewie or your Lando (regardless of deployment restrictions). OR If Chewie and your Lando are in a battle together, subtract 3 from all attrition against you (cannot not fall below zero). OR Cancel I Had No Choice.");
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        final Filter sameSiteAsChewieOrLando = Filters.sameSiteAs(self, Filters.or(Filters.Chewie, Filters.and(Filters.your(self), Filters.Lando)));
        final DeploymentRestrictionsOption deploymentRestrictionsOption = DeploymentRestrictionsOption.ignoreLocationDeploymentRestrictions();

        // Check condition(s)
        if (GameConditions.hasInHand(game, playerId, Filters.and(Filters.Han, Filters.deployableToLocation(self, sameSiteAsChewieOrLando, false, false, 0, null, deploymentRestrictionsOption, null)))) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Deploy Han");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new DeployCardToLocationFromHandEffect(action, playerId, Filters.Han, sameSiteAsChewieOrLando, false, deploymentRestrictionsOption));
                        }
                    }
            );
            actions.add(action);
        }
        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.I_Had_No_Choice)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.I_Had_No_Choice, Title.I_Had_No_Choice);
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.isInitialAttritionJustCalculated(game, effectResult)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.Chewie)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.your(self), Filters.Lando))) {
            int currentAttrition = (int) Math.ceil(GameConditions.getAttritionRemaining(game, playerId));
            if (currentAttrition > 0) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Reduce attrition by 3");
                // Allow response(s)
                action.allowResponses(
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s)
                                action.appendEffect(
                                        new ReduceAttritionEffect(action, playerId, 3));
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
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.I_Had_No_Choice)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }
}