package com.gempukku.swccgo.cards.set213.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.choose.ChooseAndLoseCardFromHandEffect;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.PutStackedCardsInUsedPileEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardsIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.AboutToLeaveTableResult;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Set: Set 13
 * Type: Interrupt
 * Subtype: Used or Lost
 * Title: I've Been Searching For You For Some Time
 */
public class Card213_020 extends AbstractUsedOrLostInterrupt {
    public Card213_020() {
        super(Side.DARK, 5, "I've Been Searching For You For Some Time");
        setLore("");
        setGameText("USED: If opponent's character is about to be lost, place all 'Hatred' cards (if any) on that character in owner's Used Pile. LOST: Lose Vader or an Inquistor from hand to take up to two Inquisitors into hand from Reserve Deck; reshuffle.");
        addIcons(Icon.VIRTUAL_SET_13);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self) {
        //Check Condition
        if (TriggerConditions.isAboutToBeLost(game, effectResult, Filters.and(Filters.opponents(playerId), Filters.character))
                || TriggerConditions.isAboutToBeForfeitedToLostPile(game, effectResult, Filters.and(Filters.opponents(playerId), Filters.character))) {
            final AboutToLeaveTableResult aboutToLeaveTableResult = (AboutToLeaveTableResult) effectResult;
            final PhysicalCard cardToBeLost = aboutToLeaveTableResult.getCardAboutToLeaveTable();
            final Collection<PhysicalCard> hatredCards = Filters.filterStacked(game, Filters.and(Filters.hatredCard, Filters.stackedOn(cardToBeLost)));
            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Place hatred cards in Used Pile");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new PutStackedCardsInUsedPileEffect(action, playerId, hatredCards.size(), hatredCards.size(), false, cardToBeLost)
                            );
                        }
                    }
            );
            return Collections.singletonList(action);
        }

        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        GameTextActionId gameTextActionId = GameTextActionId.IVE_BEEN_SEARCHING_FOR_YOU_FOR_SOME_TIME__UPLOAD_INQUISITORS;

        // Check condition(s)
        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)
                && GameConditions.hasInHand(game, playerId, Filters.or(Filters.Vader, Filters.inquisitor))) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.LOST);
            action.setText("Take inquisitors into hand from Reserve Deck");
            // Pay cost(s)
            action.appendCost(
                    new ChooseAndLoseCardFromHandEffect(action, playerId, Filters.or(Filters.Vader, Filters.inquisitor)));
            // Allow response(s)
            action.allowResponses("Take up to 2 inquisitors into hand from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardsIntoHandFromReserveDeckEffect(action, playerId, 1, 2, Filters.inquisitor, true));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
