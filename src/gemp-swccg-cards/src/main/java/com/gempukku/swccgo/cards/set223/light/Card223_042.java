package com.gempukku.swccgo.cards.set223.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
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
import com.gempukku.swccgo.logic.effects.choose.ChooseCardFromHandEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToDeployCostModifiersToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.StandardEffect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 23
 * Type: Effect
 * Title: Passage To The Alderaan System
 */
public class Card223_042 extends AbstractNormalEffect {
    public Card223_042() {
        super(Side.LIGHT, 3, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Passage To The Alderaan System", Uniqueness.UNIQUE, ExpansionSet.SET_23, Rarity.V);
        setGameText("If Stolen Data Tapes on table, deploy on table. Once per game, may simultaneously deploy non-[Episode VII] Falcon and non-[Episode VII] Han (for -1 Force each) from hand and/or Reserve Deck; reshuffle. At Tatooine system, Imperial starships may not have their deploy cost modified. [Immune to Alter.]");
        addIcon(Icon.VIRTUAL_SET_23);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.canSpot(game, self, Filters.and(Filters.your(self), Filters.Stolen_Data_Tapes));
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.PASSAGE_TO_THE_ALDERAAN_SYSTEM__DEPLOY_FALCON_AND_HAN;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.isDuringYourPhase(game, self, Phase.DEPLOY)) {
            boolean canDeployCardFromReserveDeck = GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Persona.FALCON);
            final List<PhysicalCard> cardsInHand = game.getGameState().getHand(playerId);
            final List<PhysicalCard> validStarfighters = new ArrayList<PhysicalCard>();
            Collection<PhysicalCard> starfighters = Filters.filter(cardsInHand, game, Filters.and(Filters.not(Filters.icon(Icon.EPISODE_VII)), Filters.Falcon));
            for (PhysicalCard starfighter : starfighters) {
                if (Filters.canSpot(cardsInHand, game, Filters.and(Filters.not(Filters.icon(Icon.EPISODE_VII)), Filters.Han, Filters.deployableSimultaneouslyWith(self, starfighter, false, -1, false, -1)))) {
                    validStarfighters.add(starfighter);
                }
            }

            if (!validStarfighters.isEmpty() || canDeployCardFromReserveDeck) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Deploy a starfighter and pilot");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerGameEffect(action));
                if (!validStarfighters.isEmpty() && canDeployCardFromReserveDeck) {
                    action.appendTargeting(
                            new PlayoutDecisionEffect(action, playerId,
                                    new YesNoDecision("You have valid starfighter and pilot combinations in hand. Do you want to search Reserve Deck as well?") {
                                        @Override
                                        protected void yes() {
                                            action.setActionMsg("Deploy a non-[E7] Falcon and non-[E7] Han from hand and/or Reserve Deck");
                                            // Perform result(s)
                                            action.appendEffect(getChooseCardsEffect(action));
                                        }
                                        @Override
                                        protected void no() {
                                            action.setActionMsg("Deploy a non-[E7] Falcon and non-[E7] Han from hand");
                                            action.appendTargeting(
                                                    new ChooseCardFromHandEffect(action, playerId, Filters.in(validStarfighters)) {
                                                        @Override
                                                        public String getChoiceText(int numCardsToChoose) {
                                                            return "Choose a non-[E7] Falcon";
                                                        }
                                                        @Override
                                                        protected void cardSelected(SwccgGame game, final PhysicalCard starfighter) {
                                                            Collection<PhysicalCard> pilots = Filters.filter(cardsInHand, game, Filters.and(Filters.not(Filters.icon(Icon.EPISODE_VII)),
                                                                    Filters.Han, Filters.deployableSimultaneouslyWith(self, starfighter, false, -1, false, -1)));
                                                            action.appendTargeting(
                                                                    new ChooseCardFromHandEffect(action, playerId, Filters.in(pilots)) {
                                                                        @Override
                                                                        public String getChoiceText(int numCardsToChoose) {
                                                                            return "Choose a non-[E7] Han";
                                                                        }
                                                                        @Override
                                                                        protected void cardSelected(SwccgGame game, PhysicalCard pilot) {
                                                                            // Perform result(s)
                                                                            action.appendEffect(
                                                                                    new DeployCardsSimultaneouslyEffect(action, starfighter, false, -1, pilot, false, -1));
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
                    action.setActionMsg("Deploy a non-[E7] Falcon and Han from hand and/or Reserve Deck");
                    // Perform result(s)
                    action.appendEffect(getChooseCardsEffect(action));
                }
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    private StandardEffect getChooseCardsEffect(final TopLevelGameTextAction action) {
        return new ChooseCardCombinationFromHandAndOrReserveDeckEffect(action) {
            @Override
            public String getChoiceText(SwccgGame game, Collection<PhysicalCard> cardsSelected) {
                return "Choose a non-[E7] Falcon and Han from hand and/or Reserve Deck";
            }

            @Override
            public Filter getValidToSelectFilter(SwccgGame game, Collection<PhysicalCard> cardsSelected) {
                String playerId = action.getPerformingPlayer();
                GameState gameState = game.getGameState();
                Collection<PhysicalCard> cardsToChooseFrom = new LinkedList<PhysicalCard>(gameState.getHand(playerId));
                cardsToChooseFrom.addAll(gameState.getCardPile(playerId, Zone.RESERVE_DECK));

                if (cardsSelected.isEmpty()) {
                    final List<PhysicalCard> validStarfighters = new ArrayList<PhysicalCard>();
                    Collection<PhysicalCard> starfighters = Filters.filter(cardsToChooseFrom, game, Filters.and(Filters.not(Filters.icon(Icon.EPISODE_VII)), Filters.Falcon));
                    for (PhysicalCard starfighter : starfighters) {
                        if (Filters.canSpot(cardsToChooseFrom, game, Filters.and(Filters.Han, Filters.deployableSimultaneouslyWith(action.getActionSource(), starfighter, false, -1, false, -1)))) {
                            validStarfighters.add(starfighter);
                        }
                    }
                    return Filters.in(validStarfighters);
                } else if (cardsSelected.size() == 1) {
                    PhysicalCard starfighter = cardsSelected.iterator().next();
                    return Filters.and(Filters.Han, Filters.deployableSimultaneouslyWith(action.getActionSource(), starfighter, false, -1, false, -1));
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
                        new DeployCardsSimultaneouslyEffect(action, starfighter, false, -1, pilot, false, -1));
            }
        };
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToDeployCostModifiersToLocationModifier(self, Filters.and(Filters.Imperial_starship), Filters.any, Filters.Tatooine_system));
        return modifiers;
    }
}
