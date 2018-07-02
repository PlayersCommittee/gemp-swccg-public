package com.gempukku.swccgo.cards.set9.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddBattleDestinyEffect;
import com.gempukku.swccgo.cards.effects.DrawsNoMoreThanBattleDestinyEffect;
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
 * Set: Death Star II
 * Type: Interrupt
 * Subtype: Used
 * Title: Imperial Command
 */
public class Card9_137 extends AbstractUsedInterrupt {
    public Card9_137() {
        super(Side.DARK, 4, Title.Imperial_Command, Uniqueness.UNIQUE);
        setLore("The Emperor's high command is subjected to close scrutiny by the Imperial bureaucracy. Despite this apparent lack of trust, many turn out to be fine commanders.");
        setGameText("Take one admiral or general into hand from Reserve Deck; reshuffle. OR If your admiral is in battle at a system (or your general is in battle at a site), you may either add one battle destiny or prevent opponent from drawing more than one battle destiny.");
        addIcons(Icon.DEATH_STAR_II);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        GameTextActionId gameTextActionId = GameTextActionId.IMPERIAL_COMMAND__UPLOAD_ADMIRAL_OR_GENERAL;

        // Check condition(s)
        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            // Allow response(s)
            action.allowResponses("Take an admiral or general into hand from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(Filters.admiral, Filters.general, Filters.grantedMayBeTargetedBy(self)), true));
                        }
                    }
            );
            actions.add(action);
        }

        // Check condition(s)
        if (GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.your(self), Filters.or(Filters.admiral, Filters.grantedMayBeTargetedBy(self)), Filters.at(Filters.system)))
                || GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.your(self), Filters.general, Filters.at(Filters.site)))) {
            final String opponent = game.getOpponent(playerId);
            if (GameConditions.canAddBattleDestinyDraws(game, self)) {

                final PlayInterruptAction action1 = new PlayInterruptAction(game, self);
                action1.setText("Add one battle destiny");
                // Allow response(s)
                action1.allowResponses(
                        new RespondablePlayCardEffect(action1) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s)
                                action1.appendEffect(
                                        new AddBattleDestinyEffect(action1, 1));
                            }
                        }
                );
                actions.add(action1);
            }

            final PlayInterruptAction action2 = new PlayInterruptAction(game, self);
            action2.setText("Limit opponent to one battle destiny");
            // Allow response(s)
            action2.allowResponses("Prevent opponent from drawing more than one battle destiny",
                    new RespondablePlayCardEffect(action2) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action2.appendEffect(
                                    new DrawsNoMoreThanBattleDestinyEffect(action2, opponent, 1));
                        }
                    }
            );
            actions.add(action2);
        }
        return actions;
    }
}