package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.complete.ChooseExistingCardPileEffect;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.ShufflePileEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.List;

/**
 * Set: Premiere
 * Type: Interrupt
 * Subtype: Used
 * Title: Ommni Box
 */
public class Card1_262 extends AbstractUsedInterrupt {
    public Card1_262() {
        super(Side.DARK, 5, Title.Ommni_Box);
        setLore("Difficult-to-play instrument which enhances music. Tech Mo'r plays one for Figrin D'an and the Modal Nodes in the Mos Eisley Cantina.");
        setGameText("Shuffle any player's Reserve Deck or Lost Pile or Used Pile.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (GameConditions.hasReserveDeck(game, playerId) || GameConditions.hasReserveDeck(game, opponent)
                || GameConditions.hasLostPile(game, playerId) || GameConditions.hasLostPile(game, opponent)
                || GameConditions.hasUsedPile(game, playerId) || GameConditions.hasUsedPile(game, opponent)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Shuffle card pile");
            // Choose target(s)
            action.appendTargeting(
                    new ChooseExistingCardPileEffect(action, playerId, Filters.or(Zone.RESERVE_DECK, Zone.LOST_PILE, Zone.USED_PILE)) {
                        @Override
                        protected void pileChosen(SwccgGame game, final String cardPileOwner, final Zone cardPile) {
                            // Allow response(s)
                            action.allowResponses("Shuffle " + cardPileOwner + "'s " + cardPile.getHumanReadable(),
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new ShufflePileEffect(action, cardPileOwner, cardPile));
                                        }
                                    }
                            );
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}