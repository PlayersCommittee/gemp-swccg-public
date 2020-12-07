package com.gempukku.swccgo.cards.set213.light;

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
import com.gempukku.swccgo.logic.effects.choose.DrawCardsIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 13
 * Type: Interrupt
 * Subtype: Used
 * Title: I've Got A Really Good Feeling About This
 */
public class Card213_053 extends AbstractUsedInterrupt {
    public Card213_053() {
        super(Side.LIGHT, 4, "I've Got A Really Good Feeling About This", Uniqueness.UNIQUE);
        setLore("");
        setGameText("If you have two smugglers on table, choose one: If opponent has more cards in hand than you, draw one card from Reserve Deck (two if [Set 13] Han on table). OR If there are more [Dark Side] than [Light Side] on table, activate 1 Force (2 if [Set 13] Han on table). OR Re-circulate.");
        addIcons(Icon.VIRTUAL_SET_13);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        String opponent = game.getOpponent(playerId);
        GameState gameState = game.getGameState();

        final boolean v13HanOnTable = GameConditions.canSpot(game, self, Filters.and(Filters.icon(Icon.VIRTUAL_SET_13), Filters.Han));
        String numCardsText = v13HanOnTable ? String.valueOf(2) : String.valueOf(1);

        if (GameConditions.canSpot(game, self, 2, Filters.and(Filters.your(playerId), Filters.smuggler))) {
            if (gameState.getHand(opponent).size() > gameState.getHand(playerId).size()
                    && GameConditions.hasReserveDeck(game, playerId)) {
                if (v13HanOnTable) {
                    numCardsText = String.valueOf(2);
                }
                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Draw " + numCardsText + " card" + (numCardsText.equals(String.valueOf(1)) ? "" : "s"));
                // Allow response(s)
                action.allowResponses(
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s)
                                action.appendEffect(
                                        new DrawCardsIntoHandFromReserveDeckEffect(action, playerId, v13HanOnTable ? 2 : 1)
                                );
                            }
                        }
                );
                actions.add(action);
            }

            if (game.getModifiersQuerying().getTotalForceIconCount(gameState, opponent) > game.getModifiersQuerying().getTotalForceIconCount(gameState, playerId)) {
                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Activate " + numCardsText + " Force");
                // Allow response(s)
                action.allowResponses(
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s)
                                action.appendEffect(
                                        new ActivateForceEffect(action, playerId, v13HanOnTable ? 2 : 1)
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
