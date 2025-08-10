package com.gempukku.swccgo.logic.timing.rules;


import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.TargetId;
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
import com.gempukku.swccgo.logic.timing.results.PlaceCardInPlayResult;
import com.gempukku.swccgo.logic.timing.results.PlayCardResult;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Enforces the game rule that if a character of the same persona of the Jedi Test target was just deployed or placed in play,
 * the Jedi Test is updated to point to that character.
 */
public class JediTestTargetRule implements Rule {
    private ActionsEnvironment _actionsEnvironment;

    /**
     * Creates a rule that if a character of the same persona of the Jedi Test target was just deployed or placed in play,
     * the Jedi Test is updated to point to that character.
     * @param actionsEnvironment the actions environment
     */
    public JediTestTargetRule(ActionsEnvironment actionsEnvironment) {
        _actionsEnvironment = actionsEnvironment;
    }

    public void applyRule() {
        _actionsEnvironment.addUntilEndOfGameActionProxy(
                new AbstractActionProxy() {
                    @Override
                    public List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult effectResult) {
                        // Check condition(s)
                        PhysicalCard character;
                        if (TriggerConditions.justDeployed(game, effectResult, Filters.character)) {
                            character = ((PlayCardResult) effectResult).getPlayedCard();
                        }
                        else if (TriggerConditions.justPlacedInPlay(game, effectResult, Filters.character)) {
                            character = ((PlaceCardInPlayResult) effectResult).getPlacedCard();
                        }
                        else {
                            return null;
                        }

                        GameState gameState = game.getGameState();
                        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

                        // Get all the Jedi Tests on the table.
                        Collection<PhysicalCard> jediTests = Filters.filterAllOnTable(game, Filters.Jedi_Test);

                        // Update the apprentice of the Jedi Test to point to the card with the same persona
                        for (PhysicalCard jediTest : jediTests) {
                            PhysicalCard prevApprentice = jediTest.getTargetedCard(gameState, TargetId.JEDI_TEST_APPRENTICE);
                            if (prevApprentice != null && prevApprentice.getCardId() != character.getCardId()) {
                                Set<Persona> personaSet = prevApprentice.getBlueprint().getPersonas();
                                for (Persona persona : personaSet) {
                                    if (modifiersQuerying.hasPersona(gameState, character, persona)) {
                                        jediTest.setTargetedCard(TargetId.JEDI_TEST_APPRENTICE, jediTest.getTargetGroupId(TargetId.JEDI_TEST_APPRENTICE), character, jediTest.getValidTargetedFilter(TargetId.JEDI_TEST_APPRENTICE));
                                        break;
                                    }
                                }
                            }
                        }
                        return null;
                    }
                }
        );
    }
}

