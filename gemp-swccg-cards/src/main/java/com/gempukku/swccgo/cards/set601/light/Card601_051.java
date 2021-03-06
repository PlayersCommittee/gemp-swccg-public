package com.gempukku.swccgo.cards.set601.light;

import com.gempukku.swccgo.cards.AbstractUsedOrStartingInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.RevealTopCardsOfReserveDeckEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.decisions.ArbitraryCardsSelectionDecision;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.*;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Block 6
 * Type: Interrupt
 * Subtype: Used Or Starting
 * Title: Let The Wookiee Win (V)
 */
public class Card601_051 extends AbstractUsedOrStartingInterrupt {
    public Card601_051() {
        super(Side.LIGHT, 5, Title.Let_The_Wookiee_Win, Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("'It's not wise to upset a Wookiee.' 'But sir, nobody worries about upsetting a droid.' 'That's cause a droid don't pull people's arms out of their sockets when they lose.'");
        setGameText("USED: Reveal the top 3 cards of Reserve Deck; place one in hand, and the others on bottom of Used Pile (in any order).\n" +
                "STARTING: If your starting location was a non-[Special Edition] location, deploy from Reserve Deck an always [Immune to Alter] Effect and any number of <> sites with < 3 total [Light Side Force]. Place this Interrupt in hand.");
        addIcons(Icon.A_NEW_HOPE, Icon.BLOCK_6);
        setVirtualSuffix(true);
        setAsLegacy(true);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (GameConditions.hasReserveDeck(game, playerId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Reveal top 3 cards of Reserve Deck");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new RevealTopCardsOfReserveDeckEffect(action, playerId, playerId, 3) {
                                        @Override
                                        protected void cardsRevealed(final List<PhysicalCard> cards) {
                                            game.getUserFeedback().sendAwaitingDecision(playerId,
                                                    new ArbitraryCardsSelectionDecision("Top card" + GameUtils.s(cards) + " of Reserve Deck. Choose card to take into hand", cards, cards, 1, 1) {
                                                        @Override
                                                        public void decisionMade(String result) throws DecisionResultInvalidException {
                                                            List<PhysicalCard> selectedCards = getSelectedCardsByResponse(result);
                                                            PhysicalCard selectedCard = selectedCards.get(0);
                                                            Collection<PhysicalCard> toPlaceInUsed = Filters.filter(cards, game, Filters.not(Filters.samePermanentCardId(selectedCard)));
                                                            action.appendEffect(new TakeOneCardIntoHandFromOffTableEffect(action, playerId, selectedCard, "") {
                                                                @Override
                                                                protected void afterCardTakenIntoHand() {}
                                                            });
                                                            action.appendEffect(new PlaceCardsInUsedPileFromOffTableEffect(action, toPlaceInUsed, true));
                                                        }
                                                    });
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected PlayInterruptAction getGameTextStartingAction(final String playerId, final SwccgGame game, final PhysicalCard self) {
        // Check condition(s)
        final PhysicalCard startingLocation = game.getModifiersQuerying().getStartingLocation(playerId);
        if (startingLocation != null && !Filters.icon(Icon.SPECIAL_EDITION).accepts(game, startingLocation)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.STARTING);
            action.setText("Deploy an Effect and sites from Reserve Deck");
            // Allow response(s)
            action.allowResponses("Deploy an Effect and <> sites from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new DeployCardFromReserveDeckEffect(action, Filters.and(Filters.Effect, Filters.always_immune_to_Alter), true, false));
                            //TODO this doesn't actually enforce the icon limit. something like ChooseCardCombinationFromHandAndOrCardPileEffect where you say what the valid combinations are seems like the right way to go
                            action.appendEffect(
                                    new DeployCardsFromReserveDeckEffect(action, Filters.generic_site, 1, 2, true, false));
//                            action.appendEffect(
//                                    new ChooseCardCombinationFromCardPileEffect(action, playerId, Zone.RESERVE_DECK) {
//
//                                        @Override
//                                        public Filter getValidToSelectFilter(SwccgGame game, Collection<PhysicalCard> cardsSelected) {
//                                            return Filters.generic_site;
//                                        }
//
//                                        @Override
//                                        public boolean isSelectionValid(SwccgGame game, Collection<PhysicalCard> cardsSelected) {
//                                            int forceIconTotal = 0;
//                                            for (PhysicalCard card: cardsSelected) {
//                                                forceIconTotal += card.getBlueprint().getIconCount(Icon.LIGHT_FORCE);
//                                            }
//                                            return forceIconTotal < 3;
//                                        }
//
//                                        @Override
//                                        public void cardsChosen(List<PhysicalCard> cardsChosen) {
////                                            new DeployCardsFromReserveDeckEffect(action, playerId, cardsChosen);
//                                        }
//                                    }
//                            );
                            action.appendEffect(
                                    new TakeCardFromVoidIntoHandEffect(action, playerId, self));
                        }
                    }
            );
            return action;
        }

        return null;
    }
}