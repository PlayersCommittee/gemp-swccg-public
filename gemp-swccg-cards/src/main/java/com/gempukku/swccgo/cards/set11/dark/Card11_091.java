package com.gempukku.swccgo.cards.set11.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.decisions.IntegerAwaitingDecision;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collections;
import java.util.List;


/**
 * Set: Tatooine
 * Type: Interrupt
 * Subtype: Lost
 * Title: You Swindled Me!
 */
public class Card11_091 extends AbstractLostInterrupt {
    public Card11_091() {
        super(Side.DARK, 4, "You Swindled Me!", Uniqueness.UNIQUE);
        setLore("Needless to say, Watto was not happy about his loss.");
        setGameText("Opponent chooses: opponent may activate up to 2 Force and you may activate up to 4 Force, or opponent retrieves 1 Force and you retrieve 3 Force.");
        addIcons(Icon.TATOOINE, Icon.EPISODE_I);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);

        if (GameConditions.canActivateForce(game, playerId)
             && GameConditions.canActivateForce(game, opponent)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Make opponent choose");
            // Allow response(s)
            action.allowResponses("Make opponent choose to activate Force or retrieve Force",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new PlayoutDecisionEffect(action, opponent,
                                            new MultipleChoiceAwaitingDecision("Choose effect", new String[]{"Activate up to 2 Force", "Retrieve 1 Force"}) {
                                                @Override
                                                protected void validDecisionMade(int index, String result) {
                                                    final GameState gameState = game.getGameState();

                                                    if (index == 0) {
                                                        gameState.sendMessage(opponent + " chooses to activate up to 2 Force and allow " + playerId + " to activate up to 4 Force");
                                                        int maxForceForOpponentToActivate = Math.min(2, gameState.getReserveDeckSize(opponent));
                                                        if (maxForceForOpponentToActivate > 0) {
                                                            action.appendEffect(
                                                                    new PlayoutDecisionEffect(action, opponent,
                                                                            new IntegerAwaitingDecision("Choose amount of Force to activate", 0, maxForceForOpponentToActivate, maxForceForOpponentToActivate) {
                                                                                @Override
                                                                                public void decisionMade(final int result) throws DecisionResultInvalidException {
                                                                                    // Perform result(s)
                                                                                    action.appendEffect(
                                                                                            new ActivateForceEffect(action, opponent, result));
                                                                                    action.appendEffect(
                                                                                            new PassthruEffect(action) {
                                                                                                @Override
                                                                                                protected void doPlayEffect(SwccgGame game) {
                                                                                                    int maxForceForPlayerToActivate = Math.min(4, gameState.getReserveDeckSize(playerId));
                                                                                                    if (maxForceForPlayerToActivate > 0) {
                                                                                                        action.appendEffect(
                                                                                                                new PlayoutDecisionEffect(action, playerId,
                                                                                                                        new IntegerAwaitingDecision("Choose amount of Force to activate", 0, maxForceForPlayerToActivate, maxForceForPlayerToActivate) {
                                                                                                                            @Override
                                                                                                                            public void decisionMade(final int result) throws DecisionResultInvalidException {
                                                                                                                                // Perform result(s)
                                                                                                                                action.appendEffect(
                                                                                                                                        new ActivateForceEffect(action, playerId, result));
                                                                                                                            }
                                                                                                                        }
                                                                                                                )
                                                                                                        );
                                                                                                    }
                                                                                                }
                                                                                            }
                                                                                    );
                                                                                }
                                                                            }
                                                                    )
                                                            );
                                                        } else {
                                                            int maxForceForPlayerToActivate = Math.min(4, gameState.getReserveDeckSize(playerId));
                                                            if (maxForceForPlayerToActivate > 0) {
                                                                action.appendEffect(
                                                                        new PlayoutDecisionEffect(action, playerId,
                                                                                new IntegerAwaitingDecision("Choose amount of Force to activate", 0, maxForceForPlayerToActivate, maxForceForPlayerToActivate) {
                                                                                    @Override
                                                                                    public void decisionMade(final int result) throws DecisionResultInvalidException {
                                                                                        // Perform result(s)
                                                                                        action.appendEffect(
                                                                                                new ActivateForceEffect(action, playerId, result));
                                                                                    }
                                                                                }
                                                                        )
                                                                );
                                                            }
                                                        }
                                                    } else {
                                                        gameState.sendMessage(opponent + " chooses to retrieve 1 Force and " + playerId + " to retrieve 3 Force");
                                                        action.appendEffect(
                                                                new RetrieveForceEffect(action, opponent, 1));
                                                        action.appendEffect(
                                                                new RetrieveForceEffect(action, playerId, 3));
                                                    }
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