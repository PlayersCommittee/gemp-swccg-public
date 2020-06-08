package com.gempukku.swccgo.cards.set5.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddBattleDestinyEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Cloud City
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: I Don't Need Their Scum, Either
 */
public class Card5_051 extends AbstractUsedOrLostInterrupt {
    public Card5_051() {
        super(Side.LIGHT, 5, "I Don't Need Their Scum, Either", Uniqueness.UNIQUE);
        setLore("Of all the scum and villainy Lando had dealt with (pirates, smugglers, con-artists, thieves, swindlers, politicians and Imperial lackeys), he hated bounty hunters the most.");
        setGameText("USED: Search your Reserve Deck, take one We Don't Need Their Scum into hand and reshuffle. LOST: If a bounty hunter and your Lando are involved in the same battle, you may add one battle destiny (add two if bounty hunter is Boba Fett).");
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        GameTextActionId gameTextActionId = GameTextActionId.I_DONT_NEED_THEIR_SCUM_EITHER__UPLOAD_WE_DONT_NEED_THEIR_SCUM;

        // Check condition(s)
        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.USED);
            action.setText("Take card into hand from Reserve Deck");
            // Allow response(s)
            action.allowResponses("Take a We Don't Need Their Scum into hand from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.We_Dont_Need_Their_Scum, true));
                        }
                    }
            );
            actions.add(action);
        }

        // Check condition(s)
        if (GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.your(self), Filters.Lando))
                && GameConditions.canAddBattleDestinyDraws(game, self)) {
            if (GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.bounty_hunter, Filters.not(Filters.Boba_Fett)))) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
                action.setText("Add one battle destiny");
                // Allow response(s)
                action.allowResponses(
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s)
                                action.appendEffect(
                                        new AddBattleDestinyEffect(action, 1));
                            }
                        }
                );
                actions.add(action);
            }
            if (GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.bounty_hunter, Filters.Boba_Fett))) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
                action.setText("Add two battle destiny");
                // Allow response(s)
                action.allowResponses(
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s)
                                action.appendEffect(
                                        new AddBattleDestinyEffect(action, 2));
                            }
                        }
                );
                actions.add(action);
            }
        }
        return actions;
    }
}