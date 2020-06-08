package com.gempukku.swccgo.cards.set14.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.decisions.IntegerAwaitingDecision;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;


/**
 * Set: Theed Palace
 * Type: Interrupt
 * Subtype: Used
 * Title: We're Hit, Artoo
 */
public class Card14_110 extends AbstractUsedInterrupt {
    public Card14_110() {
        super(Side.DARK, 5, "We're Hit, Artoo", Uniqueness.RESTRICTED_2);
        setLore("Complex recursive algorithms are used by Trade Federation starships to seek out and destroy enemies within targeting range.");
        setGameText("Once during battle, target a participating starfighter present with your droid starfighter. Draw destiny. If destiny > target's defense value, target 'hit.' OR If opponent's starfighter was just 'hit' where your droid starfighter is present, activate up to 4 Force.");
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;
        Filter filter = Filters.and(Filters.starfighter, Filters.participatingInBattle, Filters.presentWith(self, Filters.and(Filters.your(self), Filters.droid_starfighter)));
        TargetingReason targetingReason = TargetingReason.TO_BE_HIT;

        // Check condition(s)
        if (GameConditions.isOncePerBattle(game, self, self.getCardId(), gameTextActionId)
                && GameConditions.canTarget(game, self, targetingReason, filter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Target starfighter");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerBattleEffect(action));
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose starfighter", targetingReason, filter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard starship) {
                            action.addAnimationGroup(starship);
                            // Allow response(s)
                            action.allowResponses("'Hit' " + GameUtils.getCardLink(starship),
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            final PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                            // Perform result(s)
                                            action.appendEffect(
                                                    new DrawDestinyEffect(action, playerId) {
                                                        @Override
                                                        protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                                            return Collections.singletonList(finalTarget);
                                                        }
                                                        @Override
                                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                            GameState gameState = game.getGameState();
                                                            if (totalDestiny == null) {
                                                                gameState.sendMessage("Result: Failed due to failed destiny draw");
                                                                return;
                                                            }

                                                            float defenseValue = game.getModifiersQuerying().getDefenseValue(game.getGameState(), finalTarget);
                                                            gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                            gameState.sendMessage("Defense value: " + GuiUtils.formatAsString(defenseValue));
                                                            if (totalDestiny > defenseValue) {
                                                                gameState.sendMessage("Result: Succeeded");
                                                                action.appendEffect(
                                                                        new HitCardEffect(action, finalTarget, self));
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
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        // Check condition(s)
        if (TriggerConditions.justHit(game, effectResult, Filters.and(Filters.opponents(self), Filters.starfighter, Filters.at(Filters.wherePresent(self, Filters.and(Filters.your(self), Filters.droid_starfighter)))))
                && GameConditions.canActivateForce(game, playerId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Activate up to 4 Force");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            int maxForceToActivate = Math.min(4, game.getGameState().getReserveDeckSize(playerId));
                            if (maxForceToActivate > 0) {
                                action.appendEffect(
                                        new PlayoutDecisionEffect(action, playerId,
                                                new IntegerAwaitingDecision("Choose amount of Force to activate ", 1, maxForceToActivate, maxForceToActivate) {
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
            return Collections.singletonList(action);
        }
        return null;
    }
}