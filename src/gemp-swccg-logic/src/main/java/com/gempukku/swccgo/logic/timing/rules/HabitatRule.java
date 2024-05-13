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
import com.gempukku.swccgo.logic.effects.LoseCardsFromTableSimultaneouslyEffect;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Enforces the rule that causes creatures to be lost if they are no longer in their habitat.
 */
public class HabitatRule implements Rule {
    private ActionsEnvironment _actionsEnvironment;
    private Rule _that;

    /**
     * Creates a rule that causes cards to be lost if they are no longer in their habitat.
     * @param actionsEnvironment the actions environment
     */
    public HabitatRule(ActionsEnvironment actionsEnvironment) {
        _actionsEnvironment = actionsEnvironment;
        _that = this;
    }

    public void applyRule() {
        _actionsEnvironment.addUntilEndOfGameActionProxy(
                new AbstractActionProxy() {
                    @Override
                    public List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult effectResult) {
                        GameState gameState = game.getGameState();
                        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

                        if (TriggerConditions.isTableChanged(game, effectResult)) {
                            // Check for any creatures that are not in their habitat, unless attached to a host.
                            Collection<PhysicalCard> creatures = Filters.filterAllOnTable(game, Filters.creature);
                            List<PhysicalCard> creaturesToLose = new ArrayList<PhysicalCard>();

                            for (PhysicalCard creature : creatures) {
                                if (creature.getAttachedTo() == null) {
                                    PhysicalCard location = modifiersQuerying.getLocationThatCardIsAt(gameState, creature);
                                    if (location != null && !creature.getBlueprint().getHabitatFilter(game, creature).accepts(game, location)) {
                                        creaturesToLose.add(creature);
                                    }
                                }
                            }

                            if (!creaturesToLose.isEmpty()) {

                                RequiredRuleTriggerAction action = new RequiredRuleTriggerAction(_that);
                                action.setText("Make " + GameUtils.getAppendedTextNames(creaturesToLose) + " lost");
                                action.setSingletonTrigger(true);
                                action.appendEffect(
                                        new LoseCardsFromTableSimultaneouslyEffect(action, creaturesToLose, true, true));
                                return Collections.singletonList((TriggerAction) action);
                            }
                        }
                        return null;
                    }
                }
        );
    }
}
