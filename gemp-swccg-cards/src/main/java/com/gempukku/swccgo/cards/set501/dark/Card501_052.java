package com.gempukku.swccgo.cards.set501.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromHandOnUsedPileEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.ExchangeCardsInHandWithCardInLostPileEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 13
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: I Never Ask For Anything Twice
 */
public class Card501_052 extends AbstractUsedOrLostInterrupt {
    public Card501_052() {
        super(Side.DARK, 4, "I Never Ask For Anything Twice", Uniqueness.UNIQUE);
        setLore("");
        setGameText("USED: Take Dryden Vos or First Light into hand from Reserve Deck; reshuffle. OR Place a character from hand on Used pile to activate 1 Force (2 if Vos on table). LOST: Once per game, may exchange 2 cards in hand for 1 card in Lost pile (Immune to Sense).");
        addIcons(Icon.VIRTUAL_SET_13);
        addImmuneToCardTitle(Title.Alter);
        setTestingText("I Never Ask For Anything Twice");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        GameTextActionId gameTextActionId = GameTextActionId.I_NEVER_ASK_FOR_ANYTHING_TWICE__UPLOAD_CARD;

        // Check condition(s)
        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.USED);
            action.setText("Take Dryden Vos or First Light into hand from Reserve Deck");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(Filters.Vos, Filters.First_Light), true));
                        }
                    }
            );
            actions.add(action);
        }

        // Check condition(s)
        if (GameConditions.hasInHand(game, playerId, Filters.character)
                && GameConditions.hasReserveDeck(game, playerId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Place a character from hand on Used pile");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            int forceToActivate = GameConditions.canSpot(game, self, Filters.Vos) ? 2 : 1;
                            // Perform result(s)
                            action.appendEffect(
                                    new PutCardFromHandOnUsedPileEffect(action, playerId, Filters.character, false));
                            action.appendEffect(
                                    new ActivateForceEffect(action, playerId, forceToActivate)
                            );
                        }
                    }
            );
            actions.add(action);
        }

        GameTextActionId gameTextActionId2 = GameTextActionId.I_NEVER_ASK_FOR_ANYTHING_TWICE__EXCHANGE;

        if (GameConditions.isOncePerGame(game, self, gameTextActionId2)
                && GameConditions.numCardsInHand(game, playerId) >= 2
                && GameConditions.hasLostPile(game, playerId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId2, CardSubtype.LOST);
            action.setText("Exchange cards with card in Lost Pile");
            action.setActionMsg("Exchange two cards in hand with a card in Lost Pile");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new ExchangeCardsInHandWithCardInLostPileEffect(action, playerId, 2, 2));
                        }
                    }
            );
            actions.add(action);
        }

        return actions;
    }
}
