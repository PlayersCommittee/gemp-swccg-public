package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
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
 * Title: Luke's Back
 */
public class Card1_095 extends AbstractLostInterrupt {
    public Card1_095() {
        super(Side.LIGHT, 3, "Luke's Back", Uniqueness.UNRESTRICTED, ExpansionSet.PREMIERE, Rarity.U2);
        setLore("'It looks like I'm going nowhere.'");
        setGameText("If Luke is lost, use 1 Force to retrieve him from the Lost Pile. OR Use 3 Force to search through your Reserve Deck and take Luke into your hand. Shuffle deck, cut and replace.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        GameTextActionId gameTextActionId = GameTextActionId.LUKES_BACK__RETRIEVE_LUKE;

        // Check condition(s)
        if (GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1)
                && GameConditions.canSearchLostPile(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Retrieve Luke");
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new RetrieveCardEffect(action, playerId, Filters.Luke));
                        }
                    }
            );
            actions.add(action);
        }

        gameTextActionId = GameTextActionId.LUKES_BACK__UPLOAD_LUKE;

        // Check condition(s)
        if (GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 3)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Take Luke into hand from Reserve Deck");
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 3));
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.Luke, true));
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }
}