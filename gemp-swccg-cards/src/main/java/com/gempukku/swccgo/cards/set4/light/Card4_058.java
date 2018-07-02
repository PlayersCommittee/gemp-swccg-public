package com.gempukku.swccgo.cards.set4.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.ShuffleHandAndUsedPileIntoReserveDeckEffect;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DrawCardsIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.List;


/**
 * Set: Dagobah
 * Type: Interrupt
 * Subtype: Lost
 * Title: Recoil In Fear
 */
public class Card4_058 extends AbstractLostInterrupt {
    public Card4_058() {
        super(Side.LIGHT, 3, Title.Recoil_In_Fear);
        setLore("In combat, a bold and unexpected move can change the entire situation.");
        setGameText("Use 3 Force. Each player counts cards in hand, then places entire hand and Used Pile onto Reserve Deck. Shuffle, cut and replace. Each player then draws from Reserve Deck the counted number of cards to create a new hand.");
        addIcons(Icon.DAGOBAH);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 3)
                && GameConditions.hasHand(game, playerId)
                && GameConditions.hasHand(game, opponent)
                && !GameConditions.hasGameTextModification(game, self, ModifyGameTextType.RECOIL_IN_FEAR__MAY_NOT_BE_PLAYED_EXCEPT_TO_CANCEL_INTERRUPT)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Make each player redraw hand");
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 3));
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            GameState gameState = game.getGameState();
                            // Perform result(s)
                            if (game.getModifiersQuerying().mayNotRemoveCardsFromOpponentsHand(gameState, self, playerId)) {
                                game.getGameState().sendMessage(opponent + " is not allowed to remove cards from " + opponent + "'s hand");
                                return;
                            }
                            // Perform result(s)
                            int numCardsToDraw = game.getGameState().getHand(playerId).size();
                            int numOpponentsCardsToDraw = game.getGameState().getHand(opponent).size();
                            action.appendEffect(
                                    new ShuffleHandAndUsedPileIntoReserveDeckEffect(action, playerId));
                            action.appendEffect(
                                    new ShuffleHandAndUsedPileIntoReserveDeckEffect(action, opponent));
                            if (numCardsToDraw > 0) {
                                action.appendEffect(
                                        new DrawCardsIntoHandFromReserveDeckEffect(action, playerId, numCardsToDraw));
                            }
                            if (numOpponentsCardsToDraw > 0) {
                                action.appendEffect(
                                        new DrawCardsIntoHandFromReserveDeckEffect(action, opponent, numOpponentsCardsToDraw));
                            }
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}