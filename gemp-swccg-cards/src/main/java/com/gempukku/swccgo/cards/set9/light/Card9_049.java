package com.gempukku.swccgo.cards.set9.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PeekAtTopCardOfReserveDeckEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromReserveDeckOnBottomOfCardPileEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.List;


/**
 * Set: Death Star II
 * Type: Interrupt
 * Subtype: Used
 * Title: Critical Error Revealed
 */
public class Card9_049 extends AbstractUsedInterrupt {
    public Card9_049() {
        super(Side.LIGHT, 4, "Critical Error Revealed", Uniqueness.UNIQUE);
        setLore("Hologram technology allows efficient communication of complex intelligence during war room briefings.");
        setGameText("If you have a leader at your war room, peek at the top card of opponent's Reserve Deck. You may place it on bottom of that Reserve Deck.");
        addIcons(Icon.DEATH_STAR_II);
        addKeywords(Keyword.HOLOGRAM);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (GameConditions.canSpot(game, self, Filters.and(Filters.your(self), Filters.leader, Filters.at(Filters.and(Filters.your(self), Filters.war_room))))
                && GameConditions.hasReserveDeck(game, opponent)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Peek at top of opponent's Reserve Deck");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new PeekAtTopCardOfReserveDeckEffect(action, playerId, opponent) {
                                        @Override
                                        protected void cardPeekedAt(final PhysicalCard peekedAtCard) {
                                            action.appendEffect(
                                                    new PlayoutDecisionEffect(action, playerId,
                                                            new YesNoDecision("Do you want to place " + GameUtils.getCardLink(peekedAtCard) + " on bottom of Reserve Deck?") {
                                                                @Override
                                                                protected void yes() {
                                                                    action.appendEffect(
                                                                            new PutCardFromReserveDeckOnBottomOfCardPileEffect(action, peekedAtCard, Zone.RESERVE_DECK, true));
                                                                }
                                                                protected void no() {
                                                                    game.getGameState().sendMessage(playerId + " chooses to not place card on bottom of Reserve Deck");
                                                                }
                                                            }
                                                    ));
                                        }
                                    });
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}