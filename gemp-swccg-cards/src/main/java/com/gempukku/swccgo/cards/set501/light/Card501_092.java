package com.gempukku.swccgo.cards.set501.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.effects.RecirculateEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.RetrieveCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DrawCardsIntoHandFromForcePileEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 13
 * Type: Interrupt
 * Subtype: Used
 * Title: I've Got A Really Good Feeling About This
 */
public class Card501_092 extends AbstractUsedInterrupt {
    public Card501_092() {
        super(Side.LIGHT, 4, "I've Got A Really Good Feeling About This", Uniqueness.UNIQUE);
        setLore("");
        setGameText("If you have two smugglers on table, choose one: If your opponent has more cards in hand, draw 2 cards. OR If opponent has less cards in Lost Pile, retreive a freighter. OR If more [DS] than [LS] on table, activate 2 Force. OR Recirculate.");
        addIcons(Icon.VIRTUAL_SET_13);
        setTestingText("I've Got A Really Good Feeling About This");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        String opponent = game.getOpponent(playerId);
        GameState gameState = game.getGameState();

        if (GameConditions.canSpot(game, self, 2, Filters.and(Filters.your(playerId), Filters.smuggler))) {
            if (gameState.getHand(opponent).size() > gameState.getHand(playerId).size()) {
                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Draw 2 cards");
                // Allow response(s)
                action.allowResponses(
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s)
                                action.appendEffect(
                                        new DrawCardsIntoHandFromForcePileEffect(action, playerId, 2)
                                );
                            }
                        }
                );
                actions.add(action);
            }

            if (gameState.getLostPile(opponent).size() < gameState.getLostPile(playerId).size()) {
                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Retrieve a freighter");
                // Allow response(s)
                action.allowResponses(
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s)
                                action.appendEffect(
                                        new RetrieveCardEffect(action, playerId, Filters.freighter)
                                );
                            }
                        }
                );
                actions.add(action);
            }


            if (game.getModifiersQuerying().getTotalForceIconCount(gameState, opponent) > game.getModifiersQuerying().getTotalForceIconCount(gameState, playerId)) {
                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Activate 2 Force");
                // Allow response(s)
                action.allowResponses(
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s)
                                action.appendEffect(
                                        new ActivateForceEffect(action, playerId, 2)
                                );
                            }
                        }
                );
                actions.add(action);
            }

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Recirculate");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new RecirculateEffect(action, playerId)
                            );
                        }
                    }
            );
            actions.add(action);
        }

        return actions;
    }
}
