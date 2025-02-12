package com.gempukku.swccgo.cards.set219.light;

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.DeployCardsSimultaneouslyEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardCombinationFromHandAndOrReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardFromHandEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.IconModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.modifiers.MovesFreeToLocationModifier;
import com.gempukku.swccgo.logic.timing.StandardEffect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 19
 * Type: Location
 * Subtype: System
 * Title: Lothal
 */
public class Card219_038 extends AbstractSystem {
    public Card219_038() {
        super(Side.LIGHT, Title.Lothal, 6, ExpansionSet.SET_19, Rarity.V);
        setLocationLightSideGameText("Once per game, may simultaneously deploy Ghost and Hera here from hand and/or Reserve Deck; reshuffle.");
        setLocationDarkSideGameText("Thrawn and Pryce deploy -1 (and move for free) to here. While Lothal converted, gains one [Dark Side] icon.");
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcon(Icon.DARK_FORCE, 1);
        addIcons(Icon.PLANET, Icon.VIRTUAL_SET_19);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextLightSideTopLevelActions(final String playerOnLightSideOfLocation, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LOTHAL__DEPLOY_GHOST_AND_HERA;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.isDuringYourPhase(game, playerOnLightSideOfLocation, Phase.DEPLOY)) {

            boolean canDeployCardFromReserveDeck = GameConditions.canDeployCardFromReserveDeck(game, playerOnLightSideOfLocation, self, gameTextActionId, Arrays.asList(Title.Ghost, Title.Hera));
            final List<PhysicalCard> cardsInHand = game.getGameState().getHand(playerOnLightSideOfLocation);
            final List<PhysicalCard> validGhostsInHand = new ArrayList<PhysicalCard>();
            Collection<PhysicalCard> ghostsInHand = Filters.filter(cardsInHand, game, Filters.Ghost);
            for (PhysicalCard ghostInHand : ghostsInHand) {
                if (Filters.canSpot(cardsInHand, game, Filters.and(Filters.Hera, Filters.deployableSimultaneouslyWith(self, ghostInHand, false, 0, false, 0)))) {
                    validGhostsInHand.add(ghostInHand);
                }
            }

            if (!validGhostsInHand.isEmpty() || canDeployCardFromReserveDeck) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnLightSideOfLocation, gameTextSourceCardId, gameTextActionId);
                action.setText("Deploy Ghost and Hera");
                // Update usage limit(s)
                action.appendUsage(new OncePerGameEffect(action));
                // Choose target(s)
                if (!validGhostsInHand.isEmpty() && canDeployCardFromReserveDeck) {
                    action.appendTargeting(
                            new PlayoutDecisionEffect(action, playerOnLightSideOfLocation,
                                    new YesNoDecision("You have Ghost and Hera in hand. Do you want to search Reserve Deck as well?") {
                                        @Override
                                        protected void yes() {
                                            // Allow response(s)
                                            action.setActionMsg("Deploy Ghost and Hera from hand and/or Reserve Deck");
                                            action.appendEffect(getChooseCardsEffect(action));
                                        }
                                        @Override
                                        protected void no() {
                                            // Allow response(s)
                                            action.setActionMsg("Deploy Ghost and Hera from hand");
                                            action.appendTargeting(
                                                new ChooseCardFromHandEffect(action, playerOnLightSideOfLocation, Filters.in(validGhostsInHand)) {
                                                    @Override
                                                    public String getChoiceText(int numCardsToChoose) {
                                                        return "Choose Ghost";
                                                    }

                                                    @Override
                                                    protected void cardSelected(SwccgGame game, final PhysicalCard ghost) {
                                                        Collection<PhysicalCard> heras = Filters.filter(cardsInHand, game, Filters.and(Filters.Hera,
                                                                Filters.deployableSimultaneouslyWith(self, ghost, false, 0, false, 0)));
                                                        action.appendTargeting(
                                                                new ChooseCardFromHandEffect(action, playerOnLightSideOfLocation, Filters.in(heras)) {
                                                                    @Override
                                                                    public String getChoiceText(int numCardsToChoose) {
                                                                        return "Choose Hera";
                                                                    }
                                                                    @Override
                                                                    protected void cardSelected(SwccgGame game, PhysicalCard hera) {
                                                                        action.appendEffect(
                                                                                new DeployCardsSimultaneouslyEffect(action, ghost, false, 0, hera, false, 0));
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
                    // Allow response(s)
                    action.setActionMsg("Deploy Ghost and Hera from hand and/or Reserve Deck");
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
                return "Choose Ghost and Hera from hand and/or Reserve Deck";
            }

            @Override
            public Filter getValidToSelectFilter(SwccgGame game, Collection<PhysicalCard> cardsSelected) {
                String playerId = action.getPerformingPlayer();
                GameState gameState = game.getGameState();
                Collection<PhysicalCard> cardsToChooseFrom = new LinkedList<PhysicalCard>(gameState.getHand(playerId));
                cardsToChooseFrom.addAll(gameState.getCardPile(playerId, Zone.RESERVE_DECK));

                if (cardsSelected.isEmpty()) {
                    final List<PhysicalCard> validGhosts = new ArrayList<PhysicalCard>();
                    Collection<PhysicalCard> ghosts = Filters.filter(cardsToChooseFrom, game, Filters.Ghost);
                    for (PhysicalCard ghost : ghosts) {
                        if (Filters.canSpot(cardsToChooseFrom, game, Filters.and(Filters.Hera, Filters.deployableSimultaneouslyWith(action.getActionSource(), ghost, false, 0, false, 0)))) {
                            validGhosts.add(ghost);
                        }
                    }
                    return Filters.in(validGhosts);
                } else if (cardsSelected.size() == 1) {
                    PhysicalCard ghost = cardsSelected.iterator().next();
                    return Filters.and(Filters.Hera, Filters.deployableSimultaneouslyWith(action.getActionSource(), ghost, false, 0, false, 0));
                }
                return Filters.none;
            }

            @Override
            public boolean isSelectionValid(SwccgGame game, Collection<PhysicalCard> cardsSelected) {
                return (cardsSelected.size() == 2);
            }

            @Override
            protected void cardsChosen(List<PhysicalCard> cardsChosen) {
                PhysicalCard ghost = cardsChosen.get(0);
                PhysicalCard hera = cardsChosen.get(1);
                action.appendEffect(
                        new DeployCardsSimultaneouslyEffect(action, ghost, false, 0, hera, false, 0));
            }
        };
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, final SwccgGame game, PhysicalCard self) {
        Condition isLothalConverted = new Condition() {
            @Override
            public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
                return GameConditions.canSpotConvertedLocation(game, Filters.Lothal_system);
            }
        };
        List<Modifier> modifiers = new LinkedList<>();
        Filter ThrawnOrPryce = Filters.or(Filters.Thrawn, Filters.Pryce);
        modifiers.add(new DeployCostToLocationModifier(self, ThrawnOrPryce, -1, self));
        modifiers.add(new MovesFreeToLocationModifier(self, ThrawnOrPryce, self));
        modifiers.add(new IconModifier(self, isLothalConverted, Icon.DARK_FORCE, 1));
        return modifiers;
    }
}
