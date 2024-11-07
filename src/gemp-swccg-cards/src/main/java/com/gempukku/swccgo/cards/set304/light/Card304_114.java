package com.gempukku.swccgo.cards.set304.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
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
 * Set: The Great Hutt Expansion
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Slithering Back
 */
public class Card304_114 extends AbstractUsedOrLostInterrupt {
    public Card304_114() {
        super(Side.LIGHT, 4, "Slithering Back", Uniqueness.UNIQUE, ExpansionSet.GREAT_HUTT_EXPANSION, Rarity.V);
        setLore("Sometimes, no matter how you try, the Hutts keep slithering back.");
        setGameText("USED: Take Ferfiek Chawa or Gaius into hand from Reserve Deck; reshuffle. OR Place a character from hand on Used Pile to activate 1 Force (2 if Gaius on table). LOST: Once per game, exchange two cards in hand with any one card in Lost Pile. [Immune to Sense.]");
        addImmuneToCardTitle(Title.Sense);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        GameTextActionId gameTextActionId = GameTextActionId.SLITHERING_BACK__UPLOAD_CARD;

        // Check condition(s)
        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.USED);
            action.setText("Take Gaius the Hutt or Ferfiek Chawa into hand from Reserve Deck");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(Filters.Gaius, Filters.FERFIEK_CHAWA), true));
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
                            int forceToActivate = GameConditions.canSpot(game, self, Filters.Gaius) ? 2 : 1;
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

        GameTextActionId gameTextActionId2 = GameTextActionId.SLITHERING_BACK__EXCHANGE;

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
