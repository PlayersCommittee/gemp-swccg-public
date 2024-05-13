package com.gempukku.swccgo.cards.set11.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.conditions.PhaseCondition;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfPlayersNextTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.ChooseEffectOrderEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotDrawRaceDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.StandardEffect;

import java.util.ArrayList;
import java.util.List;


/**
 * Set: Tatooine
 * Type: Interrupt
 * Subtype: Lost
 * Title: Neck And Neck
 */
public class Card11_038 extends AbstractLostInterrupt {
    public Card11_038() {
        super(Side.LIGHT, 4, Title.Neck_And_Neck, Uniqueness.UNIQUE, ExpansionSet.TATOOINE, Rarity.U);
        setLore("When Anakin had finally caught up with Sebulba, he knew he needed to make some kind of a move to break away from the Dug and win the race.");
        setGameText("If difference between opponent's and your highest race totals < 4, lose 1 Force. Opponent draws no race destiny next control phase. OR If both players have < 10 Life Force remaining, both players retrieve 4 Force.");
        addIcons(Icon.TATOOINE, Icon.EPISODE_I);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new ArrayList<>();
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (GameConditions.isDuringPodrace(game)) {
            final GameState gameState = game.getGameState();
            ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
            float yourPodraceTotal = modifiersQuerying.getHighestRaceTotal(gameState, playerId);
            float opponentsPodraceTotal = modifiersQuerying.getHighestRaceTotal(gameState, opponent);
            if (Math.abs(yourPodraceTotal - opponentsPodraceTotal) < 4) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Make opponent draw no race destiny");
                // Pay cost(s)
                action.appendCost(
                        new LoseForceEffect(action, playerId, 1, true));
                // Allow response(s)
                action.allowResponses("Make opponent draw no race destiny next control phase",
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s)
                                if (gameState.getCurrentPhase() != Phase.ACTIVATE && gameState.getCurrentPhase() != Phase.CONTROL) {
                                    String nextTurnOwner = game.getOpponent(gameState.getCurrentPlayerId());
                                    action.appendEffect(
                                            new AddUntilEndOfPlayersNextTurnModifierEffect(action, nextTurnOwner, new MayNotDrawRaceDestinyModifier(self, new PhaseCondition(Phase.CONTROL, nextTurnOwner), opponent),
                                                    "Makes opponent draw no race destiny next control phase"));
                                }
                                else {
                                    action.appendEffect(
                                            new AddUntilEndOfTurnModifierEffect(action, new MayNotDrawRaceDestinyModifier(self, new PhaseCondition(Phase.CONTROL), opponent),
                                                    "Makes opponent draw no race destiny next control phase"));
                                }
                            }
                        });
                actions.add(action);
            }
        }

        // Check condition(s)
        if (GameConditions.hasLifeForceLessThan(game, playerId, 10)
                && GameConditions.hasLifeForceLessThan(game, opponent, 10)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Make both players retrieve 4 Force");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            List<StandardEffect> effectList = new ArrayList<>();
                            effectList.add(new RetrieveForceEffect(action, playerId, 4));
                            effectList.add(new RetrieveForceEffect(action, opponent, 4));
                            action.appendEffect(
                                    new ChooseEffectOrderEffect(action, effectList));
                        }
                    });
            actions.add(action);
        }

        return actions;
    }
}