package com.gempukku.swccgo.cards.set218.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.DeployCardsSimultaneouslyEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardCombinationFromHandAndOrReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardFromHandOrDeployableAsIfFromHandEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.StandardEffect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 18
 * Type: Effect
 * Title: Best Starpilot In The Galaxy
 */
public class Card218_015 extends AbstractNormalEffect {
    public Card218_015() {
        super(Side.LIGHT, 0, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Best Starpilot In The Galaxy", Uniqueness.UNIQUE);
        setGameText("If your [Skywalker] Epic Event on table, deploy on table. May [download] Polis Massa. Once per game, may simultaneously deploy an unpiloted Azure Angel, Falcon, or Red 5 and matching non-[Maintenance] pilot from hand and/or Reserve Deck; reshuffle. [Immune to Alter].");
        addIcons(Icon.SKYWALKER, Icon.VIRTUAL_SET_18);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.canSpot(game, self, Filters.and(Filters.your(self), Icon.SKYWALKER, Filters.Epic_Event));
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId = GameTextActionId.BEST_STARPILOT_IN_THE_GALAXY__DEPLOY_POLIS_MASSA;

        if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Title.Polis_Massa)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Polis Massa from Reserve Deck");
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.Polis_Massa_system, true));
            actions.add(action);
        }

        gameTextActionId = GameTextActionId.BEST_STARPILOT_IN_THE_GALAXY__DEPLOY_STARSHIP_AND_PILOT;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.isDuringYourPhase(game, self, Phase.DEPLOY)) {
            boolean canDeployCardFromReserveDeck = GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Title.Azure_Angel)
                    || GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Persona.FALCON)
                    || GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Persona.RED_5);

            final List<PhysicalCard> cardsToChooseFrom = new ArrayList<>();
            cardsToChooseFrom.addAll(game.getGameState().getHand(playerId));

            Filter cardsThatCanDeployAsIfFromHand = Filters.and(Filters.your(playerId), Filters.canDeployAsIfFromHand);

            if(GameConditions.canSpot(game, self, Filters.hasStacked(cardsThatCanDeployAsIfFromHand))){
                cardsToChooseFrom.addAll(Filters.filterStacked(game, Filters.canDeployAsIfFromHand));
            }

            final List<PhysicalCard> validStarfighters = new ArrayList<PhysicalCard>();
            Collection<PhysicalCard> starfighters = Filters.filter(cardsToChooseFrom, game, Filters.and(Filters.unpiloted, Filters.or(Filters.Azure_Angel, Filters.Falcon, Filters.Red_5)));

            for (PhysicalCard starfighter : starfighters) {
                if (Filters.canSpot(cardsToChooseFrom, game, Filters.and(Filters.matchingPilot(starfighter), Filters.deployableSimultaneouslyWith(self, starfighter, false, 0, false, 0)))) {
                    validStarfighters.add(starfighter);
                }
            }

            if (!validStarfighters.isEmpty() || canDeployCardFromReserveDeck) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Deploy starship and pilot");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerGameEffect(action));
                if (!validStarfighters.isEmpty() && canDeployCardFromReserveDeck) {
                    action.appendTargeting(
                            new PlayoutDecisionEffect(action, playerId,
                                    new YesNoDecision("You have valid starship and pilot combinations that can deploy from (or as if from) hand. Do you want to search Reserve Deck as well?") {
                                        @Override
                                        protected void yes() {
                                            action.setActionMsg("Deploy Azure Angel, Falcon, or Red 5 and a matching pilot from (or as if from) hand and/or Reserve Deck");
                                            // Perform result(s)
                                            action.appendEffect(getChooseCardsEffect(action, self));
                                        }
                                        @Override
                                        protected void no() {
                                            action.setActionMsg("Deploy Azure Angel, Falcon, or Red 5 and a matching pilot from (or as if from) hand");
                                            action.appendTargeting(
                                                    new ChooseCardFromHandOrDeployableAsIfFromHandEffect(action, playerId, Filters.in(validStarfighters)) {
                                                        @Override
                                                        public String getChoiceText(int numCardsToChoose) {
                                                            return "Choose Azure Angel, Falcon, or Red 5";
                                                        }
                                                        @Override
                                                        protected void cardSelected(SwccgGame game, final PhysicalCard starfighter) {
                                                            Collection<PhysicalCard> pilots = Filters.filter(cardsToChooseFrom, game, Filters.and(Filters.matchingPilot(starfighter), Filters.not(Icon.MAINTENANCE),
                                                                    Filters.deployableSimultaneouslyWith(self, starfighter, false, 0, false, 0)));
                                                            action.appendTargeting(
                                                                    new ChooseCardFromHandOrDeployableAsIfFromHandEffect(action, playerId, Filters.in(pilots)) {
                                                                        @Override
                                                                        public String getChoiceText(int numCardsToChoose) {
                                                                            return "Choose matching pilot";
                                                                        }
                                                                        @Override
                                                                        protected void cardSelected(SwccgGame game, PhysicalCard pilot) {
                                                                            // Perform result(s)
                                                                            action.appendEffect(
                                                                                    new DeployCardsSimultaneouslyEffect(action, starfighter, false, 0, pilot, false, 0));
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
                }
                else {
                    action.setActionMsg("Deploy Azure Angel, Falcon, or Red 5 and a matching pilot from hand and/or Reserve Deck");
                    // Perform result(s)
                    action.appendEffect(getChooseCardsEffect(action, self));
                }
                actions.add(action);
            }
        }

        return actions;
    }


    private StandardEffect getChooseCardsEffect(final TopLevelGameTextAction action, final PhysicalCard self) {
        return new ChooseCardCombinationFromHandAndOrReserveDeckEffect(action) {
            @Override
            public String getChoiceText(SwccgGame game, Collection<PhysicalCard> cardsSelected) {
                return "Choose Azure Angel, Falcon, or Red 5 and a matching pilot from hand and/or Reserve Deck";
            }

            @Override
            public Filter getValidToSelectFilter(SwccgGame game, Collection<PhysicalCard> cardsSelected) {
                String playerId = action.getPerformingPlayer();
                GameState gameState = game.getGameState();

                final List<PhysicalCard> cardsToChooseFrom = new ArrayList<>();
                cardsToChooseFrom.addAll(game.getGameState().getHand(playerId));

                Filter cardsThatCanDeployAsIfFromHand = Filters.and(Filters.your(playerId), Filters.canDeployAsIfFromHand);

                if(GameConditions.canSpot(game, self, Filters.hasStacked(cardsThatCanDeployAsIfFromHand))){
                    cardsToChooseFrom.addAll(Filters.filterStacked(game, Filters.canDeployAsIfFromHand));
                }

                cardsToChooseFrom.addAll(gameState.getCardPile(playerId, Zone.RESERVE_DECK));

                if (cardsSelected.isEmpty()) {
                    final List<PhysicalCard> validStarfighters = new ArrayList<PhysicalCard>();
                    Collection<PhysicalCard> starfighters = Filters.filter(cardsToChooseFrom, game, Filters.and(Filters.unpiloted, Filters.or(Filters.Azure_Angel, Filters.Falcon, Filters.Red_5)));
                    for (PhysicalCard starfighter : starfighters) {
                        if (Filters.canSpot(cardsToChooseFrom, game, Filters.and(Filters.matchingPilot(starfighter), Filters.not(Icon.MAINTENANCE), Filters.deployableSimultaneouslyWith(action.getActionSource(), starfighter, false, 0, false, 0)))) {
                            validStarfighters.add(starfighter);
                        }
                    }
                    return Filters.in(validStarfighters);
                } else if (cardsSelected.size() == 1) {
                    PhysicalCard starfighter = cardsSelected.iterator().next();
                    return Filters.and(Filters.matchingPilot(starfighter), Filters.not(Icon.MAINTENANCE), Filters.deployableSimultaneouslyWith(action.getActionSource(), starfighter, false, 0, false, 0));
                }
                return Filters.none;
            }

            @Override
            public boolean isSelectionValid(SwccgGame game, Collection<PhysicalCard> cardsSelected) {
                return (cardsSelected.size() == 2);
            }

            @Override
            protected void cardsChosen(List<PhysicalCard> cardsChosen) {
                PhysicalCard starfighter = cardsChosen.get(0);
                PhysicalCard pilot = cardsChosen.get(1);

                // Perform result(s)
                action.appendEffect(
                        new DeployCardsSimultaneouslyEffect(action, starfighter, false, 0, pilot, false, 0));
            }
        };
    }
}