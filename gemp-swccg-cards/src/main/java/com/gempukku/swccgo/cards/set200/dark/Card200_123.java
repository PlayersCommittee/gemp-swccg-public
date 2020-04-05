package com.gempukku.swccgo.cards.set200.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.CancelDestinyAndCauseRedrawEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.ExchangeCardInHandWithCardInLostPileEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeDestinyCardIntoHandEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;


/**
 * Set: Set 0
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Sith Fury (V)
 */
public class Card200_123 extends AbstractUsedOrLostInterrupt {
    public Card200_123() {
        super(Side.DARK, 4, Title.Sith_Fury, Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("At his peak, no one could stand up to the Dark Lord of the Sith. His superior tactics devastated those who opposed him.");
        setGameText("USED: If you just drew a character for destiny, take that card into hand to cancel and redraw that destiny. LOST: Once per game, exchange a Dark Jedi in hand with a Dark Jedi in Lost Pile.");
        addIcons(Icon.TATOOINE, Icon.VIRTUAL_SET_0);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        GameTextActionId gameTextActionId = GameTextActionId.ANY_CARD__CANCEL_AND_REDRAW_A_DESTINY;

        // Check condition(s)
        if (TriggerConditions.isDestinyJustDrawnBy(game, effectResult, playerId)
                && GameConditions.canCancelDestinyAndCauseRedraw(game, playerId)
                && GameConditions.isDestinyCardMatchTo(game, Filters.character)
                && GameConditions.canTakeDestinyCardIntoHand(game, playerId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.USED);
            action.setText("Take destiny card into hand and cause re-draw");
            // Pay cost(s)
            action.appendEffect(
                    new TakeDestinyCardIntoHandEffect(action));
            // Allow response(s)
            action.allowResponses("Cancel destiny and cause re-draw",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new CancelDestinyAndCauseRedrawEffect(action));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        GameTextActionId exchangeCardActionId = GameTextActionId.SITH_FURY__EXCHANGE_CARD;
        // Check condition(s)
        if (GameConditions.hasInHand(game, playerId, Filters.Dark_Jedi)
                && GameConditions.canTakeCardsIntoHandFromLostPile(game, playerId, self, exchangeCardActionId)
                && GameConditions.isOncePerGame(game, self, exchangeCardActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, exchangeCardActionId, CardSubtype.LOST);
            action.setText("Exchange card in hand");
            action.setActionMsg("Exchange Dark Jedi in hand with a Dark Jedi in lost pile.");

            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));

            // Allow Responses
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new ExchangeCardInHandWithCardInLostPileEffect(action, playerId, Filters.Dark_Jedi, Filters.Dark_Jedi));
                        }
                    });

            return Collections.singletonList(action);
        }
        return null;
    }
}