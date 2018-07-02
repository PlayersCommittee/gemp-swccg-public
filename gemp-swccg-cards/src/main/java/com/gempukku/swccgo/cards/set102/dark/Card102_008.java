package com.gempukku.swccgo.cards.set102.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.results.MovingResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Premium (Jedi Pack)
 * Type: Interrupt
 * Subtype: Lost
 * Title: Gravity Shadow
 */
public class Card102_008 extends AbstractLostInterrupt {
    public Card102_008() {
        super(Side.DARK, 4, Title.Gravity_Shadow, Uniqueness.UNIQUE);
        setLore("'Traveling through hyperspace ain't like dustin' crops, boy!' Gravitational phenomena cast shadows in hyperspace, posing a serious threat to lightspeed navigation.");
        setGameText("If opponent's starship has just begun to move through hyperspace, draw destiny. If destiny > pilot's ability, starship must return to original location and may not move this turn. If destiny = pilot's ability, starship is lost.");
        addIcons(Icon.PREMIUM);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        TargetingReason targetingReason = TargetingReason.TO_BE_LOST;
        Filter filter = Filters.and(Filters.opponents(self), Filters.starship, Filters.canBeTargetedBy(self, targetingReason));

        if (TriggerConditions.movingThroughHyperspace(game, effectResult, filter)) {
            final MovingResult movingResult = (MovingResult) effectResult;
            final PhysicalCard starship = movingResult.getCardMoving();

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Target " + GameUtils.getFullName(starship));
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose starship", targetingReason, starship) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Allow response(s)
                            action.allowResponses("Draw destiny while targeting " + GameUtils.getCardLink(targetedCard),
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
                                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                            GameState gameState = game.getGameState();
                                                            if (totalDestiny == null) {
                                                                gameState.sendMessage("Result: Failed due to failed destiny draw");
                                                                return;
                                                            }

                                                            gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                            float ability = game.getModifiersQuerying().getHighestAbilityPiloting(game.getGameState(), starship, false);
                                                            gameState.sendMessage("Ability: " + GuiUtils.formatAsString(ability));

                                                            if (totalDestiny > ability) {
                                                                gameState.sendMessage("Result: Starship returns to original location");
                                                                movingResult.getPreventableCardEffect().preventEffectOnCard(finalTarget);
                                                                action.appendEffect(
                                                                        new MayNotMoveUntilEndOfTurnEffect(action, finalTarget));
                                                            } else if (totalDestiny == ability) {
                                                                gameState.sendMessage("Result: Starship is lost");
                                                                movingResult.getPreventableCardEffect().preventEffectOnCard(finalTarget);
                                                                action.appendEffect(
                                                                        new LoseCardFromTableEffect(action, starship));
                                                            } else {
                                                                gameState.sendMessage("Result: No result");
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