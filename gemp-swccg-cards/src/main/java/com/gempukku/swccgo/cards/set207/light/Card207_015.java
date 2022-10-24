package com.gempukku.swccgo.cards.set207.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.DeployCardsSimultaneouslyEffect;
import com.gempukku.swccgo.logic.effects.ModifyTotalBattleDestinyEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardCombinationFromHandAndOrReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardFromHandEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.StandardEffect;

import java.util.*;


/**
 * Set: Set 7
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: This is MY Ship!
 */
public class Card207_015 extends AbstractUsedOrLostInterrupt {
    public Card207_015() {
        super(Side.LIGHT, 4, Title.This_Is_My_Ship, Uniqueness.UNIQUE);
        setGameText("USED: Deploy Ghost and Hera simultaneously from your hand and/or Reserve Deck (for -1 Force each). LOST: If Chopper, Ezra, Hera, Kanan, Sabine, or Zeb in a battle, they each add 1 to your total battle destiny.");
        addIcons(Icon.VIRTUAL_SET_7);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        GameTextActionId gameTextActionId = GameTextActionId.THIS_IS_MY_SHIP__DEPLOY_GHOST_AND_HERA;

        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, self, Phase.DEPLOY)) {
            boolean canDeployCardFromReserveDeck = GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Arrays.asList(Title.Ghost, Title.Hera));
            final List<PhysicalCard> cardsInHand = game.getGameState().getHand(playerId);
            final List<PhysicalCard> validGhostsInHand = new ArrayList<PhysicalCard>();
            Collection<PhysicalCard> ghostsInHand = Filters.filter(cardsInHand, game, Filters.Ghost);
            for (PhysicalCard ghostInHand : ghostsInHand) {
                if (Filters.canSpot(cardsInHand, game, Filters.and(Filters.Hera, Filters.deployableSimultaneouslyWith(self, ghostInHand, false, -1, false, -1)))) {
                    validGhostsInHand.add(ghostInHand);
                }
            }

            if (!validGhostsInHand.isEmpty() || canDeployCardFromReserveDeck) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.USED);
                action.setText("Deploy Ghost and Hera");
                // Choose target(s)
                if (!validGhostsInHand.isEmpty() && canDeployCardFromReserveDeck) {
                    action.appendTargeting(
                            new PlayoutDecisionEffect(action, playerId,
                                    new YesNoDecision("You have Ghost and Hera in hand. Do you want to search Reserve Deck as well?") {
                                        @Override
                                        protected void yes() {
                                            // Allow response(s)
                                            action.allowResponses("Deploy Ghost and Hera from hand and/or Reserve Deck",
                                                    new RespondablePlayCardEffect(action) {
                                                        @Override
                                                        protected void performActionResults(Action targetingAction) {
                                                            // Perform result(s)
                                                            action.appendEffect(getChooseCardsEffect(action));
                                                        }
                                                    }
                                            );
                                        }

                                        @Override
                                        protected void no() {
                                            // Allow response(s)
                                            action.allowResponses("Deploy Ghost and Hera from hand",
                                                    new RespondablePlayCardEffect(action) {
                                                        @Override
                                                        protected void performActionResults(Action targetingAction) {
                                                            // Perform result(s)
                                                            action.appendEffect(
                                                                    new ChooseCardFromHandEffect(action, playerId, Filters.in(validGhostsInHand)) {
                                                                        @Override
                                                                        public String getChoiceText(int numCardsToChoose) {
                                                                            return "Choose Ghost";
                                                                        }

                                                                        @Override
                                                                        protected void cardSelected(SwccgGame game, final PhysicalCard ghost) {
                                                                            Collection<PhysicalCard> heras = Filters.filter(cardsInHand, game, Filters.and(Filters.Hera,
                                                                                    Filters.deployableSimultaneouslyWith(self, ghost, false, -1, false, -1)));
                                                                            action.appendEffect(
                                                                                    new ChooseCardFromHandEffect(action, playerId, Filters.in(heras)) {
                                                                                        @Override
                                                                                        public String getChoiceText(int numCardsToChoose) {
                                                                                            return "Choose Hera";
                                                                                        }

                                                                                        @Override
                                                                                        protected void cardSelected(SwccgGame game, PhysicalCard hera) {
                                                                                            action.appendEffect(
                                                                                                    new DeployCardsSimultaneouslyEffect(action, ghost, false, -1, hera, false, -1));
                                                                                        }
                                                                                    }
                                                                            );
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
                    action.allowResponses("Deploy Ghost and Hera from hand and/or Reserve Deck",
                            new RespondablePlayCardEffect(action) {
                                @Override
                                protected void performActionResults(Action targetingAction) {
                                    // Perform result(s)
                                    action.appendEffect(getChooseCardsEffect(action));
                                }
                            }
                    );
                }
                actions.add(action);
            }
        }

        // Check condition(s)
        if (GameConditions.isDuringBattle(game)) {
            final Filter inBattleFilter = Filters.and(Filters.or(Filters.Chopper, Filters.Ezra, Filters.Hera, Filters.Kanan, Filters.Sabine, Filters.Zeb), Filters.participatingInBattle);
            int count = Filters.countActive(game, self, inBattleFilter);
            if (count > 0) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
                action.setText("Add " + count + " to total battle destiny");
                // Allow response(s)
                action.allowResponses(
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s)
                                final int numToAdd = Filters.countActive(game, self, inBattleFilter);
                                action.appendEffect(
                                        new ModifyTotalBattleDestinyEffect(action, playerId, numToAdd));
                            }
                        }
                );
                actions.add(action);
            }
        }

        return actions;
    }

    private StandardEffect getChooseCardsEffect(final PlayInterruptAction action) {
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
                        if (Filters.canSpot(cardsToChooseFrom, game, Filters.and(Filters.Hera, Filters.deployableSimultaneouslyWith(action.getActionSource(), ghost, false, -1, false, -1)))) {
                            validGhosts.add(ghost);
                        }
                    }
                    return Filters.in(validGhosts);
                } else if (cardsSelected.size() == 1) {
                    PhysicalCard ghost = cardsSelected.iterator().next();
                    return Filters.and(Filters.Hera, Filters.deployableSimultaneouslyWith(action.getActionSource(), ghost, false, -1, false, -1));
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
                        new DeployCardsSimultaneouslyEffect(action, ghost, false, -1, hera, false, -1));
            }
        };
    }
}