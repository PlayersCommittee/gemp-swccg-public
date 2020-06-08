package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.RetrieveCardEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premiere
 * Type: Interrupt
 * Subtype: Lost
 * Title: The Empire's Back
 */
public class Card1_272 extends AbstractLostInterrupt {
    public Card1_272() {
        super(Side.DARK, 3, "The Empire's Back");
        setLore("'No star system will dare oppose the Emperor now.'");
        setGameText("If Vader, Tarkin, Motti or Tagge is lost, use 2 Force to retrieve one of them from the Lost Pile. OR Use 4 Force to search through your Reserve Deck and take Vader, Tarkin, Motti, or Tagge into your hand. Shuffle deck, cut and replace.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        final Filter filter = Filters.or(Filters.Vader, Filters.Tarkin, Filters.Motti, Filters.Tagge);

        GameTextActionId gameTextActionId = GameTextActionId.THE_EMPIRES_BACK__RETRIEVE_VADER_TARKIN_MOTTI_OR_TAGGE;

        // Check condition(s)
        if (GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 2)
                && GameConditions.canSearchLostPile(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Retrieve Vader, Tarkin, Motti, or Tagge");
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 2));
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new RetrieveCardEffect(action, playerId, filter));
                        }
                    }
            );
            actions.add(action);
        }

        gameTextActionId = GameTextActionId.THE_EMPIRES_BACK__UPLOAD_VADER_TARKIN_MOTTI_OR_TAGGE;

        // Check condition(s)
        if (GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 4)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 4));
            // Allow response(s)
            action.allowResponses("Take Vader, Tarkin, Motti, or Tagge into hand from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, filter, true));
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }
}