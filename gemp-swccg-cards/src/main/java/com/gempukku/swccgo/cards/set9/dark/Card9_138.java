package com.gempukku.swccgo.cards.set9.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrStartingInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.ModifyNumCardsDrawnInStartingHandEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromHandOnForcePileEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromVoidInLostPileEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardAndCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.List;


/**
 * Set: Death Star II
 * Type: Interrupt
 * Subtype: Used Or Starting
 * Title: Operational As Planned
 */
public class Card9_138 extends AbstractUsedOrStartingInterrupt {
    public Card9_138() {
        super(Side.DARK, 5, Title.Operational_As_Planned, Uniqueness.UNIQUE);
        setLore("'We shall double our efforts.'");
        setGameText("USED: Place one card from hand on top of Force Pile. STARTING: If Endor system on table, take Death Star II system, Jerjerrod and any Effect into hand from Reserve Deck. When you draw your starting hand, draw only six more cards. Place Interrupt in Lost Pile.");
        addIcons(Icon.DEATH_STAR_II);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        // Check condition(s)
        if (GameConditions.hasInHand(game, playerId, Filters.not(self))) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Place a card from hand on Force Pile");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new PutCardFromHandOnForcePileEffect(action, playerId));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected PlayInterruptAction getGameTextStartingAction(final String playerId, SwccgGame game, final PhysicalCard self) {
        // Check condition(s)
        if (GameConditions.canSpotLocation(game, Filters.Endor_system)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.STARTING);
            action.setText("Take cards into hand from Reserve Deck");
            // Allow response(s)
            action.allowResponses("Take Death Star II system, Jerjerrod, and any Effect into hand from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardAndCardIntoHandFromReserveDeckEffect(action, playerId, Filters.Death_Star_II_system, Filters.Jerjerrod, Filters.Effect, false));
                            action.appendEffect(
                                    new ModifyNumCardsDrawnInStartingHandEffect(action, playerId, 6));
                            action.appendEffect(
                                    new PutCardFromVoidInLostPileEffect(action, playerId, self));
                        }
                    }
            );
            return action;
        }
        return null;
    }
}