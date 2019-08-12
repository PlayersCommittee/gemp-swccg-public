package com.gempukku.swccgo.cards.set211.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelBattleEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardFromHandEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 11
 * Type: Effect
 * Title: Beldon's Eye & All My Urchins
 */
public class Card211_031 extends AbstractNormalEffect {
    public Card211_031() {
        super(Side.LIGHT, 3, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Beldon's Eye & All My Urchins", Uniqueness.UNIQUE);
        addComboCardTitles("Beldon's Eye", "All My Urchins");
        setGameText("Deploy on table. Once per game, if Quiet Mining Colony on table, may search your hand and/or Reserve Deck and reveal an [Independent] starfighter and matching pilot; place both in hand, reshuffle; they each deploy -1 this turn. Once per game, if a battle just initiated at Bespin system while Executor there, may cancel that battle unless opponent uses 15 Force. [Immune to Alter.]");
        addIcons(Icon.VIRTUAL_SET_11);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<OptionalGameTextTriggerAction> actions = new LinkedList<>();

        final String opponent = game.getOpponent(playerId);
        GameTextActionId gameTextActionId = GameTextActionId.BELDONS_EYE__CANCEL_BATTLE;

        if (TriggerConditions.battleInitiatedAt(game, effectResult, opponent, Filters.Bespin_system)
                && GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canSpot(game, self, Filters.and(Filters.Executor, Filters.at(Filters.Bespin_system)))) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Cancel Battle just initiated at Bespin");
            action.appendUsage(
                    new OncePerGameEffect(action)
            );

            final int amountOfForce = 15;

            if (GameConditions.canUseForce(game, opponent, amountOfForce)) {
                action.appendEffect(
                        new PlayoutDecisionEffect(action, opponent,
                                new YesNoDecision("Pay " + amountOfForce + " force to prevent the battle from being cancelled?") {
                                    @Override
                                    protected void yes() {
                                        game.getGameState().sendMessage(opponent + " chooses to use " + amountOfForce + " Force");
                                        action.appendEffect(
                                                new UseForceEffect(action, opponent, amountOfForce));
                                    }

                                    @Override
                                    protected void no() {
                                        game.getGameState().sendMessage(playerId + " cancels the battle");
                                        action.appendEffect(
                                                new CancelBattleEffect(action));
                                    }
                                }
                        )
                );
            } else {
                action.appendEffect(
                        new CancelBattleEffect(action)
                );
            }

            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        final List<TopLevelGameTextAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId = GameTextActionId.BELDONS_EYE__TAKE_STARFIGHTER_AND_MATCHING_PILOT_INTO_HAND;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canSpot(game, self, Filters.Quiet_Mining_Colony)) {

            final Filter independent_starfighter = Filters.and(Icon.INDEPENDENT, Filters.starfighter);

            final Collection<PhysicalCard> cardsInHand = game.getGameState().getHand(playerId);
            final Collection<PhysicalCard> independentStarfightersInHand = Filters.filter(cardsInHand, game, independent_starfighter);
            final Collection<PhysicalCard> starfightersWithMatchingPilotsInHand = new ArrayList<>();
            for (PhysicalCard starfighter : independentStarfightersInHand) {
                Collection<PhysicalCard> matchingPilotsInHand = Filters.filter(cardsInHand, game, Filters.matchingPilot(starfighter));
                if (!matchingPilotsInHand.isEmpty()) {
                    starfightersWithMatchingPilotsInHand.add(starfighter);
                }
            }

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Choose starfighter and matching pilot.");
            action.appendUsage(
                    new OncePerGameEffect(action)
            );

            if (!starfightersWithMatchingPilotsInHand.isEmpty()) {
                action.appendTargeting(
                        new PlayoutDecisionEffect(action, playerId,
                                new YesNoDecision("You have valid starfighter and pilot combinations in hand. Do you want to search Reserve Deck as well?") {
                                    @Override
                                    protected void yes() {
                                        action.setActionMsg("Choose a unique [Independent] starfighter and matching pilot from hand and/or Reserve Deck");
                                        // Perform result(s)
                                        //action.appendEffect(getChooseCardsEffect(action));
                                        chooseCards(self, game, action, playerId, cardsInHand);
                                    }

                                    @Override
                                    protected void no() {
                                        action.setActionMsg("Choose a unique [Independent] starfighter and matching pilot from hand");
                                        action.appendTargeting(
                                                new ChooseCardFromHandEffect(action, playerId, Filters.in(starfightersWithMatchingPilotsInHand)) {
                                                    @Override
                                                    public String getChoiceText(int numCardsToChoose) {
                                                        return "Choose unique [Independent] starfighter";
                                                    }

                                                    @Override
                                                    protected void cardSelected(SwccgGame game, final PhysicalCard starfighter) {
                                                        Collection<PhysicalCard> pilots = Filters.filter(cardsInHand, game, Filters.matchingPilot(starfighter));
                                                        action.appendTargeting(
                                                                new ChooseCardFromHandEffect(action, playerId, Filters.in(pilots)) {
                                                                    @Override
                                                                    public String getChoiceText(int numCardsToChoose) {
                                                                        return "Choose matching pilot";
                                                                    }

                                                                    @Override
                                                                    protected void cardSelected(SwccgGame game, PhysicalCard pilot) {
                                                                        action.appendEffect(
                                                                                new ShowCardOnScreenEffect(action, starfighter));
                                                                        action.appendEffect(
                                                                                new ShowCardOnScreenEffect(action, pilot));
                                                                        reduceCost(self, action, starfighter);
                                                                        reduceCost(self, action, pilot);
                                                                    }
                                                                }
                                                        );
                                                    }
                                                }
                                        );
                                    }
                                }
                        )
                );
            } else {
                chooseCards(self, game, action, playerId, cardsInHand);
            }


            actions.add(action);
        }
        return actions;
    }

    private void chooseCards(final PhysicalCard self, final SwccgGame game, final Action action, final String playerId, final Collection<PhysicalCard> cardsInHand) {
        final Filter independent_starfighter = Filters.and(Icon.INDEPENDENT, Filters.starfighter);

        final Collection<PhysicalCard> cardsInReserveAndHand = new ArrayList<>();
        final Collection<PhysicalCard> choicesInReserveDeck = new ArrayList<>();

        final Collection<PhysicalCard> cardsInReserve = game.getGameState().getReserveDeck(playerId);
        cardsInReserveAndHand.addAll(cardsInReserve);
        cardsInReserveAndHand.addAll(cardsInHand);

        final Collection<PhysicalCard> independentStarfightersInReserve = Filters.filter(cardsInReserve, game, independent_starfighter);
        for (PhysicalCard starfighter : independentStarfightersInReserve) {
            Collection<PhysicalCard> matchingPilots = Filters.filter(cardsInReserveAndHand, game, Filters.matchingPilot(starfighter));
            if (!matchingPilots.isEmpty()) {
                choicesInReserveDeck.add(starfighter);
            }
        }

        final Collection<PhysicalCard> pilotsInReserve = Filters.filter(cardsInReserve, game, Filters.pilot);
        for (PhysicalCard pilot : pilotsInReserve) {
            Collection<PhysicalCard> matchingStarfighters = Filters.filter(cardsInReserveAndHand, game, Filters.matchingStarship(pilot));

            if (!matchingStarfighters.isEmpty()) {
                for (PhysicalCard matchingStarfighter : matchingStarfighters) {
                    if (matchingStarfighter.getBlueprint().hasIcon(Icon.INDEPENDENT)) {
                        choicesInReserveDeck.add(pilot);
                    }
                }
            }
        }

        if (!choicesInReserveDeck.isEmpty()) {
            action.appendTargeting(
                    new ChooseCardFromReserveDeckEffect(action, playerId, Filters.in(choicesInReserveDeck)) {
                        @Override
                        protected void cardSelected(SwccgGame game, PhysicalCard card) {
                            if (card.getBlueprint().getCardCategory() == CardCategory.CHARACTER) {
                                final Collection<PhysicalCard> matchingStarfightersInHand = Filters.filter(cardsInHand, game, Filters.and(Icon.INDEPENDENT, Filters.matchingStarship(card)));
                                final Collection<PhysicalCard> matchingStarfightersInReserve = Filters.filter(cardsInReserve, game, Filters.and(Icon.INDEPENDENT, Filters.matchingStarship(card)));

                                getMatchingCard(self, action, playerId, matchingStarfightersInHand, matchingStarfightersInReserve, "starfighter", card);

                            } else {
                                final Collection<PhysicalCard> matchingPilotsInHand = Filters.filter(cardsInHand, game, Filters.matchingPilot(card));
                                final Collection<PhysicalCard> matchingPilotsInReserve = Filters.filter(cardsInReserve, game, Filters.matchingPilot(card));

                                getMatchingCard(self, action, playerId, matchingPilotsInHand, matchingPilotsInReserve, "pilot", card);
                            }
                        }
                    }
            );
        } else {
            game.getGameState().sendMessage("No valid matching pairs were found.");
            action.appendEffect(new LookAtReserveDeckEffect(action, game.getOpponent(playerId), playerId));
            action.appendEffect(new ShuffleReserveDeckEffect(action));
        }
    }

    private void getMatchingCard(final PhysicalCard self, final Action action, final String playerId, final Collection<PhysicalCard> matchingCardsInHand, final Collection<PhysicalCard> matchingCardsInReserve, String msg, final PhysicalCard card) {
        if (!matchingCardsInHand.isEmpty() && !matchingCardsInReserve.isEmpty()) {
            action.appendTargeting(
                    new PlayoutDecisionEffect(action, playerId,
                            new YesNoDecision("Choose matching " + msg + " from Reserve Deck?") {
                                @Override
                                protected void yes() {
                                    takeCardIntoHandFromReserveDeckAndReduceCost(self, action, playerId, matchingCardsInReserve, card);
                                }

                                @Override
                                protected void no() {
                                    chooseCardFromHandAndReduceCost(self, action, playerId, matchingCardsInHand, card);
                                }
                            }
                    )
            );
        } else if (!matchingCardsInReserve.isEmpty()) {
            takeCardIntoHandFromReserveDeckAndReduceCost(self, action, playerId, matchingCardsInReserve, card);
        } else {
            chooseCardFromHandAndReduceCost(self, action, playerId, matchingCardsInHand, card);
        }
    }

    private void takeCardIntoHandFromReserveDeckAndReduceCost(final PhysicalCard self, final Action action, final String playerId, Collection<PhysicalCard> matchingCardsInReserve, final PhysicalCard card) {
        action.appendTargeting(
                new ChooseCardFromReserveDeckEffect(action, playerId, Filters.in(matchingCardsInReserve)) {
                    @Override
                    protected void cardSelected(SwccgGame game, PhysicalCard selectedCard) {
                        action.appendEffect(
                                new TakeCardIntoHandFromReserveDeckEffect(action, playerId, selectedCard, false)
                        );
                        reduceCost(self, action, selectedCard);
                        action.appendEffect(
                                new TakeCardIntoHandFromReserveDeckEffect(action, playerId, card, false)
                        );
                        reduceCost(self, action, card);
                    }
                }
        );
    }

    private void chooseCardFromHandAndReduceCost(final PhysicalCard self, final Action action, final String playerId, Collection<PhysicalCard> matchingCardsInHand, final PhysicalCard card) {
        action.appendTargeting(
                new ChooseCardFromHandEffect(action, playerId, Filters.in(matchingCardsInHand)) {
                    @Override
                    protected void cardSelected(SwccgGame game, PhysicalCard selectedCard) {
                        action.appendEffect(
                                new ShowCardOnScreenEffect(action, selectedCard));
                        reduceCost(self, action, selectedCard);
                        action.appendEffect(
                                new TakeCardIntoHandFromReserveDeckEffect(action, playerId, card, false)
                        );
                        reduceCost(self, action, card);
                    }
                }
        );
    }

    private void reduceCost(PhysicalCard self, Action action, PhysicalCard card) {
        action.appendEffect(
                new AddUntilEndOfTurnModifierEffect(action, new DeployCostModifier(self, Filters.title(card.getBlueprint().getTitle()), -1), GameUtils.getCardLink(card) + " is deploy -1 for remainder of turn.")
        );
    }
}