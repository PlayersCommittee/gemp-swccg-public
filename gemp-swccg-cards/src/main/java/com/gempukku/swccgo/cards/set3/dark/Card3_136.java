package com.gempukku.swccgo.cards.set3.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardsIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.List;


/**
 * Set: Hoth
 * Type: Interrupt
 * Subtype: Lost
 * Title: Tactical Support
 */
public class Card3_136 extends AbstractLostInterrupt {
    public Card3_136() {
        super(Side.DARK, 4, Title.Tactical_Support, Uniqueness.UNIQUE);
        setLore("Highly organized Imperial infantry units can mobilize with incredible speed, often putting their surprised adversaries on the defensive.");
        setGameText("Lose 1 Force to search through your Reserve Deck and take up to three troopers into your hand. Shuffle, cut and replace.");
        addIcons(Icon.HOTH);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        GameTextActionId gameTextActionId = GameTextActionId.TACTICAL_SUPPORT__UPLOAD_TROOPERS;

        // Check condition(s)
        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Take troopers into hand from Reserve Deck");
            // Pay cost(s)
            action.appendCost(
                    new LoseForceEffect(action, playerId, 1, true));
            // Allow response(s)
            action.allowResponses("Take up to three troopers into hand from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardsIntoHandFromReserveDeckEffect(action, playerId, 1, 3, Filters.trooper, true));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}