package com.gempukku.swccgo.cards.set4.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.MayNotMoveOrBattleUntilEndOfPlayersNextTurnEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;


/**
 * Set: Dagobah
 * Type: Interrupt
 * Subtype: Lost
 * Title: Egregious Pilot Error
 */
public class Card4_050 extends AbstractLostInterrupt {
    public Card4_050() {
        super(Side.LIGHT, 4, Title.Egregious_Pilot_Error, Uniqueness.UNIQUE);
        setLore("Details of the notorious 'Incident at Anoat' are required reading at the Imperial Academy of Raithal, where the pilots' names are posthumously displayed.");
        setGameText("During opponent's control phase, if opponent has two or more capital starships at a system or sector together, draw destiny. If destiny -1 < number of those starships, they may not move or participate in battle until end of your next turn.");
        addIcons(Icon.DAGOBAH);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        Filter locationFilter = Filters.and(Filters.system_or_sector, Filters.sameLocationAs(self, Filters.and(Filters.opponents(self), Filters.capital_starship,
                Filters.with(self, Filters.and(Filters.opponents(self), Filters.capital_starship)))));

        // Check condition(s)
        if (GameConditions.isDuringOpponentsPhase(game, self, Phase.CONTROL)
                && GameConditions.canSpotLocation(game, locationFilter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Draw destiny against number of starships");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose location with multiple opponent's capital starships", locationFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard location) {
                            action.addAnimationGroup(location);
                            // Allow response(s)
                            action.allowResponses("Draw destiny against number of opponent's capital starships at " + GameUtils.getCardLink(location),
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new DrawDestinyEffect(action, playerId) {
                                                        @Override
                                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, final List<Float> destinyDrawValues, Float totalDestiny) {
                                                            final GameState gameState = game.getGameState();
                                                            if (totalDestiny == null) {
                                                                gameState.sendMessage("Result: No result due to failed destiny draw");
                                                                return;
                                                            }

                                                            gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                            Collection<PhysicalCard> starships = Filters.filterActive(game, self,
                                                                    Filters.and(Filters.opponents(self), Filters.capital_starship, Filters.at(location)));
                                                            int numberOfStarships = starships.size();
                                                            gameState.sendMessage("Number of starships: " + numberOfStarships);

                                                            if ((totalDestiny - 1) < numberOfStarships) {
                                                                gameState.sendMessage("Result: Succeeded");
                                                                action.appendEffect(
                                                                        new MayNotMoveOrBattleUntilEndOfPlayersNextTurnEffect(action, starships, playerId));
                                                            }
                                                            else {
                                                                gameState.sendMessage("Result: Failed");
                                                            }
                                                        }
                                                    });
                                        }
                                    }
                            );
                       }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}