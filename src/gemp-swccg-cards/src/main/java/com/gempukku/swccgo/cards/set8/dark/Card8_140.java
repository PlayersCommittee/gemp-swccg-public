package com.gempukku.swccgo.cards.set8.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.CaptureCharacterOnTableEffect;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.ResetPowerUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Endor
 * Type: Interrupt
 * Subtype: Used
 * Title: Don't Move!
 */
public class Card8_140 extends AbstractUsedInterrupt {
    public Card8_140() {
        super(Side.DARK, 3, "Don't Move!", Uniqueness.UNIQUE, ExpansionSet.ENDOR, Rarity.C);
        setLore("Stormtroopers rely on an intimidating presence and technologically superiority.");
        setGameText("If a battle was just initiated, and two of your troopers are armed with weapons, target one opponent's character present. Draw destiny: (0-2) no effect, (3-4) target is power = 0 for remainder of turn, (5+) target captured.");
        addIcons(Icon.ENDOR);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.battleInitiated(game, effectResult)
                && GameConditions.canSpot(game, self, 2, Filters.and(Filters.your(self), Filters.trooper, Filters.armedWith(Filters.weapon), Filters.participatingInBattle))) {
            Filter filter = Filters.and(Filters.opponents(self), Filters.character, Filters.presentInBattle);
            TargetingReason targetingReason = TargetingReason.TO_BE_CAPTURED;
            if (GameConditions.canTarget(game, self, targetingReason, filter)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Target opponent's character");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose character", targetingReason, filter) {
                            @Override
                            protected void cardTargeted(final int targetGroupId, final PhysicalCard targetedCard) {
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

                                                                if (totalDestiny >= 3 && totalDestiny <= 4) {
                                                                    gameState.sendMessage("Result: Destiny between 3 and 4");
                                                                    action.appendEffect(
                                                                            new ResetPowerUntilEndOfTurnEffect(action, finalTarget, 0));
                                                                }
                                                                else if (totalDestiny >= 5) {
                                                                    gameState.sendMessage("Result: Destiny is 5 or greater");
                                                                    action.appendEffect(
                                                                            new CaptureCharacterOnTableEffect(action, finalTarget));
                                                                }
                                                                else {
                                                                    gameState.sendMessage("Result: No effect");
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
        }
        return null;
    }
}