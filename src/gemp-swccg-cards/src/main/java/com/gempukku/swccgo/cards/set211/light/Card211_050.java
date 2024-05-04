package com.gempukku.swccgo.cards.set211.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.ExchangeCardInHandWithCardInLostPileEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromLostPileEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 11
 * Type: Interrupt
 * Subtype: Used or Lost
 * Title: Where's Han?
 */
public class Card211_050 extends AbstractUsedOrLostInterrupt {
    public Card211_050() {
        super(Side.LIGHT, 4, "Where's Han?", Uniqueness.UNIQUE, ExpansionSet.SET_11, Rarity.V);
        setLore("");
        setGameText("USED: [Upload] Han's Dice or non-[Maintenance] Han. LOST: If you just lost a gambler, take that gambler into hand. OR Once per game, exchange a card in hand with a Resistance Agent or [Episode VII] Skywalker in Lost Pile.");
        addIcons(Icon.EPISODE_VII, Icon.VIRTUAL_SET_11);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // LOST: Once per game, exchange a card in hand with a Resistance Agent or [Episode VII] Skywalker in Lost Pile.
        GameTextActionId exchangeCardActionId = GameTextActionId.WHERES_HAN_EXCHANGE_CARD;
        Filter ep7Skywalker = Filters.and(Filters.Skywalker, Icon.EPISODE_VII);
        final Filter ep7SkywalkerOrResistanceAgent = Filters.or(ep7Skywalker, Filters.Resistance_Agent);

        // USED: /\ Han's Dice or non-[Maintenance] Han.
        GameTextActionId uploadCardGametextActionId = GameTextActionId.WHERES_HAN_UPLOAD_CARD;

        final Filter nonMaintHanOrHansDice = Filters.or(
                Filters.and(Filters.Han, Filters.except(Icon.MAINTENANCE)),
                Filters.Hans_Dice
        );

        // Check condition(s)
        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, uploadCardGametextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, uploadCardGametextActionId, CardSubtype.USED);
            action.setText("Upload Han's Dice or Han");
            action.setActionMsg("Upload Han's Dice or non-[Maintenance] Han");

            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, nonMaintHanOrHansDice, true));
                        }
                    }
            );
            actions.add(action);
        }

        // Check condition(s)
        if (GameConditions.hasHand(game, playerId) &&
            GameConditions.hasInHand(game, playerId, 2, Filters.any) &&
            GameConditions.canTakeCardsIntoHandFromLostPile(game, playerId, self, exchangeCardActionId) &&
            GameConditions.isOncePerGame(game, self, exchangeCardActionId))
        {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, exchangeCardActionId, CardSubtype.LOST);
            action.setText("Exchange card in hand");
            action.setActionMsg("Exchange a card in hand with a Resistance Agent or [Episode VII] Skywalker in Lost Pile");

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
                                    new ExchangeCardInHandWithCardInLostPileEffect(action, playerId, Filters.any, ep7SkywalkerOrResistanceAgent));


                        }
                    });

            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (TriggerConditions.justLost(game, effectResult, Filters.and(Filters.your(self), Filters.gambler))) {
            final PhysicalCard justLostCard = ((LostFromTableResult) effectResult).getCard();

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Take " + GameUtils.getFullName(justLostCard) + " into hand");
            // Allow response(s)
            action.allowResponses("Take " + GameUtils.getCardLink(justLostCard) + " into hand",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromLostPileEffect(action, playerId, justLostCard, false, true));
                        }
                    }
            );
            return Collections.singletonList(action);
        }

        return actions;
    }
}