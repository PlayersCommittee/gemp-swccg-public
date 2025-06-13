package com.gempukku.swccgo.cards.set12.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Coruscant
 * Type: Interrupt
 * Subtype: Used
 * Title: New Leadership Is Needed
 */
public class Card12_064 extends AbstractUsedInterrupt {
    public Card12_064() {
        super(Side.LIGHT, 5, Title.New_Leadership_Is_Needed, Uniqueness.UNIQUE, ExpansionSet.CORUSCANT, Rarity.C);
        setLore("The political process in the Republic is one whereby even the most powerful positions are fraught with peril.");
        setGameText("If you have a senate majority, activate 1 Force. OR If your opponent has a senate majority, place your character with politics at Galactic Senate in Used Pile to place an opponent's character present (your choice) in opponent's Used Pile. (Immune to Sense.)");
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (GameConditions.hasSenateMajority(game, playerId)
                && GameConditions.canActivateForce(game, playerId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Activate 1 Force");
            action.setImmuneTo(Title.Sense);
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new ActivateForceEffect(action, playerId, 1));
                        }
                    }
            );
            actions.add(action);
        }

        Filter yourCharacterFilter = Filters.and(Filters.your(self), Filters.character_with_politics, Filters.at(Filters.Galactic_Senate));
        final Filter opponentsCharacterFilter = Filters.and(Filters.opponents(self), Filters.character, Filters.presentAt(Filters.Galactic_Senate));

        // Check condition(s)
        if (GameConditions.hasSenateMajority(game, opponent)
                && GameConditions.canTarget(game, self, yourCharacterFilter)
                && GameConditions.canTarget(game, self, opponentsCharacterFilter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Place character in Used Pile");
            action.setImmuneTo(Title.Sense);
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose your character", yourCharacterFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId1, final PhysicalCard yourCharacter) {
                            // Choose target(s)
                            action.appendTargeting(
                                    new TargetCardOnTableEffect(action, playerId, "Choose opponent's character", opponentsCharacterFilter) {
                                        @Override
                                        protected void cardTargeted(final int targetGroupId2, PhysicalCard opponentsCharacter) {
                                            action.addAnimationGroup(opponentsCharacter);
                                            // Pay cost(s)
                                            action.appendCost(
                                                    new PlaceCardInUsedPileFromTableEffect(action, yourCharacter));
                                            // Allow response(s)
                                            action.allowResponses("Place " + GameUtils.getCardLink(opponentsCharacter) + " in Used Pile",
                                                    new RespondablePlayCardEffect(action) {
                                                        @Override
                                                        protected void performActionResults(Action targetingAction) {
                                                            // Get the targeted card(s) from the action using the targetGroupId.
                                                            // This needs to be done in case the target(s) were changed during the responses.
                                                            PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId2);

                                                            // Perform result(s)
                                                            action.appendEffect(
                                                                    new PlaceCardInUsedPileFromTableEffect(action, finalTarget));
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