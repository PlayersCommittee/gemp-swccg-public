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
import com.gempukku.swccgo.logic.timing.results.HitResult;

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

                        Filter hitOutsideBattleOrAttack = Filters.and(Filters.hit, Filters.not(Filters.or(Filters.participatingInAttack, Filters.participatingInBattle)));
                        List<PhysicalCard> hitAndNotInAttackOrBattle = new LinkedList<PhysicalCard>();

                        // A nested hit (Baze firing while already about to be lost from Sniper) must lose only the
                        // newly hit card. Re-losing every hit card on table re-emits about-to-be-lost on the original
                        // card and lets "if about to be lost" fire again.
                        if (TriggerConditions.justHit(game, effectResult, Filters.any)) {
                            PhysicalCard hitCard = ((HitResult) effectResult).getCardHit();
                            if (hitCard != null && hitOutsideBattleOrAttack.accepts(game, hitCard)) {
                                hitAndNotInAttackOrBattle.add(hitCard);
                            }
                        } else if (effectResult.getType() == EffectResult.Type.ATTACK_CANCELED
                                || effectResult.getType() == EffectResult.Type.ATTACK_ENDED
                                || TriggerConditions.battleCanceled(game, effectResult)
                                || TriggerConditions.battleEnded(game, effectResult)
                                || TriggerConditions.justExcludedFromBattle(game, effectResult, Filters.any)
                                || TriggerConditions.moved(game, effectResult, Filters.any)
                                || TriggerConditions.captured(game, effectResult, Filters.any)) {

                            hitAndNotInAttackOrBattle.addAll(Filters.filterAllOnTable(game, hitOutsideBattleOrAttack));
                            hitAndNotInAttackOrBattle.addAll(Filters.filterStacked(game, hitOutsideBattleOrAttack));
                        }

                        if (!hitAndNotInAttackOrBattle.isEmpty()) {
                            RequiredRuleTriggerAction action = new RequiredRuleTriggerAction(_that);
                            action.setSingletonTrigger(true);
                            action.appendEffect(
                                    new LoseCardsFromTableSimultaneouslyEffect(action, hitAndNotInAttackOrBattle, false, true));
                            return Collections.singletonList((TriggerAction) action);
                        }

                        return null;
                    }
                }
        );
    }
}
