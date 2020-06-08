package com.gempukku.swccgo.cards.set14.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.modifiers.AttemptToBlowAwayBlockadeFlagshipTotalModifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Theed Palace
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: This Is Not Good
 */
public class Card14_108 extends AbstractUsedOrLostInterrupt {
    public Card14_108() {
        super(Side.DARK, 4, "This Is Not Good", Uniqueness.UNIQUE);
        setLore("'Everything's overheated.'");
        setGameText("USED: For remainder of turn, subtract 2 from any attempt to 'blow away' Blockade Flagship. LOST: During any deploy phase, use 3 Force to target any starship at same system as your battleship. Target is hyperspeed = 0 until end of turn.");
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (GameConditions.canSpot(game, self, Filters.Blockade_Flagship)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Subtract 2 from attempts to 'blow away' Blockade Flagship");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new AddUntilEndOfTurnModifierEffect(action,
                                            new AttemptToBlowAwayBlockadeFlagshipTotalModifier(self, -2),
                                            "Subtracts 2 from attempts to 'blow away' Blockade Flagship"));
                        }
                    }
            );
            actions.add(action);
        }

        Filter filter = Filters.and(Filters.starship, Filters.hasHyperdrive, Filters.at(Filters.sameSystemAs(self, Filters.and(Filters.your(self), Filters.battleship))));

        // Check condition(s)
        if (GameConditions.isDuringEitherPlayersPhase(game, Phase.DEPLOY)
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 3)
                && GameConditions.canTarget(game, self, filter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Reset starship's hyperspeed to 0");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose starship", filter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard starship) {
                            action.addAnimationGroup(starship);
                            // Pay cost(s)
                            action.appendCost(
                                    new UseForceEffect(action, playerId, 3));
                            // Allow response(s)
                            action.allowResponses("Reset " + GameUtils.getCardLink(starship) + "'s hyperspeed to 0",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            final PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                            // Perform result(s)
                                            action.appendEffect(
                                                    new ResetHyperspeedUntilEndOfTurnEffect(action, finalTarget, 0));
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