package com.gempukku.swccgo.logic.timing.rules;

import com.gempukku.swccgo.common.Icon;
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
import com.gempukku.swccgo.logic.effects.AttackEffect;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.*;

/**
 * Enforces the rule that causes creatures to attack each other.
 */
public class CreaturesAttackEachOtherRule implements Rule {
    private ActionsEnvironment _actionsEnvironment;
    private Rule _that;

    /**
     * Creates a rule that causes causes creatures to attack each other.
     * @param actionsEnvironment the actions environment
     */
    public CreaturesAttackEachOtherRule(ActionsEnvironment actionsEnvironment) {
        _actionsEnvironment = actionsEnvironment;
        _that = this;
    }

    public void applyRule() {
        _actionsEnvironment.addUntilEndOfGameActionProxy(
                new AbstractActionProxy() {
                    @Override
                    public List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult effectResult) {
                        final GameState gameState = game.getGameState();
                        final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

                        List<TriggerAction> actions = new ArrayList<>();

                        if (TriggerConditions.isTableChanged(game, effectResult)
                                && gameState.getAttackState() == null
                                && gameState.getBattleState() == null) {

                            // Check for any creatures present at same location (and are either not selective, or not same kind)
                            List<PhysicalCard> locationsWithCreaturesToAttackEachOther = new ArrayList<>();

                            Collection<PhysicalCard> creatures = Filters.filterAllOnTable(game, Filters.and(Filters.creature, Filters.presentAt(Filters.location)));
                            for (PhysicalCard creature : creatures) {
                                final PhysicalCard location = modifiersQuerying.getLocationThatCardIsAt(gameState, creature);
                                if (!locationsWithCreaturesToAttackEachOther.contains(location)) {
                                    for (PhysicalCard otherCreature : Filters.filter(creatures, game, Filters.presentWith(creature))) {
                                        if (modifiersQuerying.getIconCount(gameState, creature, Icon.SELECTIVE_CREATURE) == 0
                                                || modifiersQuerying.getIconCount(gameState, otherCreature, Icon.SELECTIVE_CREATURE) == 0
                                                || !creature.getBlueprint().getModelTypes().containsAll(otherCreature.getBlueprint().getModelTypes())
                                                || !otherCreature.getBlueprint().getModelTypes().containsAll(creature.getBlueprint().getModelTypes())) {
                                            locationsWithCreaturesToAttackEachOther.add(location);
                                            break;
                                        }
                                    }
                                }
                            }

                            for (final PhysicalCard locationWithCreaturesToAttackEachOther : locationsWithCreaturesToAttackEachOther) {

                                final RequiredRuleTriggerAction action = new RequiredRuleTriggerAction(_that);
                                action.skipInitialMessageAndAnimation();
                                action.setText("Make creatures at " + GameUtils.getCardLink(locationWithCreaturesToAttackEachOther) + " attack each other");
                                action.setMessage(null);
                                action.setSingletonTrigger(true);
                                action.appendEffect(
                                        new PassthruEffect(action) {
                                            @Override
                                            protected void doPlayEffect(SwccgGame game) {
                                                Map<PhysicalCard, PhysicalCard> creaturesToAttack = new HashMap<>();
                                                Collection<PhysicalCard> creatures = Filters.filterAllOnTable(game, Filters.and(Filters.creature, Filters.present(locationWithCreaturesToAttackEachOther)));
                                                for (PhysicalCard creature : creatures) {
                                                    for (PhysicalCard otherCreature : Filters.filter(creatures, game, Filters.presentWith(creature))) {
                                                        if (modifiersQuerying.getIconCount(gameState, creature, Icon.SELECTIVE_CREATURE) == 0
                                                                || modifiersQuerying.getIconCount(gameState, otherCreature, Icon.SELECTIVE_CREATURE) == 0
                                                                || !creature.getBlueprint().getModelTypes().containsAll(otherCreature.getBlueprint().getModelTypes())
                                                                || !otherCreature.getBlueprint().getModelTypes().containsAll(creature.getBlueprint().getModelTypes())) {
                                                            creaturesToAttack.put(creature, otherCreature);
                                                        }
                                                    }
                                                }
                                                if (!creaturesToAttack.isEmpty()) {
                                                    PhysicalCard randomCreature = GameUtils.getRandomCards(creaturesToAttack.keySet(), 1).get(0);
                                                    PhysicalCard otherCreature = creaturesToAttack.get(randomCreature);
                                                    gameState.activatedCard(null, randomCreature);
                                                    gameState.activatedCard(null, otherCreature);
                                                    gameState.cardAffectsCard(null, randomCreature, otherCreature);
                                                    gameState.cardAffectsCard(null, otherCreature, randomCreature);
                                                    action.appendEffect(
                                                            new AttackEffect(action, randomCreature, otherCreature));
                                                }
                                            }
                                        });
                                actions.add(action);
                            }
                        }
                        return actions;
                    }
                }
        );
    }
}
