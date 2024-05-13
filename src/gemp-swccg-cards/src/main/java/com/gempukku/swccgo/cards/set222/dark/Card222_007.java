package com.gempukku.swccgo.cards.set222.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.ExchangeCardsInHandWithCardInForcePileEffect;
import com.gempukku.swccgo.logic.effects.choose.PlaceCardOutOfPlayFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 22
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Force Push & Podracer Collision
 */
public class Card222_007 extends AbstractUsedOrLostInterrupt {
    public Card222_007() {
        super(Side.DARK, 4, "Force Push & Podracer Collision", Uniqueness.UNIQUE, ExpansionSet.SET_22, Rarity.V);
        addComboCardTitles(Title.Force_Push, Title.Podracer_Collision);
        setGameText("USED: If you just verified opponent's Reserve Deck, search that Reserve Deck and place one Interrupt (except Houjix) found there out of play. " +
                "LOST: Once per game, exchange two cards from hand with any one card from Force Pile; reshuffle. Immune to Too Close For Comfort.");
        addIcons(Icon.EPISODE_I, Icon.VIRTUAL_SET_22);
        addImmuneToCardTitle("Too Close For Comfort");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();
        final String opponent = game.getOpponent(playerId);

        GameTextActionId gameTextActionId = GameTextActionId.FORCE_PUSH_PODRACER_COLLISION__SEARCH_OPPONENT_RESERVE_DECK;

        // Check condition(s)
        if (TriggerConditions.justVerifiedOpponentsReserveDeck(game, effectResult, playerId)
                && GameConditions.canSearchOpponentsReserveDeck(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.USED);
            action.setText("Place an Interrupt from Reserve Deck out of play");
            // Allow response(s)
            action.allowResponses("Search opponent's Reserve Deck and place an Interrupt (except Houjix) found there out of play",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new PlaceCardOutOfPlayFromReserveDeckEffect(action, playerId, opponent, Filters.and(Filters.Interrupt, Filters.not(Filters.Houjix)), false));
                        }
                    }
            );
            actions.add(action);
        }

        return actions;
    }


    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();
        GameTextActionId gameTextActionId = GameTextActionId.FORCE_PUSH__EXCLUDE_CHARACTERS_OR_EXCHANGE_CARDS;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.hasInHand(game, playerId, 2, Filters.not(self))
                && GameConditions.hasForcePile(game, playerId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.LOST);
            action.setText("Exchange cards with card in Force Pile");
            action.setActionMsg("Exchange two cards in hand with a card in Force Pile");
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
                                    new ExchangeCardsInHandWithCardInForcePileEffect(action, playerId, 2, 2, true));
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }
}