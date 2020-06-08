package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.ChoosePlayerBySideEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: A New Hope
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Blue Milk
 */
public class Card2_046 extends AbstractUsedOrLostInterrupt {
    public Card2_046() {
        super(Side.LIGHT, 4, Title.Blue_Milk, Uniqueness.UNIQUE);
        setLore("Nutrient-rich beverage common in moisture farm communities. Rumored to have medicinal qualities. Popular in cantinas among those who can't hold their juri juice.");
        setGameText("USED: Select a player to activate 1 Force. LOST: Cancel Juri Juice or Hypo.");
        addIcons(Icon.A_NEW_HOPE);
    }

    @Override
    public List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        final String opponent = game.getOpponent(playerId);
        boolean playerCanActivate = GameConditions.canActivateForce(game, playerId);
        boolean opponentCanActivate = GameConditions.canActivateForce(game, opponent);
        final int numberOnCard = game.getModifiersQuerying().isDoubled(game.getGameState(), self) ? 2 : 1;

        // Check condition(s)
        if (playerCanActivate && opponentCanActivate) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Select player to activate Force");
            // Choose target(s)
            action.appendTargeting(
                    new ChoosePlayerBySideEffect(action, playerId) {
                        @Override
                        protected void playerChosen(SwccgGame game, final String playerChosen) {
                            // Allow response(s)
                            action.allowResponses(playerChosen.equals(playerId) ? ("Activate " + numberOnCard + " Force") : ("Make " + playerChosen + " activate " + numberOnCard + " Force"),
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new ActivateForceEffect(action, playerChosen, numberOnCard));
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(action);
        }
        else if (playerCanActivate) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Activate " + numberOnCard + " Force");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new ActivateForceEffect(action, playerId, numberOnCard));
                        }
                    }
            );
            actions.add(action);
        }
        else if (opponentCanActivate) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Make opponent activate " + numberOnCard + " Force");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new ActivateForceEffect(action, opponent, numberOnCard));
                        }
                    }
            );
            actions.add(action);
        }

        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Juri_Juice)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Juri_Juice, Title.Juri_Juice);
            actions.add(action);
        }

        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Hypo)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Hypo, Title.Hypo);
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.or(Filters.Juri_Juice, Filters.Hypo))
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }
}