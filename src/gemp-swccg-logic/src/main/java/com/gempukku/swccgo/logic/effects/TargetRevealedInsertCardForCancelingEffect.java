package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.InactiveReason;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.results.InsertCardRevealedResult;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * An abstract class for to target cards being played for canceling.
 */
public abstract class TargetRevealedInsertCardForCancelingEffect extends AbstractTargetCardsEffect {
    private final InsertCardRevealedResult _effectResult;

    /**
     * Creates an effect to target cards accepted by the specified filter.
     * @param action the action performing this effect
     * @param effectResult the 'insert' card revealed effect result
     */
    public TargetRevealedInsertCardForCancelingEffect(Action action, InsertCardRevealedResult effectResult) {
        super(action);
        _effectResult = effectResult;
    }

    @Override
    protected FullEffectResult playEffectReturningResult(final SwccgGame game) {
        PhysicalCard insertCard = _effectResult.getCard();
        if (!insertCard.isInsertCardRevealed())
            return new FullEffectResult(false);

        Map<PhysicalCard, Set<TargetingReason>> targetMap = new HashMap<PhysicalCard, Set<TargetingReason>>();
        targetMap.put(insertCard, Collections.singleton(TargetingReason.TO_BE_CANCELED));
        Map<TargetingReason, Filterable> targetFiltersMap = new HashMap<TargetingReason, Filterable>();
        targetFiltersMap.put(TargetingReason.TO_BE_CANCELED, insertCard);
        _action.addPrimaryTargetCards("", 1, 1, 1, true, false, null, targetMap, new HashMap<InactiveReason, Boolean>(), targetFiltersMap);
        cardTargetedToBeCanceled(insertCard);

        return new FullEffectResult(true);
    }

    /**
     * This method is called when card has been targeted to be canceled.
     * @param targetedCard the targeted card
     */
    protected abstract void cardTargetedToBeCanceled(PhysicalCard targetedCard);
}
