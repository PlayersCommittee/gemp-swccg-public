package com.gempukku.swccgo.logic.timing.rules;

import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.ActionsEnvironment;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredRuleTriggerAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.effects.LoseCardsFromTableSimultaneouslyEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Enforces the rule that causes hit cards outside of attack or battle to be lost.
 */
public class HitCardOutsideOfAttackOrBattleRule implements Rule {
    private ActionsEnvironment _actionsEnvironment;
    private Rule _that;

    /**
     * Creates a rule that causes hit cards outside of attack or battle to be lost.
     * @param actionsEnvironment the actions environment
     */
    public HitCardOutsideOfAttackOrBattleRule(ActionsEnvironment actionsEnvironment) {
        _actionsEnvironment = actionsEnvironment;
        _that = this;
    }

    public void applyRule() {
        _actionsEnvironment.addUntilEndOfGameActionProxy(
                new AbstractActionProxy() {
                    @Override
                    public List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult effectResult) {

                        if (effectResult.getType() == EffectResult.Type.ATTACK_CANCELED
                                || effectResult.getType() == EffectResult.Type.ATTACK_ENDED
                                || TriggerConditions.battleCanceled(game, effectResult)
                                || TriggerConditions.battleEnded(game, effectResult)
                                || TriggerConditions.justExcludedFromBattle(game, effectResult, Filters.any)
                                || TriggerConditions.justHit(game, effectResult, Filters.any)
                                || TriggerConditions.moved(game, effectResult, Filters.any)
                                || TriggerConditions.captured(game, effectResult, Filters.any)
                                || TriggerConditions.isTableChanged(game, effectResult)) {

                            // Check if any cards outside the attack or battle are 'hit', if any, those are immediately lost.
                            Filter filter = Filters.and(Filters.hit, Filters.not(Filters.or(Filters.participatingInAttack, Filters.participatingInBattle)));
                            List<PhysicalCard> hitAndNotInAttackOrBattle = new LinkedList<PhysicalCard>(Filters.filterAllOnTable(game, filter));
                            hitAndNotInAttackOrBattle.addAll(Filters.filterStacked(game, filter));
                            if (!hitAndNotInAttackOrBattle.isEmpty()) {

                                RequiredRuleTriggerAction action = new RequiredRuleTriggerAction(_that);
                                action.setSingletonTrigger(true);
                                action.appendEffect(
                                        new LoseCardsFromTableSimultaneouslyEffect(action, hitAndNotInAttackOrBattle, true, true));
                                return Collections.singletonList((TriggerAction) action);
                            }
                        }

                        return null;
                    }
                }
        );
    }
}
