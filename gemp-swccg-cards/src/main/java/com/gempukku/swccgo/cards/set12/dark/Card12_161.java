package com.gempukku.swccgo.cards.set12.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PeekAtTopCardOfForcePileEffect;
import com.gempukku.swccgo.cards.effects.RetargetInterruptEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromForcePileOnBottomOfCardPileEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardsOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.TargetingActionUtils;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


/**
 * Set: Coruscant
 * Type: Interrupt
 * Subtype: Used
 * Title: They're Still Coming Through!
 */
public class Card12_161 extends AbstractUsedInterrupt {
    public Card12_161() {
        super(Side.DARK, 4, "They're Still Coming Through!");
        setLore("Despite the barriers erected, sometimes a determined opponent cannot be kept out.");
        setGameText("Cancel Rebel Barrier, It's A Trap!, or Smoke Screen. OR Re-target Fallen Portal by choosing up to two of opponent's characters there instead of your own. OR Peek at the top card of your Force Pile; either return it, or place it under your Force Pile or Reserve Deck.");
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Rebel_Barrier)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Rebel_Barrier, Title.Rebel_Barrier);
            actions.add(action);
        }
        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Its_A_Trap)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Its_A_Trap, Title.Its_A_Trap);
            actions.add(action);
        }
        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Smoke_Screen)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Smoke_Screen, Title.Smoke_Screen);
            actions.add(action);
        }

        // Check condition(s)
        if (GameConditions.hasForcePile(game, playerId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Peek at top card of Force Pile");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new PeekAtTopCardOfForcePileEffect(action, playerId) {
                                        @Override
                                        protected void cardPeekedAt(final PhysicalCard peekedAtCard) {
                                            action.appendEffect(
                                                    new PlayoutDecisionEffect(action, playerId,
                                                            new MultipleChoiceAwaitingDecision("Choose where to place card", new String[]{"Return it", "Place it under Force Pile", "Place it under Reserve Deck"}) {
                                                                @Override
                                                                protected void validDecisionMade(int index, String result) {
                                                                    GameState gameState = game.getGameState();
                                                                    if (index == 0) {
                                                                        gameState.sendMessage(playerId + " chooses to return card to top of Force Pile");
                                                                    }
                                                                    else if (index == 1) {
                                                                        gameState.sendMessage(playerId + " chooses to place card on bottom of Force Pile");
                                                                        action.appendEffect(
                                                                                new PutCardFromForcePileOnBottomOfCardPileEffect(action, peekedAtCard, Zone.FORCE_PILE, true));
                                                                    }
                                                                    else {
                                                                        gameState.sendMessage(playerId + " chooses to place card on bottom of Reserve Deck");
                                                                        action.appendEffect(
                                                                                new PutCardFromForcePileOnBottomOfCardPileEffect(action, peekedAtCard, Zone.RESERVE_DECK, true));
                                                                    }
                                                                }
                                                            }
                                                    )
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
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.or(Filters.Rebel_Barrier, Filters.Its_A_Trap, Filters.Smoke_Screen))
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            actions.add(action);
        }

        Filter yourCharacterFilter = Filters.and(Filters.your(self), Filters.character);

        // TODO: See if reuse function from I Have A Bad Feeling About This / Surprise
        
        // Check condition(s)
        if (TriggerConditions.isPlayingCardTargeting(game, effect, Filters.Fallen_Portal, yourCharacterFilter)) {
            final Action fallenPortalTargetingAction = effect.getAction();
            final List<PhysicalCard> yourCharactersTargeted = TargetingActionUtils.getCardsTargeted(game, fallenPortalTargetingAction, yourCharacterFilter);
            if (!yourCharactersTargeted.isEmpty()) {
                PhysicalCard location = game.getModifiersQuerying().getLocationThatCardIsAt(game.getGameState(), yourCharactersTargeted.get(0));
                if (location != null) {
                    Set<TargetingReason> targetingReasons = TargetingActionUtils.getTargetingReasons(game, fallenPortalTargetingAction, yourCharacterFilter);
                    Filter opponentsCharacterFilter = Filters.and(Filters.opponents(self), Filters.character, Filters.at(location), Filters.canBeTargetedBy(fallenPortalTargetingAction.getActionSource(), targetingReasons));
                    if (GameConditions.canTarget(game, self, opponentsCharacterFilter)) {

                        final PlayInterruptAction action = new PlayInterruptAction(game, self);
                        action.setText("Re-target Fallen Portal");
                        // Choose target(s)
                        action.appendTargeting(
                                new TargetCardsOnTableEffect(action, playerId, "Choose characters to retarget " + GameUtils.getCardLink(fallenPortalTargetingAction.getActionSource()) + " to", 1, 2, targetingReasons, opponentsCharacterFilter) {
                                    @Override
                                    protected void cardsTargeted(final int targetGroupId, Collection<PhysicalCard> targetedCards) {
                                        action.addAnimationGroup(targetedCards);
                                        // Allow response(s)
                                        action.allowResponses("Re-target " + GameUtils.getCardLink(fallenPortalTargetingAction.getActionSource()) + " to " + GameUtils.getAppendedNames(targetedCards),
                                                new RespondablePlayCardEffect(action) {
                                                    @Override
                                                    protected void performActionResults(Action targetingAction) {
                                                        // Get the targeted card(s) from the action using the targetGroupId.
                                                        // This needs to be done in case the target(s) were changed during the responses.
                                                        Collection<PhysicalCard> finalTargets = action.getPrimaryTargetCards(targetGroupId);

                                                        // Perform result(s)
                                                        action.appendEffect(
                                                                new RetargetInterruptEffect(action, fallenPortalTargetingAction, yourCharactersTargeted, finalTargets));
                                                    }
                                                }
                                        );
                                    }
                                }
                        );
                        actions.add(action);
                    }
                }
            }
        }

        return actions;
    }
}