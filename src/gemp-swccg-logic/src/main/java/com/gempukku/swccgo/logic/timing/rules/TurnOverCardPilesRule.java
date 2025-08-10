package com.gempukku.swccgo.logic.timing.rules;

import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.ActionsEnvironment;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredRuleTriggerAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.effects.TurnOverLostPileEffect;
import com.gempukku.swccgo.logic.effects.TurnOverTopOfReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.TurnOverUsedPilesEffect;
import com.gempukku.swccgo.logic.modifiers.ModifierFlag;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Enforces the effects of cards that turn over card piles.
 */
public class TurnOverCardPilesRule implements Rule {
    private ActionsEnvironment _actionsEnvironment;
    private Rule _that;

    /**
     * Creates a rule that enforces the effects of cards that turn over card piles.
     * @param actionsEnvironment the actions environment
     */
    public TurnOverCardPilesRule(ActionsEnvironment actionsEnvironment) {
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

                            // Used Piles (check if should be turned over)
                            boolean usedPilesShouldBeTurnedOver = modifiersQuerying.hasFlagActive(gameState, ModifierFlag.USED_PILES_FACE_UP);
                            if (usedPilesShouldBeTurnedOver != gameState.isUsedPilesTurnedOver()) {

                                RequiredRuleTriggerAction action = new RequiredRuleTriggerAction(_that, null);
                                action.setSingletonTrigger(true);
                                action.setText("Turn over Used Piles");
                                // Perform result(s)
                                action.appendEffect(
                                        new TurnOverUsedPilesEffect(action, usedPilesShouldBeTurnedOver));
                                triggerActions.add(action);
                            }

                            // Lose Piles (check if should be turned over)
                            String darkPlayer = game.getDarkPlayer();
                            boolean darkLostPileShouldBeTurnedOver = modifiersQuerying.hasFlagActive(gameState, ModifierFlag.LOST_PILE_FACE_DOWN, darkPlayer);
                            if (darkLostPileShouldBeTurnedOver != gameState.isLostPileTurnedOver(darkPlayer)) {

                                RequiredRuleTriggerAction action = new RequiredRuleTriggerAction(_that, null);
                                action.setSingletonTrigger(true);
                                action.setText("Turn over " + darkPlayer + "'s Lost Pile");
                                // Perform result(s)
                                action.appendEffect(
                                        new TurnOverLostPileEffect(action, darkPlayer, darkLostPileShouldBeTurnedOver));
                                triggerActions.add(action);
                            }

                            String lightPlayer = game.getLightPlayer();
                            boolean lightLostPileShouldBeTurnedOver = modifiersQuerying.hasFlagActive(gameState, ModifierFlag.LOST_PILE_FACE_DOWN, lightPlayer);
                            if (lightLostPileShouldBeTurnedOver != gameState.isLostPileTurnedOver(lightPlayer)) {

                                RequiredRuleTriggerAction action = new RequiredRuleTriggerAction(_that, null);
                                action.setSingletonTrigger(true);
                                action.setText("Turn over " + lightPlayer + "'s Lost Pile");
                                // Perform result(s)
                                action.appendEffect(
                                        new TurnOverLostPileEffect(action, lightPlayer, lightLostPileShouldBeTurnedOver));
                                triggerActions.add(action);
                            }

                            // Reserve Decks (check if top card should be revealed
                            boolean darkTopOfReserveDeckShouldBeRevealed = modifiersQuerying.hasFlagActive(gameState, ModifierFlag.TOP_OF_RESERVE_DECK_REVEALED, darkPlayer);
                            if (darkTopOfReserveDeckShouldBeRevealed != gameState.isTopCardOfReserveDeckRevealed(darkPlayer)) {

                                RequiredRuleTriggerAction action = new RequiredRuleTriggerAction(_that, null);
                                action.setSingletonTrigger(true);
                                action.setText("Turn over top card of " + darkPlayer + "'s Reserve Deck");
                                // Perform result(s)
                                action.appendEffect(
                                        new TurnOverTopOfReserveDeckEffect(action, darkPlayer, darkTopOfReserveDeckShouldBeRevealed));
                                triggerActions.add(action);
                            }

                            boolean lightTopOfReserveDeckShouldBeRevealed = modifiersQuerying.hasFlagActive(gameState, ModifierFlag.TOP_OF_RESERVE_DECK_REVEALED, lightPlayer);
                            if (lightTopOfReserveDeckShouldBeRevealed != gameState.isTopCardOfReserveDeckRevealed(lightPlayer)) {

                                RequiredRuleTriggerAction action = new RequiredRuleTriggerAction(_that, null);
                                action.setSingletonTrigger(true);
                                action.setText("Turn over top card of " + lightPlayer + "'s Reserve Deck");
                                // Perform result(s)
                                action.appendEffect(
                                        new TurnOverTopOfReserveDeckEffect(action, lightPlayer, lightTopOfReserveDeckShouldBeRevealed));
                                triggerActions.add(action);
                            }

                            return triggerActions;
                        }
                        return null;
                    }
                }
        );
    }
}
