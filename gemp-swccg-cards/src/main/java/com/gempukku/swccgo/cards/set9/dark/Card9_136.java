package com.gempukku.swccgo.cards.set9.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Death Star II
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Force Lightning
 */
public class Card9_136 extends AbstractUsedOrLostInterrupt {
    public Card9_136() {
        super(Side.DARK, 5, Title.Force_Lightning, Uniqueness.UNIQUE);
        setLore("The Emperor is strong in the dark arts of the Force. He can direct pure energy to shoot forth from his outstretched fingers.");
        setGameText("Target any character (even a captive) present with Emperor. USED: Target is defense value -4 for remainder of turn. LOST: Emperor loses immunity to attrition and is defense value -2 for remainder of turn. Draw destiny. Target lost if destiny +1 > defense value.");
        addIcons(Icon.DEATH_STAR_II);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        Filter filter1 = Filters.and(Filters.character, Filters.not(Filters.frozenCaptive),
                Filters.presentWith(self, Filters.or(Filters.Emperor, Filters.grantedMayBeTargetedBy(self))));

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
        Filter filter2 = Filters.and(Filters.or(Filters.Emperor, Filters.grantedMayBeTargetedBy(self)), Filters.hasAnyImmunityToAttrition, Filters.presentWith(self,
                SpotOverride.INCLUDE_CAPTIVE, Filters.and(Filters.character, Filters.not(Filters.frozenCaptive), Filters.canBeTargetedBy(self, targetingReason))));

        // Check condition(s)
        if (GameConditions.canSpot(game, self, filter2)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Make target lost");
            // Choose target(s)
            action.appendTargeting(
                    new ChooseCardOnTableEffect(action, playerId, "Choose Emperor", filter2) {
                        @Override
                        protected void cardSelected(final PhysicalCard emperor) {
                            action.addAnimationGroup(emperor);
                            Filter filter3 = Filters.and(Filters.character, Filters.not(Filters.frozenCaptive), Filters.presentWith(emperor));
                            // Choose target(s)
                            action.appendTargeting(
                                    new TargetCardOnTableEffect(action, playerId, "Choose character", SpotOverride.INCLUDE_CAPTIVE, targetingReason, filter3) {
                                        @Override
                                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                                            action.addAnimationGroup(targetedCard);
                                            // Pay cost(s)
                                            action.appendCost(
                                                    new CancelImmunityToAttritionUntilEndOfTurnEffect(action, emperor, "Cancels " + GameUtils.getCardLink(emperor) + "'s immunity to attrition"));
                                            action.appendCost(
                                                    new ModifyDefenseValueUntilEndOfTurnEffect(action, emperor, -2));
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