package com.gempukku.swccgo.cards.set601.light;

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
import com.gempukku.swccgo.logic.effects.choose.ChooseCardFromHandEffect;
import com.gempukku.swccgo.logic.modifiers.DefenseValueModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.StandardEffect;

import java.util.*;

/**
 * Set: Block 2
 * Type: Effect
 * Title: Beldon's Eye (V)
 */
public class Card601_233 extends AbstractNormalEffect {
    public Card601_233() {
        super(Side.LIGHT, 6, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Beldon's Eye", Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("Symbol for the Cloud City Miner's Guild (not affiliated with the Galactic Miner's Guild). Named after the beldons, giant creatures who generate Tibanna gas.");
        setGameText("Deploy on table. Your [Independent] starships are defense value +2 at Bespin locations. Once per game, if Quiet Mining Colony on table, may simultaneously deploy a unique (•) [Independent] starfighter and matching pilot (for -1 Force each) from your hand and/or Reserve Deck; reshuffle. [Immune to Alter]");
        addIcons(Icon.CLOUD_CITY, Icon.LEGACY_BLOCK_2);
        addImmuneToCardTitle(Title.Alter);
        setAsLegacy(true);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefenseValueModifier(self, Filters.and(Filters.your(self), Icon.INDEPENDENT, Filters.starship, Filters.at(Filters.Bespin_location)), 2));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.BELDONS_EYE__DEPLOY_STARFIGHTER_AND_MATCHING_PILOT;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.isDuringYourPhase(game, self, Phase.DEPLOY)
                && GameConditions.canSpot(game, self, Filters.Quiet_Mining_Colony)) {
            boolean canDeployCardFromReserveDeck = GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId);
            final List<PhysicalCard> cardsInHand = game.getGameState().getHand(playerId);
            final List<PhysicalCard> validStarfighters = new ArrayList<PhysicalCard>();
            Collection<PhysicalCard> starfighters = Filters.filter(cardsInHand, game, Filters.and(Filters.starfighter, Icon.INDEPENDENT));
            for (PhysicalCard starfighter : starfighters) {
                if (Filters.canSpot(cardsInHand, game, Filters.and(Filters.matchingPilot(starfighter), Filters.deployableSimultaneouslyWith(self, starfighter, false, -1, false, -1)))) {
                    validStarfighters.add(starfighter);
                }
            }

            if (!validStarfighters.isEmpty() || canDeployCardFromReserveDeck) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Deploy starfighter and pilot");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerGameEffect(action));
                if (!validStarfighters.isEmpty() && canDeployCardFromReserveDeck) {
                    action.appendTargeting(
                            new PlayoutDecisionEffect(action, playerId,
                                    new YesNoDecision("You have valid starfighter and pilot combinations in hand. Do you want to search Reserve Deck as well?") {
                                        @Override
                                        protected void yes() {
                                            action.setActionMsg("Deploy a unique [Independent] starfighter and matching pilot from hand and/or Reserve Deck");
                                            // Perform result(s)
                                            action.appendEffect(getChooseCardsEffect(action));
                                        }
                                        @Override
                                        protected void no() {
                                            action.setActionMsg("Deploy a unique [Independent] starfighter and matching pilot from hand");
                                            action.appendTargeting(
                                                    new ChooseCardFromHandEffect(action, playerId, Filters.in(validStarfighters)) {
                                                        @Override
                                                        public String getChoiceText(int numCardsToChoose) {
                                                            return "Choose unique [Independent] starfighter";
                                                        }
                                                        @Override
                                                        protected void cardSelected(SwccgGame game, final PhysicalCard starfighter) {
                                                            Collection<PhysicalCard> pilots = Filters.filter(cardsInHand, game, Filters.and(Filters.matchingPilot(starfighter),
                                                                    Filters.deployableSimultaneouslyWith(self, starfighter, false, -1, false, -1)));
                                                            action.appendTargeting(
                                                                    new ChooseCardFromHandEffect(action, playerId, Filters.in(pilots)) {
                                                                        @Override
                                                                        public String getChoiceText(int numCardsToChoose) {
                                                                            return "Choose matching pilot";
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
                    action.setActionMsg("Deploy a unique [Independent] starfighter and matching pilot from hand and/or Reserve Deck");
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
                return "Choose a unique [Independent] starfighter and matching pilot from hand and/or Reserve Deck";
            }

            @Override
            public Filter getValidToSelectFilter(SwccgGame game, Collection<PhysicalCard> cardsSelected) {
                String playerId = action.getPerformingPlayer();
                GameState gameState = game.getGameState();
                Collection<PhysicalCard> cardsToChooseFrom = new LinkedList<PhysicalCard>(gameState.getHand(playerId));
                cardsToChooseFrom.addAll(gameState.getCardPile(playerId, Zone.RESERVE_DECK));

                if (cardsSelected.isEmpty()) {
                    final List<PhysicalCard> validStarfighters = new ArrayList<PhysicalCard>();
                    Collection<PhysicalCard> starfighters = Filters.filter(cardsToChooseFrom, game, Filters.and(Filters.starfighter, Icon.INDEPENDENT));
                    for (PhysicalCard starfighter : starfighters) {
                        if (Filters.canSpot(cardsToChooseFrom, game, Filters.and(Filters.matchingPilot(starfighter), Filters.deployableSimultaneouslyWith(action.getActionSource(), starfighter, false, -1, false, -1)))) {
                            validStarfighters.add(starfighter);
                        }
                    }
                    return Filters.in(validStarfighters);
                } else if (cardsSelected.size() == 1) {
                    PhysicalCard starfighter = cardsSelected.iterator().next();
                    return Filters.and(Filters.matchingPilot(starfighter), Filters.deployableSimultaneouslyWith(action.getActionSource(), starfighter, false, -1, false, -1));
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
}