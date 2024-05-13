package com.gempukku.swccgo.logic.timing.rules;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.ActionsEnvironment;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredRuleTriggerAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.StackActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Enforces the game rule that at the end of a Bomber owner's battle phase, any bombers making a Bombing Run must return
 * to the related system.
 */
public class BombingRunRule implements Rule {
    private ActionsEnvironment _actionsEnvironment;
    private Rule _that;

    /**
     *  Creates a rule that at the end of a Bomber owner's battle phase, any bombers making a Bombing Run must return
     * to the related system.
     * @param actionsEnvironment the actions environment
     */
    public BombingRunRule(ActionsEnvironment actionsEnvironment) {
        _actionsEnvironment = actionsEnvironment;
        _that = this;
    }

    public void applyRule() {
        _actionsEnvironment.addUntilEndOfGameActionProxy(
                new AbstractActionProxy() {
                    @Override
                    public List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult effectResult) {
                        // Check condition(s)
                        if (TriggerConditions.isEndOfEachPhase(game, effectResult, Phase.BATTLE)) {
                            GameState gameState = game.getGameState();
                            String playerId = gameState.getCurrentPlayerId();

                            List<TriggerAction> triggerActions = new LinkedList<TriggerAction>();

                            // Check for any of owner's cards making a bombing run
                            Collection<PhysicalCard> cardsMakingBombingRun = Filters.filterAllOnTable(game, Filters.and(Filters.owner(playerId), Filters.makingBombingRun));
                            for (PhysicalCard cardMakingBombingRun : cardsMakingBombingRun) {
                                Action moveAction = cardMakingBombingRun.getBlueprint().getMoveToEndBombingRunAction(cardMakingBombingRun.getOwner(), game, cardMakingBombingRun, false, false);

                                RequiredRuleTriggerAction action = new RequiredRuleTriggerAction(_that, cardMakingBombingRun);
                                action.setText("End Bombing Run");
                                if (moveAction != null) {
                                    action.appendEffect(
                                            new StackActionEffect(action, moveAction));
                                }
                                else {
                                    action.appendEffect(
                                            new LoseCardFromTableEffect(action, cardMakingBombingRun));
                                }
                                triggerActions.add(action);
                            }
                            return triggerActions;
                        }
                        return null;
                    }
                });
    }
}
