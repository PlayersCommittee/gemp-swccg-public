package com.gempukku.swccgo.cards.set4.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.PutCardsFromHandOnForcePileEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.List;


/**
 * Set: Dagobah
 * Type: Interrupt
 * Subtype: Used
 * Title: Yoda Stew
 */
public class Card4_072 extends AbstractUsedInterrupt {
    public Card4_072() {
        super(Side.LIGHT, 3, Title.Yoda_Stew, Uniqueness.UNIQUE, ExpansionSet.DAGOBAH, Rarity.U);
        setLore("'For the Jedi it is time to eat as well.'");
        setGameText("During opponent's turn, take up to four cards from your hand and place them on top of your Force Pile.");
        addIcons(Icon.DAGOBAH);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, PhysicalCard self) {
        // Check condition(s)
        if (GameConditions.isOpponentsTurn(game, self)
                && GameConditions.hasInHand(game, playerId, Filters.not(self))) {
            final int numberOnCard = game.getModifiersQuerying().isDoubled(game.getGameState(), self) ? 8 : 4;

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Put cards from hand on Force Pile");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new PutCardsFromHandOnForcePileEffect(action, playerId, 1, numberOnCard));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}