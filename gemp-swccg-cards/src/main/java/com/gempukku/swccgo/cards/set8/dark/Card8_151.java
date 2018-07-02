package com.gempukku.swccgo.cards.set8.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddBattleDestinyEffect;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Endor
 * Type: Interrupt
 * Subtype: Used
 * Title: Pitiful Little Band
 */
public class Card8_151 extends AbstractUsedInterrupt {
    public Card8_151() {
        super(Side.DARK, 5, "Pitiful Little Band", Uniqueness.UNIQUE);
        setLore("The Emperor's defense of the forest moon of Endor appeared to work with devastating effectiveness.");
        setGameText("If your scout is battling opponent's scout, spy or operative, add one battle destiny. OR If you have a spy or scout at Bunker or any prison, place out of play one captured spy of ability < 3 or captured operative there. Retrieve Force equal to double that captive's forfeit.");
        addIcons(Icon.ENDOR);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.your(self), Filters.scout))
                && GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.opponents(self), Filters.or(Filters.scout, Filters.spy, Filters.operative)))
                && GameConditions.canAddBattleDestinyDraws(game, self)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Add one battle destiny");
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

        Filter bunkerOrPrisonFilter = Filters.and(Filters.or(Filters.Bunker, Filters.prison), Filters.sameLocationAs(self, Filters.and(Filters.your(self), Filters.or(Filters.spy, Filters.scout))));
        Filter filter = Filters.and(Filters.captive, Filters.or(Filters.and(Filters.spy, Filters.abilityLessThan(3)), Filters.operative), Filters.at(bunkerOrPrisonFilter));
        TargetingReason targetingReason = TargetingReason.TO_BE_PLACED_OUT_OF_PLAY;

        // Check condition(s)
        if (GameConditions.canTarget(game, self, targetingReason, filter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Place captive out of play to retrieve Force");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose captive", targetingReason, filter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            final float forceToRetrieve = 2 * game.getModifiersQuerying().getForfeit(game.getGameState(), targetedCard);
                            // Allow response(s)
                            action.allowResponses("Place " + GameUtils.getCardLink(targetedCard) + " out of play to retrieve " + GuiUtils.formatAsString(forceToRetrieve) + " Force",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            final PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);
                                            final boolean mayNotContributeToForceRetrieval = !Filters.mayContributeToForceRetrieval.accepts(game, finalTarget);

                                            // Perform result(s)
                                            action.appendEffect(
                                                    new PlaceCardOutOfPlayFromTableEffect(action, finalTarget) {
                                                        @Override
                                                        protected void cardPlacedOutOfPlay(PhysicalCard card) {
                                                            if (mayNotContributeToForceRetrieval) {
                                                                action.appendEffect(
                                                                        new SendMessageEffect(action, "Force retrieval not allowed due to including cards not allowed to contribute to Force retrieval"));
                                                            }
                                                            else {
                                                                action.appendEffect(
                                                                        new RetrieveForceEffect(action, playerId, forceToRetrieve));
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
            actions.add(action);
        }

        return actions;
    }
}