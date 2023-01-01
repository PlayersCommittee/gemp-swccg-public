package com.gempukku.swccgo.cards.set208.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.PlaceDestinyCardOnTopOfReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.ShuffleReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromUsedPileEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 8
 * Type: Interrupt
 * Subtype: Lost
 * Title: You Swindled Me! (V)
 */
public class Card208_047 extends AbstractUsedInterrupt {
    public Card208_047() {
        super(Side.DARK, 4, "You Swindled Me!", Uniqueness.UNIQUE, ExpansionSet.SET_8, Rarity.V);
        setVirtualSuffix(true);
        setLore("Needless to say, Watto was not happy about his loss.");
        setGameText("Place opponent's just drawn destiny on top of their Reserve Deck; reshuffle. OR If opponent just lost Force from your [Episode I] objective, take any one card from Used Pile into hand; reshuffle.");
        addIcons(Icon.TATOOINE, Icon.EPISODE_I, Icon.VIRTUAL_SET_8);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.isDestinyJustDrawnBy(game, effectResult, opponent)
                && GameConditions.canPlaceDestinyCardInCardPile(game)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Place destiny card on top of Reserve Deck");
            // Allow response(s)
            action.allowResponses("Place destiny card on top of opponent's Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new PlaceDestinyCardOnTopOfReserveDeckEffect(action));
                            action.appendEffect(
                                    new ShuffleReserveDeckEffect(action, playerId, opponent));
                        }
                    });
            actions.add(action);
        }

        GameTextActionId gameTextActionId = GameTextActionId.YOU_SWINDLED_ME__UPLOAD_CARD_FROM_USED_PILE;

        // Check condition(s)
        if (TriggerConditions.justLostForceFromCard(game, effectResult, opponent, Filters.and(Filters.your(self), Filters.Objective, Icon.EPISODE_I))
                && GameConditions.canTakeCardsIntoHandFromUsedPile(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Take card into hand from Used Pile");
            // Allow response(s)
            action.allowResponses("Take a card into hand from Used Pile",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromUsedPileEffect(action, playerId, Filters.any, true, true));
                        }
                    });
            actions.add(action);
        }

        return actions;
    }
}