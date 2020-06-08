package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.AboutToForfeitCardFromTableResult;
import com.gempukku.swccgo.logic.timing.results.AboutToLoseCardFromTableResult;

import java.util.Collections;
import java.util.List;


/**
 * Set: A New Hope
 * Type: Interrupt
 * Subtype: Lost
 * Title: We Have A Prisoner
 */
public class Card2_142 extends AbstractLostInterrupt {
    public Card2_142() {
        super(Side.DARK, 3, Title.We_Have_A_Prisoner);
        setLore("'You are part of the Rebel Alliance, and a traitor. Take her away!'");
        setGameText("Use 1 Force if opponent's character is about to be lost or forfeited from battle. It is captured instead (character is first restored to normal). OR Use X Force to capture all characters on board a captured starship, where X = twice the number of characters.");
        addIcons(Icon.A_NEW_HOPE);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        TargetingReason targetingReason = TargetingReason.TO_BE_CAPTURED;
        Filter opponentsCharacterFilter = Filters.and(Filters.opponents(self), Filters.character, Filters.participatingInBattle, Filters.canBeTargetedBy(self, targetingReason));

        // Check condition(s)
        if (TriggerConditions.isAboutToBeLost(game, effectResult, opponentsCharacterFilter)
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1)) {
            final AboutToLoseCardFromTableResult result = (AboutToLoseCardFromTableResult) effectResult;
            final PhysicalCard cardToBeLost = result.getCardToBeLost();

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Capture " + GameUtils.getFullName(cardToBeLost));
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose character", targetingReason, cardToBeLost) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Pay cost(s)
                            action.appendCost(
                                    new UseForceEffect(action, playerId, 1));
                            // Allow response(s)
                            action.allowResponses("Capture " + GameUtils.getCardLink(targetedCard),
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                            // Perform result(s)
                                            result.getPreventableCardEffect().preventEffectOnCard(finalTarget);
                                            action.appendEffect(
                                                    new RestoreCardToNormalEffect(action, finalTarget));
                                            action.appendEffect(
                                                    new CaptureCharacterOnTableEffect(action, finalTarget));
                                        }
                                    }
                            );
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        // Check condition(s)
        if (TriggerConditions.isAboutToBeForfeited(game, effectResult, opponentsCharacterFilter)
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1)) {
            final AboutToForfeitCardFromTableResult result = (AboutToForfeitCardFromTableResult) effectResult;
            final PhysicalCard cardToBeForfeited = result.getCardToBeForfeited();

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Capture " + GameUtils.getFullName(cardToBeForfeited));
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose character", targetingReason, cardToBeForfeited) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Pay cost(s)
                            action.appendCost(
                                    new UseForceEffect(action, playerId, 1));
                            // Allow response(s)
                            action.allowResponses("Capture " + GameUtils.getCardLink(targetedCard),
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                            // Perform result(s)
                                            result.getForfeitCardEffect().preventEffectOnCard(finalTarget);
                                            action.appendEffect(
                                                    new RestoreCardToNormalEffect(action, finalTarget));
                                            action.appendEffect(
                                                    new CaptureCharacterOnTableEffect(action, finalTarget));
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
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        // List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // TODO: Capture characters aboard captured starship

        // return actions;
        return null;
    }
}