package com.gempukku.swccgo.cards.set5.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.complete.ChooseExistingCardPileEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Cloud City
 * Type: Interrupt
 * Subtype: Used
 * Title: Slip Sliding Away
 */
public class Card5_154 extends AbstractUsedInterrupt {
    public Card5_154() {
        super(Side.DARK, 3, "Slip Sliding Away", Uniqueness.UNIQUE);
        setLore("Luke got the shaft.");
        setGameText("Relocate a card from the top of any Deck or Pile to the bottom (without looking). OR if you have a droid at a Scomp link on Cloud City, cancel Into The Ventilation Shaft, Lefty. OR use 3 Force to cause a character at Weather vane to be lost.");
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        final PlayInterruptAction action1 = new PlayInterruptAction(game, self);
        action1.setText("Relocate card to bottom of Deck or Pile");
        // Choose target(s)
        action1.appendCost(
                new ChooseExistingCardPileEffect(action1, playerId) {
                    @Override
                    protected void pileChosen(SwccgGame game, final String cardPileOwner, final Zone cardPile) {
                        // Allow response(s)
                        action1.allowResponses("Place top card of " + cardPileOwner + "'s " + cardPile.getHumanReadable() + " on bottom",
                                new RespondablePlayCardEffect(action1) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        // Perform result(s)
                                        action1.appendEffect(
                                                new PlaceTopCardFromCardPileOnBottomOfCardPileEffect(action1, cardPileOwner, cardPile, cardPile));
                                    }
                                }
                        );
                    }
                }
        );
        actions.add(action1);

        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Into_The_Ventilation_Shaft_Lefty)
                && GameConditions.canSpot(game, self, Filters.and(Filters.your(self), Filters.droid, Filters.at_Scomp_Link, Filters.on_Cloud_City))) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Into_The_Ventilation_Shaft_Lefty, Title.Into_The_Ventilation_Shaft_Lefty);
            actions.add(action);
        }

        Filter characterFilter = Filters.and(Filters.character, Filters.attachedTo(Filters.Weather_Vane));
        TargetingReason targetingReason = TargetingReason.TO_BE_LOST;

        // Check condition(s)
        if (GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 3)
                && GameConditions.canTarget(game, self, targetingReason, characterFilter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Make character on Weather Vane lost");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose character", targetingReason, characterFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard character) {
                            action.addAnimationGroup(character);
                            // Pay cost(s)
                            action.appendCost(
                                    new UseForceEffect(action, playerId, 3));
                            // Allow response(s)
                            action.allowResponses("Make " + GameUtils.getCardLink(character) + " lost",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new LoseCardFromTableEffect(action, character));
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
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(final String playerId, SwccgGame game, final Effect effect, final PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Into_The_Ventilation_Shaft_Lefty)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)
                && GameConditions.canSpot(game, self, Filters.and(Filters.your(self), Filters.droid, Filters.at_Scomp_Link, Filters.on_Cloud_City))) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }
}