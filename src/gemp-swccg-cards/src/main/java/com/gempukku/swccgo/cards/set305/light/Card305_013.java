package com.gempukku.swccgo.cards.set305.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
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
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.CancelImmunityToAttritionUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.ModifyDefenseValueUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: A Better Tomorrow
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Uncontrolled Force Lightning
 */
public class Card305_013 extends AbstractUsedOrLostInterrupt {
    public Card305_013() {
        super(Side.LIGHT, 5, Title.Uncontrolled_Force_Lightning, Uniqueness.UNIQUE, ExpansionSet.ABT, Rarity.R);
        setLore("Anger and Fear are a dangerous combination with the Force. Fighting against the First Order, Rey's emotions lashed out in a blast of uncontrolled Force lightning. Nearly killing her friend.");
        setGameText("Target any character (even a captive) present with Rey. USED: Target is defense value -4 for remainder of turn. LOST: Rey loses immunity to attrition and is defense value -2 for remainder of turn. Draw destiny. Target lost if destiny +1 > defense value.");
        addIcons(Icon.ABT);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        Filter filter1 = Filters.and(Filters.character, Filters.not(Filters.frozenCaptive),
                Filters.presentWith(self, Filters.or(Filters.Rey, Filters.grantedMayBeTargetedBy(self))));

        // Check condition(s)
        if (GameConditions.canTarget(game, self, SpotOverride.INCLUDE_CAPTIVE, filter1)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Reduce target's defense value");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose character", SpotOverride.INCLUDE_CAPTIVE, filter1) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Allow response(s)
                            action.allowResponses("Make " + GameUtils.getCardLink(targetedCard) + " defense value -4",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                            // Perform result(s)
                                            action.appendEffect(
                                                    new ModifyDefenseValueUntilEndOfTurnEffect(action, finalTarget, -4));
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(action);
        }

        final TargetingReason targetingReason = TargetingReason.TO_BE_LOST;
        Filter filter2 = Filters.and(Filters.or(Filters.Rey, Filters.grantedMayBeTargetedBy(self)), Filters.hasAnyImmunityToAttrition, Filters.presentWith(self,
                SpotOverride.INCLUDE_CAPTIVE, Filters.and(Filters.character, Filters.not(Filters.frozenCaptive), Filters.canBeTargetedBy(self, targetingReason))));

        // Check condition(s)
        if (GameConditions.canSpot(game, self, filter2)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Make target lost");
            // Choose target(s)
            action.appendTargeting(
                    new ChooseCardOnTableEffect(action, playerId, "Choose Rey", filter2) {
                        @Override
                        protected void cardSelected(final PhysicalCard rey) {
                            action.addAnimationGroup(rey);
                            Filter filter3 = Filters.and(Filters.character, Filters.not(Filters.frozenCaptive), Filters.presentWith(rey));
                            // Choose target(s)
                            action.appendTargeting(
                                    new TargetCardOnTableEffect(action, playerId, "Choose character", SpotOverride.INCLUDE_CAPTIVE, targetingReason, filter3) {
                                        @Override
                                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                                            action.addAnimationGroup(targetedCard);
                                            // Pay cost(s)
                                            action.appendCost(
                                                    new CancelImmunityToAttritionUntilEndOfTurnEffect(action, rey, "Cancels " + GameUtils.getCardLink(rey) + "'s immunity to attrition"));
                                            action.appendCost(
                                                    new ModifyDefenseValueUntilEndOfTurnEffect(action, rey, -2));
                                            // Allow response(s)
                                            action.allowResponses("Make " + GameUtils.getCardLink(targetedCard) + " lost",
                                                    new RespondablePlayCardEffect(action) {
                                                        @Override
                                                        protected void performActionResults(Action targetingAction) {
                                                            // Get the targeted card(s) from the action using the targetGroupId.
                                                            // This needs to be done in case the target(s) were changed during the responses.
                                                            final PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                                            // Perform result(s)
                                                            action.appendEffect(
                                                                    new DrawDestinyEffect(action, playerId) {
                                                                        @Override
                                                                        protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                                                            return Collections.singletonList(finalTarget);
                                                                        }
                                                                        @Override
                                                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                                            GameState gameState = game.getGameState();

                                                                            float defenseValue = game.getModifiersQuerying().getDefenseValue(game.getGameState(), finalTarget);


                                                                            if (totalDestiny != null) {
                                                                                gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                                                gameState.sendMessage("Defense value: " + GuiUtils.formatAsString(defenseValue));
                                                                                if ((totalDestiny + 1) > defenseValue) {
                                                                                    gameState.sendMessage("Result: Succeeded");
                                                                                    action.appendEffect(
                                                                                            new LoseCardFromTableEffect(action, finalTarget));
                                                                                } else {
                                                                                    gameState.sendMessage("Result: Failed");
                                                                                }
                                                                            } else {
                                                                                gameState.sendMessage("Result: Failed Destiny Draw.");
                                                                            }
                                                                        }
                                                                    });

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
        return actions;
    }
}