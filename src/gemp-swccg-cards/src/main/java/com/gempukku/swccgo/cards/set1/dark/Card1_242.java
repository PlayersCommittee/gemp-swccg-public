package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.LoseCardsFromTableEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardsOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premiere
 * Type: Interrupt
 * Subtype: Lost
 * Title: Dead Jawa
 */
public class Card1_242 extends AbstractLostInterrupt {
    public Card1_242() {
        super(Side.DARK, 5, "Dead Jawa", Uniqueness.UNRESTRICTED, ExpansionSet.PREMIERE, Rarity.C2);
        setLore("Many Jawas were killed by stormtroopers searching for R2-D2. They used banthas and gaffi sticks to feign a Tusken Raider attack, but Obi-Wan saw through the ruse.");
        setGameText("If two or more Stormtroopers are present where the opponent has Jawas, target as many Jawas there as desired. Draw destiny. If destiny > the number of Jawas you targeted, they are immediately lost.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        // Check condition(s)
        final TargetingReason targetingReason = TargetingReason.TO_BE_LOST;
        List<PhysicalCard> validLocations = new LinkedList<PhysicalCard>();
        Collection<PhysicalCard> locations = Filters.filterTopLocationsOnTable(game,
                Filters.sameLocationAs(self, Filters.and(Filters.opponents(self), Filters.Jawa, Filters.canBeTargetedBy(self, targetingReason))));
        for (PhysicalCard location : locations) {
            if (GameConditions.canSpot(game, self, 2, Filters.and(Filters.your(self), Filters.stormtrooper, Filters.present(location)))) {
                validLocations.add(location);
            }
        }

        if (!validLocations.isEmpty()) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Make Jawas lost");
            // Choose target(s)
            action.appendTargeting(
                    new ChooseCardOnTableEffect(action, playerId, "Choose location with Stormtroopers and opponent's Jawas", Filters.in(locations)) {
                        @Override
                        protected void cardSelected(PhysicalCard location) {
                            action.appendTargeting(
                                    new TargetCardsOnTableEffect(action, playerId, "Choose Jawas", 1, Integer.MAX_VALUE, targetingReason,
                                            Filters.and(Filters.opponents(self), Filters.Jawa, Filters.at(location))) {
                                        @Override
                                        protected void cardsTargeted(final int targetGroupId, Collection<PhysicalCard> targetedCards) {
                                            action.addAnimationGroup(targetedCards);
                                            // Allow response(s)
                                            action.allowResponses("Make " + GameUtils.getAppendedNames(targetedCards) + " lost",
                                                    new RespondablePlayCardEffect(action) {
                                                        @Override
                                                        protected void performActionResults(Action targetingAction) {
                                                            // Get the targeted card(s) from the action using the targetGroupId.
                                                            // This needs to be done in case the target(s) were changed during the responses.
                                                            final Collection<PhysicalCard> finalTargets = action.getPrimaryTargetCards(targetGroupId);

                                                            // Perform result(s)
                                                            action.appendEffect(
                                                                    new DrawDestinyEffect(action, playerId) {
                                                                        @Override
                                                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                                            GameState gameState = game.getGameState();
                                                                            if (totalDestiny == null) {
                                                                                gameState.sendMessage("Result: Failed due to failed destiny draw");
                                                                                return;
                                                                            }

                                                                            int numTargets = finalTargets.size();
                                                                            gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                                            gameState.sendMessage("Number of Jawas targeted: " + numTargets);

                                                                            if (totalDestiny > numTargets) {
                                                                                gameState.sendMessage("Result: Succeeded");
                                                                                action.appendEffect(
                                                                                        new LoseCardsFromTableEffect(action, finalTargets));
                                                                            }
                                                                            else {
                                                                                gameState.sendMessage("Result: Failed");
                                                                            }
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
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}