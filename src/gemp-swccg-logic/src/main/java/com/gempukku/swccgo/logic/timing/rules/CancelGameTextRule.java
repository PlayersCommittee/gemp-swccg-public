package com.gempukku.swccgo.logic.timing.rules;

import com.gempukku.swccgo.common.CardCategory;
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
import com.gempukku.swccgo.logic.timing.results.CanceledGameTextResult;
import com.gempukku.swccgo.logic.timing.results.RestoredGameTextResult;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Enforces the canceling of a card's game text.
 */
public class CancelGameTextRule implements Rule {
    private ActionsEnvironment _actionsEnvironment;
    private Rule _that;

    /**
     * Creates a rule that enforces the canceling of a card's game text.
     * @param actionsEnvironment the actions environment
     */
    public CancelGameTextRule(ActionsEnvironment actionsEnvironment) {
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

                            // Check all the cards in play to see if the game text needs to be canceled (or restored)
                            for (PhysicalCard card : Filters.filterAllOnTable(game, Filters.any)) {

                                // Check if game text needs to be canceled or restored
                                Collection<PhysicalCard> cardsCancelingGameText = modifiersQuerying.getCardsMarkingGameTextCanceled(gameState, card);
                                if (cardsCancelingGameText.isEmpty()) {
                                    if (card.isGameTextCanceled()) {
                                        // Restore game text
                                        triggerActions.add(getRestoreGameTextAction(gameState, card));
                                    }
                                }
                                else {
                                    if (!card.isGameTextCanceled()) {
                                        // Cancel game text
                                        for (PhysicalCard sourceCard : cardsCancelingGameText) {
                                            triggerActions.add(getCancelGameTextAction(gameState, sourceCard, card));
                                        }
                                    }
                                }

                                // For locations, check if game text on sides of locations need to be canceled
                                if (card.getBlueprint().getCardCategory() == CardCategory.LOCATION) {
                                    String dsPlayer = game.getDarkPlayer();
                                    String lsPlayer = game.getLightPlayer();

                                    // Check if DS game text needs to be canceled or restored
                                    Collection<PhysicalCard> cardsCancelingGameTextForDS = modifiersQuerying.getCardsMarkingGameTextCanceledForPlayer(gameState, card, dsPlayer);
                                    if (cardsCancelingGameTextForDS.isEmpty()) {
                                        if (card.isLocationGameTextCanceledForPlayer(dsPlayer)) {
                                            // Restore DS game text
                                            triggerActions.add(getRestoreGameTextForPlayerAction(gameState, card, dsPlayer));
                                        }
                                    }
                                    else {
                                        if (!card.isLocationGameTextCanceledForPlayer(dsPlayer)) {
                                            // Cancel DS game text
                                            for (PhysicalCard sourceCard : cardsCancelingGameTextForDS) {
                                                triggerActions.add(getCancelGameTextForPlayerAction(gameState, sourceCard, card, dsPlayer));
                                            }
                                        }
                                    }

                                    // Check if LS game text needs to be canceled or restored
                                    Collection<PhysicalCard> cardsCancelingGameTextForLS = modifiersQuerying.getCardsMarkingGameTextCanceledForPlayer(gameState, card, lsPlayer);
                                    if (cardsCancelingGameTextForLS.isEmpty()) {
                                        if (card.isLocationGameTextCanceledForPlayer(lsPlayer)) {
                                            // Restore LS game text
                                            triggerActions.add(getRestoreGameTextForPlayerAction(gameState, card, lsPlayer));
                                        }
                                    }
                                    else {
                                        if (!card.isLocationGameTextCanceledForPlayer(lsPlayer)) {
                                            // Cancel LS game text
                                            for (PhysicalCard sourceCard : cardsCancelingGameTextForLS) {
                                                triggerActions.add(getCancelGameTextForPlayerAction(gameState, sourceCard, card, lsPlayer));
                                            }
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
     * Creates an action to cancel a card's game text.
     * @param gameState the game state
     * @param sourceCard the card to be the source of the canceling
     * @param affectedCard the card with game text to cancel
     * @return the action
     */
    private TriggerAction getCancelGameTextAction(final GameState gameState, final PhysicalCard sourceCard, final PhysicalCard affectedCard) {
        RequiredRuleTriggerAction action = new RequiredRuleTriggerAction(_that, sourceCard);
        action.setText("Cancel " + GameUtils.getFullName(affectedCard) + "'s game text");
        action.appendEffect(
                new PassthruEffect(action) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        gameState.sendMessage(GameUtils.getCardLink(sourceCard) + " cancels " + GameUtils.getCardLink(affectedCard) + "'s game text");
                        gameState.cardAffectsCard(null, sourceCard, affectedCard);
                        affectedCard.setGameTextCanceled(true);
                        _actionsEnvironment.emitEffectResult(new CanceledGameTextResult(null, affectedCard));
                    }
                });
        return action;
    }

    /**
     * Creates an action to restore a card's game text.
     * @param gameState the game state
     * @param affectedCard the card
     * @return the action
     */
    private TriggerAction getRestoreGameTextAction(final GameState gameState, final PhysicalCard affectedCard) {
        RequiredRuleTriggerAction action = new RequiredRuleTriggerAction(_that, affectedCard);
        action.setText("Restore " + GameUtils.getFullName(affectedCard) + "'s game text");
        action.appendEffect(
                new PassthruEffect(action) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        gameState.sendMessage(GameUtils.getCardLink(affectedCard) + "'s game text is restored");
                        affectedCard.setGameTextCanceled(false);
                        _actionsEnvironment.emitEffectResult(new RestoredGameTextResult(null, affectedCard));
                    }
                });
        return action;
    }

    /**
     * Creates an action to cancel a location's game text on a player's side of that location.
     * @param gameState the game state
     * @param sourceCard the card to be the source of the canceling
     * @param affectedCard the location
     * @param playerId the player
     * @return the action
     */
    private TriggerAction getCancelGameTextForPlayerAction(final GameState gameState, final PhysicalCard sourceCard, final PhysicalCard affectedCard, final String playerId) {
        RequiredRuleTriggerAction action = new RequiredRuleTriggerAction(_that, sourceCard);
        action.setText("Cancel " + playerId + "'s game text on " + GameUtils.getFullName(affectedCard));
        action.appendEffect(
                new PassthruEffect(action) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        gameState.sendMessage(GameUtils.getCardLink(sourceCard) + " cancels " + playerId + "'s game text on " + GameUtils.getCardLink(affectedCard));
                        gameState.cardAffectsCard(null, sourceCard, affectedCard);
                        affectedCard.setLocationGameTextCanceledForPlayer(true, playerId);
                        _actionsEnvironment.emitEffectResult(new CanceledGameTextResult(null, affectedCard));
                    }
                });
        return action;
    }

    /**
     * Creates an action to restore a location's game text on a player's side of that location.
     * @param gameState the game state
     * @param affectedCard the location
     * @param playerId the player
     * @return the action
     */
    private TriggerAction getRestoreGameTextForPlayerAction(final GameState gameState, final PhysicalCard affectedCard, final String playerId) {
        RequiredRuleTriggerAction action = new RequiredRuleTriggerAction(_that, affectedCard);
        action.setSingletonTrigger(true);
        action.setText("Restore " + playerId + "'s game text on " + GameUtils.getFullName(affectedCard));
        action.appendEffect(
                new PassthruEffect(action) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        gameState.sendMessage(playerId + "'s game text on " + GameUtils.getCardLink(affectedCard) + " is restored");
                        affectedCard.setLocationGameTextCanceledForPlayer(false, playerId);
                        _actionsEnvironment.emitEffectResult(new RestoredGameTextResult(null, affectedCard));
                    }
                });
        return action;
    }
}
