package com.gempukku.swccgo.cards.set14.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PayRelocateBetweenLocationsCostEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.ModifyLandspeedUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.RelocateBetweenLocationsEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseStackedCardEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeStackedCardIntoHandEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Theed Palace
 * Type: Interrupt
 * Subtype: Used
 * Title: Gimme A Lift!
 */
public class Card14_042 extends AbstractUsedInterrupt {
    public Card14_042() {
        super(Side.LIGHT, 5, "Gimme A Lift!", Uniqueness.UNIQUE, ExpansionSet.THEED_PALACE, Rarity.R);
        setLore("Jar Jar Binks's courage on the battlefield put quite a thorn in the Trade Federation's side. Many believed it wasn't actually courage.");
        setGameText("Relocate Jar Jar to a battle just initiated at an adjacent site. OR Increase Jar Jar's landspeed by 1 for remainder of turn. OR If Jar Jar on Brisky Morning Munchen, take him into hand.");
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        Filter filter = Filters.and(Filters.Jar_Jar, Filters.canBeRelocatedToLocation(Filters.battleLocation, 0));

        // Check condition(s)
        if (TriggerConditions.battleInitiatedAt(game, effectResult, Filters.adjacentSiteTo(self, filter))
                && GameConditions.canTarget(game, self, filter)) {
            final PhysicalCard battleLocation = game.getGameState().getBattleLocation();

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Relocate Jar Jar to battle");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose Jar Jar to relocate", filter) {
                        @Override
                        protected void cardTargeted(int targetGroupId, final PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            action.addAnimationGroup(battleLocation);
                            // Pay cost(s)
                            action.appendCost(
                                    new PayRelocateBetweenLocationsCostEffect(action, playerId, targetedCard, battleLocation, 0));
                            // Allow response(s)
                            action.allowResponses("Relocate " + GameUtils.getCardLink(targetedCard) + " to " + GameUtils.getCardLink(battleLocation),
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new RelocateBetweenLocationsEffect(action, targetedCard, battleLocation));
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

        Filter filter = Filters.Jar_Jar;

        // Check condition(s)
        if (GameConditions.canTarget(game, self, filter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Add 1 to Jar Jar's landspeed");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose Jar Jar", filter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Allow response(s)
                            action.allowResponses("Add 1 to " + GameUtils.getCardLink(targetedCard) + "'s landspeed",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            final PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                            // Perform result(s)
                                            action.appendEffect(
                                                    new ModifyLandspeedUntilEndOfTurnEffect(action, finalTarget, 1));
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(action);
        }

        Filter filter2 = Filters.and(Filters.Brisky_Morning_Munchen, Filters.hasStacked(Filters.Jar_Jar));

        // Check condition(s)
        if (GameConditions.canSpot(game, self, filter2)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Take Jar Jar into hand");
            // Choose target(s)
            action.appendTargeting(
                    new ChooseStackedCardEffect(action, playerId, Filters.Brisky_Morning_Munchen, Filters.Jar_Jar) {
                        @Override
                        public String getChoiceText(int numCardsToChoose) {
                            return "Choose Jar Jar";
                        }
                        @Override
                        protected void cardSelected(final PhysicalCard selectedCard) {
                            action.addAnimationGroup(selectedCard);
                            // Allow response(s)
                            action.allowResponses("Take " + GameUtils.getCardLink(selectedCard) + " into hand",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new TakeStackedCardIntoHandEffect(action, playerId, Filters.hasStacked(selectedCard), selectedCard));
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