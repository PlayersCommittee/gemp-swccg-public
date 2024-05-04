package com.gempukku.swccgo.cards.set11.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.DamagePodracerEffect;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotDrawRaceDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collections;
import java.util.List;

/**
 * Set: Tatooine
 * Type: Interrupt
 * Subtype: Lost
 * Title: Hit Racer
 */
public class Card11_081 extends AbstractLostInterrupt {
    public Card11_081() {
        super(Side.DARK, 4, Title.Hit_Racer, Uniqueness.UNIQUE, ExpansionSet.TATOOINE, Rarity.U);
        setLore("Sebulba is willing to utilize a variety of 'tactics' to maintain his lead position. Mars Guo learned this lesson the hard way.");
        setGameText("During your control phase, use 2 Force to target opponent's Podracer with a race total within 4 of one of your race totals. Draw destiny. If destiny > target Podracer's destiny number, opponent may not draw race destiny this turn and target Podracer is 'damaged.'");
        addIcons(Icon.TATOOINE, Icon.EPISODE_I);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (GameConditions.isDuringPodrace(game)
                && GameConditions.isDuringYourPhase(game, self, Phase.CONTROL)
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 2)) {
            GameState gameState = game.getGameState();
            ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

            float highestRaceTotal = modifiersQuerying.getHighestRaceTotal(gameState, playerId);
            Filter podracerFilter = Filters.podracerWithRaceTotalInRange(highestRaceTotal - 4, highestRaceTotal + 4);
            for (PhysicalCard yourPodracer : Filters.filterActive(game, self, Filters.and(Filters.your(self), Filters.Podracer))) {
                float raceTotal = modifiersQuerying.getPodracerRaceTotal(gameState, yourPodracer);
                if (highestRaceTotal != raceTotal) {
                    podracerFilter = Filters.or(podracerFilter, Filters.podracerWithRaceTotalInRange(raceTotal - 4, raceTotal + 4));
                }
            }
            podracerFilter = Filters.and(Filters.opponents(self), podracerFilter);

            if (GameConditions.canTarget(game, self, podracerFilter)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
                action.setText("Target a Podracer");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose Podracer", podracerFilter) {
                            @Override
                            protected void cardTargeted(final int targetGroupId, PhysicalCard targetedPodracer) {
                                action.addAnimationGroup(targetedPodracer);
                                // Pay cost(s)
                                action.appendCost(
                                        new UseForceEffect(action, playerId, 2));
                                // Allow response(s)
                                action.allowResponses("Make opponent draw no race destiny this turn and make " + GameUtils.getCardLink(targetedPodracer) + " 'damaged'",
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Get the final targeted card(s)
                                                final PhysicalCard finalPodracer = action.getPrimaryTargetCard(targetGroupId);
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

                                                                float podracerDestiny = game.getModifiersQuerying().getDestiny(game.getGameState(), finalPodracer);
                                                                gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                                gameState.sendMessage("Podracer's destiny: " + GuiUtils.formatAsString(podracerDestiny));
                                                                if (totalDestiny > podracerDestiny) {
                                                                    gameState.sendMessage("Result: Succeeded");
                                                                    action.appendEffect(
                                                                            new AddUntilEndOfTurnModifierEffect(action, new MayNotDrawRaceDestinyModifier(self, opponent),
                                                                                    "Makes opponent draw no race destiny"));
                                                                    action.appendEffect(
                                                                            new DamagePodracerEffect(action, finalPodracer));
                                                                } else {
                                                                    gameState.sendMessage("Result: Failed");
                                                                }
                                                            }
                                                        });
                                            }
                                        });
                            }
                        });
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}