package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardsAtSameLocationEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardToLoseFromTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Set: Premiere
 * Type: Interrupt
 * Subtype: Lost
 * Title: Collision!
 */
public class Card1_074 extends AbstractLostInterrupt {
    public Card1_074() {
        super(Side.LIGHT, 4, Title.Collision, Uniqueness.UNRESTRICTED, ExpansionSet.PREMIERE, Rarity.C2);
        setLore("High-speed collisions are a constant danger during chaotic starfighter dogfights. Scanners can be jammed. Pilots rely on vision, increasing the chances of such accidents.");
        setGameText("Use 1 Force if opponent has at least two starships present at same system or sector. Draw destiny. If destiny < number of those starships, opponent must lose one of them.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);
        TargetingReason targetingReason = TargetingReason.TO_BE_LOST;
        Filter starshipFilter = Filters.and(Filters.opponents(self), Filters.starship, Filters.presentAt(Filters.and(Filters.system_or_sector, Filters.canBeTargetedBy(self))),
                Filters.presentWith(self, Filters.and(Filters.opponents(self), Filters.starship, Filters.canBeTargetedBy(self, targetingReason))));

        // Check condition(s)
        if (GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1)
                && GameConditions.canTarget(game, self, TargetingReason.TO_BE_LOST, starshipFilter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Draw destiny against number of starships");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardsAtSameLocationEffect(action, playerId, "Choose starships", 2, Integer.MAX_VALUE, targetingReason, starshipFilter) {
                        @Override
                        protected boolean isTargetAll() {
                            return true;
                        }
                        @Override
                        protected void cardsTargeted(final int targetGroupId1, Collection<PhysicalCard> targetedStarships) {
                            action.addAnimationGroup(targetedStarships);
                            // Set secondary target filter(s)
                            action.addSecondaryTargetFilter(Filters.sameSystemOrSectorAs(self, Filters.inActionTargetGroup(action, targetGroupId1)));
                            // Pay cost(s)
                            action.appendCost(
                                    new UseForceEffect(action, playerId, 1));
                            // Allow response(s)
                            action.allowResponses("Draw destiny against " + GameUtils.getAppendedNames(targetedStarships),
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the final targeted card(s)
                                            final Collection<PhysicalCard> finalStarships = targetingAction.getPrimaryTargetCards(targetGroupId1);
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
                                                            int numberOfStarships = finalStarships.size();
                                                            gameState.sendMessage("Number of starships: " + numberOfStarships);

                                                            if (totalDestiny < numberOfStarships) {
                                                                gameState.sendMessage("Result: Succeeded");
                                                                action.appendEffect(
                                                                        new ChooseCardToLoseFromTableEffect(action, opponent, Filters.in(finalStarships)));
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