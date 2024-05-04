package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelForceDrainEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collections;
import java.util.List;

/**
 * Set: Premiere
 * Type: Interrupt
 * Subtype: Lost
 * Title: Surprise Assault
 */
public class Card1_113 extends AbstractLostInterrupt {
    public Card1_113() {
        super(Side.LIGHT, 3, Title.Surprise_Assault, Uniqueness.UNRESTRICTED, ExpansionSet.PREMIERE, Rarity.C1);
        setLore("Imperial troops with strict orders are often caught off-guard by innovative Rebel ambushes, sneak attacks, or sabotage efforts.");
        setGameText("Use 1 Force to cancel a Force drain at one location. Draw one destiny for each character, starship and vehicle the opponent has present. Compare your destiny total to opponent's power total. Player with lowest total loses Force equal to the difference.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.forceDrainInitiatedAt(game, effectResult, Filters.canBeTargetedBy(self))
                && GameConditions.canCancelForceDrain(game, self)
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1)) {
            final PhysicalCard forceDrainLocation = game.getGameState().getForceDrainLocation();

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Cancel Force drain");
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
                                    new CancelForceDrainEffect(action));
                            action.appendEffect(
                                    new PassthruEffect(action) {
                                        @Override
                                        protected void doPlayEffect(SwccgGame game) {
                                            final GameState gameState = game.getGameState();
                                            final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
                                            int numDraws = Filters.countActive(game, self, Filters.and(Filters.opponents(self),
                                                    Filters.or(Filters.character, Filters.starship, Filters.vehicle), Filters.present(forceDrainLocation)));
                                            final float totalPower = modifiersQuerying.getTotalPowerAtLocation(gameState, forceDrainLocation, opponent, false, false);
                                            action.appendEffect(
                                                    new DrawDestinyEffect(action, playerId, numDraws) {
                                                        @Override
                                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, final Float totalDestiny) {
                                                            if (GameConditions.hasGameTextModification(game, self, ModifyGameTextType.SURPRISE_ASSAULT__ADD_DESTINY_TO_TOTAL)) {
                                                                action.appendEffect(
                                                                        new DrawDestinyEffect(action, opponent, 1) {
                                                                            @Override
                                                                            protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws2, List<Float> destinyDrawValues2, Float totalDestiny2) {
                                                                                gameState.sendMessage("Destiny total: " + (totalDestiny != null ? GuiUtils.formatAsString(totalDestiny) : "Failed destiny draw"));
                                                                                float opponentsTotal = totalPower + (totalDestiny2 != null ? totalDestiny2 : 0);
                                                                                gameState.sendMessage("Power total: " + GuiUtils.formatAsString(opponentsTotal));
                                                                                if ((totalDestiny != null ? totalDestiny : 0) < opponentsTotal) {
                                                                                    float forceToLose = opponentsTotal - (totalDestiny != null ? totalDestiny : 0);
                                                                                    gameState.sendMessage("Result: " + playerId + " loses " + GuiUtils.formatAsString(forceToLose) + " Force");
                                                                                    action.appendEffect(
                                                                                            new LoseForceEffect(action, playerId, forceToLose));
                                                                                }
                                                                                else if ((totalDestiny != null ? totalDestiny : 0) > opponentsTotal) {
                                                                                    float forceToLose = (totalDestiny != null ? totalDestiny : 0) - opponentsTotal;
                                                                                    gameState.sendMessage("Result: " + opponent + " loses " + GuiUtils.formatAsString(forceToLose) + " Force");
                                                                                    action.appendEffect(
                                                                                            new LoseForceEffect(action, opponent, forceToLose));
                                                                                }
                                                                                else {
                                                                                    gameState.sendMessage("Result: No result");
                                                                                }
                                                                            }
                                                                        }
                                                                );
                                                            }
                                                            else {
                                                                gameState.sendMessage("Destiny total: " + (totalDestiny != null ? GuiUtils.formatAsString(totalDestiny) : "Failed destiny draw"));
                                                                gameState.sendMessage("Power total: " + GuiUtils.formatAsString(totalPower));
                                                                if ((totalDestiny != null ? totalDestiny : 0) < totalPower) {
                                                                    float forceToLose = totalPower - (totalDestiny != null ? totalDestiny : 0);
                                                                    gameState.sendMessage("Result: " + playerId + " loses " + GuiUtils.formatAsString(forceToLose) + " Force");
                                                                    action.appendEffect(
                                                                            new LoseForceEffect(action, playerId, forceToLose));
                                                                }
                                                                else if ((totalDestiny != null ? totalDestiny : 0) > totalPower) {
                                                                    float forceToLose = (totalDestiny != null ? totalDestiny : 0) - totalPower;
                                                                    gameState.sendMessage("Result: " + opponent + " loses " + GuiUtils.formatAsString(forceToLose) + " Force");
                                                                    action.appendEffect(
                                                                            new LoseForceEffect(action, opponent, forceToLose));
                                                                }
                                                                else {
                                                                    gameState.sendMessage("Result: No result");
                                                                }
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
            return Collections.singletonList(action);
        }
        return null;
    }
}