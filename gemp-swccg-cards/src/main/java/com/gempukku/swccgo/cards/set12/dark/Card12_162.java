package com.gempukku.swccgo.cards.set12.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.decisions.IntegerAwaitingDecision;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DrawCardsIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collections;
import java.util.List;


/**
 * Set: Coruscant
 * Type: Interrupt
 * Subtype: Used
 * Title: Vote Now!
 */
public class Card12_162 extends AbstractUsedInterrupt {
    public Card12_162() {
        super(Side.DARK, 5, "Vote Now!", Uniqueness.UNIQUE);
        setLore("With enough political support, any number of devious plans can be put into motion.");
        setGameText("During your control phase, draw destiny. If destiny > 2, draw up to X cards (maximum 4) from Reserve Deck, where X = your total politics at Galactic Senate minus opponent's total politics at Galactic Senate.");
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, self, Phase.CONTROL)
                && GameConditions.canDrawDestiny(game, playerId)
                && GameConditions.canSpot(game, self, Filters.Galactic_Senate)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Draw destiny to draw cards");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new DrawDestinyEffect(action, playerId) {
                                        @Override
                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                            GameState gameState = game.getGameState();
                                            ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
                                            if (totalDestiny == null) {
                                                gameState.sendMessage("Result: Failed due to failed destiny draw");
                                                return;
                                            }

                                            gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));
                                            if (totalDestiny > 2) {
                                                gameState.sendMessage("Result: Succeeded");
                                                float valueForX = Math.max(0, modifiersQuerying.getTotalPoliticsAtGalacticSenate(gameState, playerId) - modifiersQuerying.getTotalPoliticsAtGalacticSenate(gameState, opponent));
                                                gameState.sendMessage("Value for X: " + GuiUtils.formatAsString(valueForX));
                                                if (valueForX > 0) {
                                                    int maxToDraw = Math.min(4, Math.min((int) Math.floor(valueForX), gameState.getReserveDeckSize(playerId)));
                                                    if (maxToDraw > 0) {
                                                        action.appendEffect(
                                                                new PlayoutDecisionEffect(action, playerId,
                                                                        new IntegerAwaitingDecision("Choose number of cards to draw", 1, maxToDraw, maxToDraw) {
                                                                            @Override
                                                                            public void decisionMade(int result) throws DecisionResultInvalidException {
                                                                                // Perform result(s)
                                                                                action.appendEffect(
                                                                                        new DrawCardsIntoHandFromReserveDeckEffect(action, playerId, result));
                                                                            }
                                                                        }
                                                                )
                                                        );
                                                    }
                                                }
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
            return Collections.singletonList(action);
        }
        return null;
    }
}