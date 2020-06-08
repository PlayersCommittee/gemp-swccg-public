package com.gempukku.swccgo.cards.set5.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddBattleDestinyEffect;
import com.gempukku.swccgo.cards.effects.SatisfyAllBattleDamageAndAttritionEffect;
import com.gempukku.swccgo.cards.effects.SatisfyAllBattleDamageEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.ForfeitCardFromTableEffect;
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
 * Subtype: Lost
 * Title: This Is Even Better
 */
public class Card5_073 extends AbstractLostInterrupt {
    public Card5_073() {
        super(Side.LIGHT, 3, "This Is Even Better", Uniqueness.UNIQUE);
        setLore("It took escape from Hoth, asteroids, slugs, capture, refreshments with Vader, torture and the inevitable carbon-freezing to get Leia to reveal her true feelings for Han.");
        setGameText("If Han and Leia are together in a battle you just lost, forfeit one of them to satisfy all battle damage and attrition against you. OR If Han is a captive and a battle was just initiated where you have Leia (or vice versa), add one battle destiny. OR Cancel This Is Still Wrong.");
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        // Check condition(s)
        if (TriggerConditions.battleInitiated(game, effectResult)
                && GameConditions.canAddBattleDestinyDraws(game, self)
                && ((GameConditions.canSpot(game, self, SpotOverride.INCLUDE_CAPTIVE, Filters.and(Filters.Han, Filters.captive)) && GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.your(self), Filters.Leia)))
                || (GameConditions.canSpot(game, self, SpotOverride.INCLUDE_CAPTIVE, Filters.and(Filters.Leia, Filters.captive)) && GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.your(self), Filters.Han))))) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setActionMsg("Add one battle destiny");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new AddBattleDestinyEffect(action, 1));
                        }
                    }
            );
            actions.add(action);
        }

        // Check condition(s)
        if (TriggerConditions.isResolvingBattleDamageAndAttrition(game, effectResult, playerId)
                && GameConditions.isDuringBattleLostBy(game, playerId)
                && (GameConditions.isBattleDamageRemaining(game, playerId) || GameConditions.isAttritionRemaining(game, playerId))
                && GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.Han, Filters.canBeTargetedBy(self)))
                && GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.Leia, Filters.canBeTargetedBy(self)))) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Forfeit Han or Leia");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose Han or Leia", Filters.or(Filters.Han, Filters.Leia)) {
                        @Override
                        protected void cardTargeted(int targetGroupId, final PhysicalCard targetedCard) {
                            final boolean cannotSatisfyAttrition = game.getModifiersQuerying().cannotSatisfyAttrition(game.getGameState(), self);
                            if (cannotSatisfyAttrition)
                                action.setActionMsg("Satisfy all battle damage");
                            else
                                action.setActionMsg("Satisfy all battle damage and attrition");
                            // Pay cost(s)
                            action.appendCost(
                                    new ForfeitCardFromTableEffect(action, targetedCard));
                            // Allow response(s)
                            action.allowResponses(
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            if (cannotSatisfyAttrition)
                                                action.appendEffect(
                                                        new SatisfyAllBattleDamageEffect(action, playerId));
                                            else
                                                action.appendEffect(
                                                        new SatisfyAllBattleDamageAndAttritionEffect(action, playerId));
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.This_Is_Still_Wrong)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.This_Is_Still_Wrong, Title.This_Is_Still_Wrong);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(final String playerId, SwccgGame game, final Effect effect, final PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.This_Is_Still_Wrong)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }
}