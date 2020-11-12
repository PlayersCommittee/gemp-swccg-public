package com.gempukku.swccgo.cards.set213.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.PutStackedCardInLostPileEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseStackedCardEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 13
 * Type: Interrupt
 * Subtype: Used or Lost
 * Title: He's The Best Smuggler Around
 */
public class Card213_051 extends AbstractUsedOrLostInterrupt {
    public Card213_051() {
        super(Side.LIGHT, 4, "He's The Best Smuggler Around", Uniqueness.UNIQUE);
        setLore("");
        setGameText("USED: Take Lando, L3-37, or Kessel Run, into hand from Reserve Deck; reshuffle. OR If you just placed a Coaxium card on Used pile randomly retrieve 1 Force." +
                "LOST: Once per game, may place a card stacked on [Set D] A Useless Gesture in your Lost Pile.)");
        addIcons(Icon.VIRTUAL_SET_13);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId1 = GameTextActionId.HES_THE_BEST_SMUGGLER_AROUND__UPLOAD_CARD;

        // Check condition(s)
        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId1)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId1, CardSubtype.USED);
            action.setText("Take Lando, L3-37, or Kessel Run into hand from Reserve Deck");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(Filters.Lando, Filters.L3_37, Filters.Kessel_Run), true));
                        }
                    }
            );
            actions.add(action);
        }

        GameTextActionId gameTextActionId2 = GameTextActionId.HES_THE_BEST_SMUGGLER_AROUND__REMOVE_STACKED_CARD;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId2)
                && GameConditions.canSpot(game, self, Filters.and(Filters.A_Useless_Gesture, Filters.icon(Icon.VIRTUAL_DEFENSIVE_SHIELD)))) {
            final PhysicalCard auselessgesturev = Filters.findFirstActive(game, self, Filters.and(Filters.A_Useless_Gesture, Filters.icon(Icon.VIRTUAL_DEFENSIVE_SHIELD)));
            if (GameConditions.hasStackedCards(game, auselessgesturev)) {
                final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId2, CardSubtype.LOST);
                action.setText("Place a card stacked on A Useless Gesture in your Lost Pile.");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerGameEffect(action));
                // Allow response(s)
                action.allowResponses(
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s)
                                action.appendEffect(
                                        new ChooseStackedCardEffect(action, playerId, auselessgesturev) {
                                            @Override
                                            protected void cardSelected(PhysicalCard selectedCard) {
                                                action.appendEffect(
                                                        new PutStackedCardInLostPileEffect(action, playerId, selectedCard, false)
                                                );
                                            }
                                        }
                                );

                            }
                        }
                );
                actions.add(action);
            }
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        if (TriggerConditions.justPutCoaxiumCardInCardPile(effectResult, Zone.USED_PILE)
                && GameConditions.hasLostPile(game, playerId)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Randomly retrieve 1 force");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new RetrieveForceEffect(action, playerId, 1, true));
                        }
                    }
            );
            return Collections.singletonList(action);
        }

        return null;
    }
}