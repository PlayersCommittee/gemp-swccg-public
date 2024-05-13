package com.gempukku.swccgo.cards.set4.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.evaluators.OnTableEvaluator;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.ActionProxy;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.BlowAwayState;
import com.gempukku.swccgo.game.state.DrawDestinyState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.effects.AddToBlownAwayForceLossEffect;
import com.gempukku.swccgo.logic.effects.BlowAwayEffect;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalDestinyModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Dagobah
 * Type: Interrupt
 * Subtype: Lost
 * Title: Transmission Terminated
 */
public class Card4_066 extends AbstractLostInterrupt {
    public Card4_066() {
        super(Side.LIGHT, 5, Title.Transmission_Terminated, Uniqueness.UNRESTRICTED, ExpansionSet.DAGOBAH, Rarity.U);
        setLore("After the mission, the Death Squadron HoloNet communications system reported fifteen system errors: ten computer malfunctions, four power failures and one asteroid.");
        setGameText("Cancel one hologram. OR Use 3 Force. Draw destiny. Add 1 to destiny for each asteroid sector on table. If total destiny > 3, Imperial Holotable site is 'blown away' (opponent loses 4 Force).");
        addIcons(Icon.DAGOBAH);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.hologram)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.hologram, "a hologram");
            actions.add(action);
        }

        Filter holotableFilter = Filters.Imperial_Holotable;
        TargetingReason targetingReason = TargetingReason.TO_BE_BLOWN_AWAY;

        // Check condition(s)
        if (GameConditions.canTarget(game, self, targetingReason, holotableFilter)
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 3)) {
            final int gameTextSourceCardId = self.getCardId();

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("'Blow away' Imperial Holotable");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose Imperial Holotable", targetingReason, holotableFilter) {
                        @Override
                        protected void cardTargeted(int targetGroupId, final PhysicalCard holotable) {
                            action.addAnimationGroup(holotable);
                            // Pay cost(s)
                            action.appendCost(
                                    new UseForceEffect(action, playerId, 3));
                            // Allow response(s)
                            action.allowResponses("'Blow away' " + GameUtils.getCardLink(holotable),
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new DrawDestinyEffect(action, playerId) {
                                                        @Override
                                                        protected List<Modifier> getDrawDestinyModifiers(SwccgGame game, DrawDestinyState drawDestinyState) {
                                                            Modifier modifier = new TotalDestinyModifier(self, drawDestinyState.getId(), new OnTableEvaluator(self, Filters.asteroid_sector));
                                                            return Collections.singletonList(modifier);
                                                        }
                                                        @Override
                                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                            GameState gameState = game.getGameState();
                                                            if (totalDestiny == null) {
                                                                gameState.sendMessage("Result: Failed due to failed destiny draw");
                                                                return;
                                                            }

                                                            gameState.sendMessage("Total destiny: " + GuiUtils.formatAsString(totalDestiny));

                                                            if (totalDestiny > 3) {
                                                                gameState.sendMessage("Result: Succeeded");
                                                                action.appendEffect(
                                                                        new BlowAwayEffect(action, holotable) {
                                                                            @Override
                                                                            protected List<ActionProxy> getBlowAwayActionProxies(SwccgGame game, BlowAwayState blowAwayState) {
                                                                                ActionProxy actionProxy = new AbstractActionProxy() {
                                                                                    @Override
                                                                                    public List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult effectResult) {
                                                                                        List<TriggerAction> actions = new LinkedList<TriggerAction>();
                                                                                        String opponent = game.getOpponent(playerId);

                                                                                        // Check condition(s)
                                                                                        if (TriggerConditions.isBlownAwayCalculateForceLossStep(game, effectResult, holotable)) {

                                                                                            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                                                                                            action.skipInitialMessageAndAnimation();
                                                                                            // Perform result(s)
                                                                                            action.appendEffect(
                                                                                                    new AddToBlownAwayForceLossEffect(action, opponent, 4));
                                                                                            actions.add(action);
                                                                                        }
                                                                                        return actions;
                                                                                    }
                                                                                };
                                                                                return Collections.singletonList(actionProxy);
                                                                            }
                                                                        }
                                                                );
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
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.hologram)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }
}