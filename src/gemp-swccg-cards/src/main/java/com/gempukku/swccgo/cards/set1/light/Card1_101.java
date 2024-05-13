package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.decisions.IntegerAwaitingDecision;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collections;
import java.util.List;

/**
 * Set: Premiere
 * Type: Interrupt
 * Subtype: Lost
 * Title: On The Edge
 */
public class Card1_101 extends AbstractLostInterrupt {
    public Card1_101() {
        super(Side.LIGHT, 2, "On The Edge", Uniqueness.UNRESTRICTED, ExpansionSet.PREMIERE, Rarity.R2);
        setLore("Luke and Leia nearly fell over the retracted bridge across the Death Star's central core while trying to elude stormtroopers. They were soon trapped on the precipice.");
        setGameText("Use 1 Force and target one Rebel (on table) of ability > 2. Choose a number from 1 to 6. You may retrieve that amount from your Lost Pile if you now draw destiny > chosen number. If you fail, Rebel is lost.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        Filter rebelFilter = Filters.and(Filters.Rebel, Filters.abilityMoreThan(2));

        // Check condition(s)
        if (GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1)
                && GameConditions.canTarget(game, self, rebelFilter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Target a Rebel");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose Rebel", rebelFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId1, final PhysicalCard rebelTargeted) {
                            action.addAnimationGroup(rebelTargeted);
                            action.appendCost(
                                    new PlayoutDecisionEffect(action, playerId,
                                            new IntegerAwaitingDecision("Choose a number ", 1, 6, 1) {
                                                @Override
                                                public void decisionMade(final int chosenNumber) throws DecisionResultInvalidException {
                                                    game.getGameState().sendMessage(playerId + " chooses " + chosenNumber);
                                                    // Pay cost(s)
                                                    action.appendCost(
                                                            new UseForceEffect(action, playerId, 1));
                                                    // Allow response(s)
                                                    action.allowResponses("Draw destiny while targeting " + GameUtils.getCardLink(rebelTargeted),
                                                            new RespondablePlayCardEffect(action) {
                                                                @Override
                                                                protected void performActionResults(Action targetingAction) {
                                                                    // Get the final targeted card(s)
                                                                    final PhysicalCard finalRebel = action.getPrimaryTargetCard(targetGroupId1);
                                                                    // Perform result(s)
                                                                    action.appendEffect(
                                                                            new DrawDestinyEffect(action, playerId) {
                                                                                @Override
                                                                                protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                                                    final GameState gameState = game.getGameState();
                                                                                    if (totalDestiny == null) {
                                                                                        gameState.sendMessage("Result: Failed due to failed destiny draw");
                                                                                        action.appendEffect(
                                                                                                new LoseCardFromTableEffect(action, finalRebel));
                                                                                        return;
                                                                                    }

                                                                                    gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                                                    gameState.sendMessage("Chosen number: " + chosenNumber);

                                                                                    if (totalDestiny > chosenNumber) {
                                                                                        gameState.sendMessage("Result: Succeeded");
                                                                                        if (!Filters.mayContributeToForceRetrieval.accepts(game, finalRebel)) {
                                                                                            gameState.sendMessage("Force retrieval not allowed due to including cards not allowed to contribute to Force retrieval");
                                                                                            return;
                                                                                        }
                                                                                        action.appendEffect(
                                                                                                new PlayoutDecisionEffect(action, playerId,
                                                                                                        new YesNoDecision("Do you want to retrieve " + chosenNumber + " Force?") {
                                                                                                            @Override
                                                                                                            protected void yes() {
                                                                                                                gameState.sendMessage(playerId + " chooses to retrieve " + chosenNumber + " Force");
                                                                                                                action.appendEffect(
                                                                                                                        new RetrieveForceEffect(action, playerId, chosenNumber));
                                                                                                            }
                                                                                                            @Override
                                                                                                            protected void no() {
                                                                                                                gameState.sendMessage(playerId + " chooses to not retrieve " + chosenNumber + " Force");
                                                                                                            }
                                                                                                        }
                                                                                                )
                                                                                        );
                                                                                    }
                                                                                    else {
                                                                                        gameState.sendMessage("Result: Failed");
                                                                                        action.appendEffect(
                                                                                                new LoseCardFromTableEffect(action, finalRebel));
                                                                                    }
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
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}