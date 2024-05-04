package com.gempukku.swccgo.cards.set13.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
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
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.ResetPowerUntilEndOfBattleEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.List;


/**
 * Set: Reflections III
 * Type: Interrupt
 * Subtype: Lost
 * Title: No Match For A Sith
 */
public class Card13_079 extends AbstractLostInterrupt {
    public Card13_079() {
        super(Side.DARK, 4, "No Match For A Sith", Uniqueness.UNIQUE, ExpansionSet.REFLECTIONS_III, Rarity.PM);
        setLore("Lightsaber duels are so fast and furious that Jedi don't plan their offensive strategy, but instead rely on instinct and their ability to sense an opponent's next strike.");
        setGameText("If Maul armed with a lightsaber in battle, use 2 Force to draw two destiny. Opponent draws two destiny. If any of your destinies equal any of opponent's destinies, opponent's Jedi is power = 0 for remainder of battle.");
        addIcons(Icon.REFLECTIONS_III, Icon.EPISODE_I);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);
        final Filter targetFilter = Filters.and(Filters.opponents(self), Filters.Jedi, Filters.participatingInBattle);

        // Check condition(s)
        if (GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.Maul, Filters.armedWith(Filters.lightsaber)))
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 2)
                && GameConditions.canTarget(game, self, targetFilter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Make both players draw two destiny");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose opponent's Jedi", targetFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard targetedCard) {
                            // Pay cost(s)
                            action.appendCost(
                                    new UseForceEffect(action, playerId, 2));
                            // Allow response(s)
                            action.allowResponses("Make both players draw two destiny while targeting " + GameUtils.getCardLink(targetedCard),
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            final PhysicalCard finalTarget = targetingAction.getPrimaryTargetCard(targetGroupId);

                                            // Perform result(s)
                                            action.appendEffect(
                                                    new DrawDestinyEffect(action, playerId, 2) {
                                                        @Override
                                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, final List<Float> destinyDrawValues1, final Float totalDestiny1) {
                                                            action.appendEffect(
                                                                    new DrawDestinyEffect(action, opponent, 2) {
                                                                        @Override
                                                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues2, Float totalDestiny2) {
                                                                            GameState gameState = game.getGameState();
                                                                            for (Float drawValues1 : destinyDrawValues1) {
                                                                                for (Float drawValues2 : destinyDrawValues2) {
                                                                                    if (drawValues2.equals(drawValues1)) {
                                                                                        gameState.sendMessage("Result: Succeeded");
                                                                                        action.appendEffect(
                                                                                                new ResetPowerUntilEndOfBattleEffect(action, finalTarget, 0));
                                                                                        return;
                                                                                    }
                                                                                }
                                                                            }
                                                                            gameState.sendMessage("Result: Failed");
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
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}