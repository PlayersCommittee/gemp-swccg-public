package com.gempukku.swccgo.logic.timing.rules;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.ActionsEnvironment;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredRuleTriggerAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.ResumeCardResult;
import com.gempukku.swccgo.logic.timing.results.SuspendCardResult;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Enforces the suspending of a card.
 */
public class SuspendCardRule implements Rule {
    private ActionsEnvironment _actionsEnvironment;
    private Rule _that;

    /**
     * Creates a rule that enforces the suspending of a card.
     * @param actionsEnvironment the actions environment
     */
    public SuspendCardRule(ActionsEnvironment actionsEnvironment) {
        _actionsEnvironment = actionsEnvironment;
        _that = this;
    }

    public void applyRule() {
        _actionsEnvironment.addUntilEndOfGameActionProxy(
                new AbstractActionProxy() {
                    @Override
                    public List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult effectResult) {
                        // Check condition(s)
                        if (TriggerConditions.isTableChanged(game, effectResult)) {

                            List<TriggerAction> triggerActions = new LinkedList<TriggerAction>();

                            GameState gameState = game.getGameState();
                            ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

                            // Check all the cards in play to see if the card needs to be suspended (or un-suspended)
                            for (PhysicalCard card : Filters.filterAllOnTable(game, Filters.any)) {

                                // Check if card needs to be suspended or un-suspended
                                Collection<PhysicalCard> cardsSuspendingCard = modifiersQuerying.getCardsMarkingCardSuspended(gameState, card);
                                if (cardsSuspendingCard.isEmpty()) {
                                    if (card.isSuspended()) {
                                        // Un-suspend card
                                        triggerActions.add(getResumeCardAction(gameState, card));
                                    }
                                }
                                else {
                                    if (!card.isSuspended()) {
                                        // Suspend card
                                        for (PhysicalCard sourceCard : cardsSuspendingCard) {
                                            triggerActions.add(getSuspendCardAction(gameState, sourceCard, card));
                                        }
                                    }
                                }
                            }

                            return triggerActions;
                        }
                        return null;
                    }
                });
    }

    /**
     * Creates an action to suspend a card.
     * @param gameState the game state
     * @param sourceCard the card to be the source of the suspending
     * @param affectedCard the card to be suspended
     * @return the action
     */
    private TriggerAction getSuspendCardAction(final GameState gameState, final PhysicalCard sourceCard, final PhysicalCard affectedCard) {
        RequiredRuleTriggerAction action = new RequiredRuleTriggerAction(_that, sourceCard);
        action.setText("Suspend " + GameUtils.getFullName(affectedCard));
        action.appendEffect(
                new PassthruEffect(action) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        gameState.sendMessage(GameUtils.getCardLink(sourceCard) + " suspends " + GameUtils.getCardLink(affectedCard));
                        gameState.cardAffectsCard(null, sourceCard, affectedCard);
                        gameState.suspendCard(affectedCard);
                        _actionsEnvironment.emitEffectResult(new SuspendCardResult(null, affectedCard));
                    }
                });
        return action;
    }

    /**
     * Creates an action to un-suspend a card.
     * @param gameState the game state
     * @param affectedCard the card to be un-suspended
     * @return the action
     */
    private TriggerAction getResumeCardAction(final GameState gameState, final PhysicalCard affectedCard) {
        RequiredRuleTriggerAction action = new RequiredRuleTriggerAction(_that, affectedCard);
        action.setSingletonTrigger(true);
        action.setText("Un-suspend " + GameUtils.getFullName(affectedCard));
        action.appendEffect(
                new PassthruEffect(action) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        gameState.sendMessage(GameUtils.getCardLink(affectedCard) + " is no longer suspended");
                        gameState.resumeCard(affectedCard);
                        _actionsEnvironment.emitEffectResult(new ResumeCardResult(null, affectedCard));
                    }
                });
        return action;
    }
}
