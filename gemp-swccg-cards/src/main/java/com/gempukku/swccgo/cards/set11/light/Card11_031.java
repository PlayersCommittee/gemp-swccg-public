package com.gempukku.swccgo.cards.set11.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.ModifyDestinyEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.PlaceCardOutOfPlayFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Tatooine
 * Type: Interrupt
 * Subtype: Used
 * Title: Changing The Odds
 */
public class Card11_031 extends AbstractUsedInterrupt {
    public Card11_031() {
        super(Side.LIGHT, 5, "Changing The Odds", Uniqueness.UNIQUE);
        setLore("Qui-Gon used his Jedi abilities to insure that there was no 'chance' involved with the use of Watto's chance cube.");
        setGameText("If opponent just drew battle destiny (or destiny for Watto's Chance Cube), subtract one from that destiny. OR If you just verified opponent's Reserve Deck, search that Reserve Deck and place one Interrupt found there out of play.");
        addIcons(Icon.TATOOINE, Icon.EPISODE_I);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.isBattleDestinyJustDrawnBy(game, effectResult, opponent)
                || TriggerConditions.isDestinyJustDrawnFor(game, effectResult, opponent, Filters.Wattos_Chance_Cube)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Subtract 1 from destiny");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new ModifyDestinyEffect(action, -1));
                        }
                    }
            );
            actions.add(action);
        }

        GameTextActionId gameTextActionId = GameTextActionId.CHANGING_THE_ODDS__SEARCH_OPPONENT_RESERVE_DECK;

        // Check condition(s)
        if (TriggerConditions.justVerifiedOpponentsReserveDeck(game, effectResult, playerId)
                && GameConditions.canSearchOpponentsReserveDeck(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Place an Interrupt from Reserve Deck out of play");
            // Allow response(s)
            action.allowResponses("Search opponent's Reserve Deck and place an Interrupt found there out of play",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new PlaceCardOutOfPlayFromReserveDeckEffect(action, playerId, opponent, Filters.Interrupt, false));
                        }
                    }
            );
            actions.add(action);
        }

        return actions;
    }
}