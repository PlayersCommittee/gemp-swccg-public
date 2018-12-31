package com.gempukku.swccgo.cards.set210.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardsIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virutal Set 10
 * Type: Interrupt
 * Subtype: Lost
 * Title: Ambush (V)
 */

public class Card210_002 extends AbstractLostInterrupt {
    public Card210_002() {
        super(Side.LIGHT, 3, "Ambush", Uniqueness.UNIQUE);
        setLore("'Well done. Hold them in the security tower, and keep it quiet. Move.'");
        setGameText("Lose 1 Force to /\\ up to three troopers. OR Once per game, if your clones occupy at least three related battlegrounds, your Force drains at same and related battlegrounds this turn are +1.");
        addIcons(Icon.EPISODE_I, Icon.VIRTUAL_SET_10, Icon.CLOUD_CITY);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        List<String> listOfPossiblePlanets = new LinkedList<>();

        Collection<PhysicalCard> occupiedBattlegroundSites = Filters.filterTopLocationsOnTable(game,
                Filters.and(Filters.battleground_site, Filters.occupiesWith(playerId, self, Filters.clone)));

        List<PhysicalCard> validBattlegroundSites = new LinkedList<PhysicalCard>();
        for (PhysicalCard occupiedBattlegroundSite : occupiedBattlegroundSites) {
            String planetName = occupiedBattlegroundSite.getPartOfSystem();
            if (planetName != null) {
                if (Filters.filterCount(occupiedBattlegroundSites, game, 3, Filters.partOfSystem(planetName)).size() >= 3) {
                    validBattlegroundSites.add(occupiedBattlegroundSite);

                    if (!listOfPossiblePlanets.contains(planetName))
                        listOfPossiblePlanets.add(planetName);
                }
            }
        }

        GameTextActionId gameTextActionId1 = GameTextActionId.AMBUSH__ADD_TO_FORCE_DRAINS;
        if (!validBattlegroundSites.isEmpty()
                && GameConditions.isOncePerGame(game, self, gameTextActionId1)) {
            for (final String possiblePlanet : listOfPossiblePlanets) {
                final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId1);
                final Filter affectedLocations = Filters.and(Filters.battleground, Filters.sameOrRelatedLocationAs(self, Filters.partOfSystem(possiblePlanet)));
                action.setText("Add to Force drains at " + possiblePlanet);
                action.appendUsage(
                        new OncePerGameEffect(action));
                // Allow response(s)
                action.allowResponses(
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s)
                                action.appendEffect(
                                        new AddUntilEndOfTurnModifierEffect(action,
                                                //new ForceDrainModifier(self, affectedLocations, new CardMatchesEvaluator(1, 2, Filters.Hoth_location), playerId),
                                                new ForceDrainModifier(self, affectedLocations, 1, playerId),
                                                "Adds to Force drains at " + possiblePlanet));
                            }
                        }
                );
                actions.add(action);
            }
        }

        GameTextActionId gameTextActionId2 = GameTextActionId.AMBUSH__UPLOAD_TROOPERS;
        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId2)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId2);
            action.setText("Take troopers into hand from Reserve Deck");
            // Pay cost(s)
            action.appendCost(
                    new LoseForceEffect(action, playerId, 1, true));
            // Allow response(s)
            action.allowResponses("Take up to three troopers into hand from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardsIntoHandFromReserveDeckEffect(action, playerId, 1, 3, Filters.trooper, true));
                        }
                    }
            );
            actions.add(action);
        }

        return actions;
    }
}