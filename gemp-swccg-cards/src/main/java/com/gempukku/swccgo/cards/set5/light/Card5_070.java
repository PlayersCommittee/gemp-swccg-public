package com.gempukku.swccgo.cards.set5.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.SwitchBattleDestinyNumbersEffect;
import com.gempukku.swccgo.logic.effects.SwitchTotalPowerInBattleEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Cloud City
 * Type: Interrupt
 * Subtype: Lost
 * Title: Somersault
 */
public class Card5_070 extends AbstractLostInterrupt {
    public Card5_070() {
        super(Side.LIGHT, 3, "Somersault", Uniqueness.UNIQUE);
        setLore("Luke's sudden reversal of fortune allowed him to turn the tables on Vader.");
        setGameText("During a battle at a site, just before drawing battle destiny, if opponent has less than double your total power, use 4 Force to switch your total power with opponent's. OR If both players just drew one battle destiny, use 1 Force to switch numbers.");
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.justBeforePlayersDrawBattleDestiny(game, effectResult)
                && GameConditions.isDuringBattleAt(game, Filters.site)
                && GameConditions.canUseForce(game, playerId, 4)) {
            float playersPower = GameConditions.getBattlePower(game, playerId);
            float opponentsPower = GameConditions.getBattlePower(game, opponent);
            if ((2 * playersPower) > opponentsPower) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Switch total power with opponent's");
                // Pay cost(s)
                action.appendCost(
                        new UseForceEffect(action, playerId, 4));
                // Allow response(s)
                action.allowResponses(
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s)
                                action.appendEffect(
                                        new SwitchTotalPowerInBattleEffect(action));
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        // Check condition(s)
        if (TriggerConditions.isBattleDestinyDrawingJustCompletedForBothPlayers(game, effectResult)
                && GameConditions.didBothPlayersDrawOneBattleDestiny(game)
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Switch battle destiny numbers");
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new SwitchBattleDestinyNumbersEffect(action));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}