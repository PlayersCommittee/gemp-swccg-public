package com.gempukku.swccgo.logic.timing.rules;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.ActionsEnvironment;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.ForfeitReducedToZeroResult;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Enforces the game rule that detects if a character's forfeit value was just reduced to 0.
 */
public class CharactersForfeitReducedToZeroRule implements Rule {
    private ActionsEnvironment _actionsEnvironment;

    /**
     * Creates a rule that detects if a character's forfeit value was just reduced to 0.
     * @param actionsEnvironment the actions environment
     */
    public CharactersForfeitReducedToZeroRule(ActionsEnvironment actionsEnvironment) {
        _actionsEnvironment = actionsEnvironment;
    }

    public void applyRule() {
        _actionsEnvironment.addUntilEndOfGameActionProxy(
                new AbstractActionProxy() {
                    @Override
                    public List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult effectResult) {
                        // Check condition(s)
                        if (TriggerConditions.isTableChanged(game, effectResult)) {

                            List<TriggerAction> actions = new LinkedList<TriggerAction>();

                            GameState gameState = game.getGameState();
                            ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
                            ActionsEnvironment actionsEnvironment = game.getActionsEnvironment();

                            // Check for any characters on table.
                            Collection<PhysicalCard> charactersOnTable = Filters.filterAllOnTable(game, Filters.character);
                            for (PhysicalCard characterOnTable : charactersOnTable) {
                                float prevForfeit = characterOnTable.getLatestInPlayForfeitValue();
                                float curForfeit = modifiersQuerying.getForfeit(gameState, characterOnTable);
                                if (curForfeit < prevForfeit && curForfeit == 0) {
                                    actionsEnvironment.emitEffectResult(new ForfeitReducedToZeroResult(effectResult.getPerformingPlayerId(), characterOnTable));
                                }
                                characterOnTable.setLatestInPlayForfeitValue(curForfeit);
                            }
                            return actions;
                        }
                        return null;
                    }
                }
        );
    }
}
