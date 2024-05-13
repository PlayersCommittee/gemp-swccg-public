package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.InactiveReason;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * An abstract class for to target the card being played.
 */
public abstract class TargetCardBeingPlayedEffect extends AbstractTargetCardsEffect {
    private final RespondablePlayingCardEffect _playingCardEffect;

    /**
     * Creates an effect to target card being played.
     * @param action the action performing this effect
     * @param playingCardEffect the playing card effect
     */
    public TargetCardBeingPlayedEffect(Action action, RespondablePlayingCardEffect playingCardEffect) {
        super(action);
        _playingCardEffect = playingCardEffect;
    }

    @Override
    protected FullEffectResult playEffectReturningResult(final SwccgGame game) {
        if (_playingCardEffect.isCanceled())
            return new FullEffectResult(false);

        Map<PhysicalCard, Set<TargetingReason>> targetMap = new HashMap<PhysicalCard, Set<TargetingReason>>();
        targetMap.put(_playingCardEffect.getCard(), Collections.singleton(TargetingReason.OTHER));
        Map<TargetingReason, Filterable> targetFiltersMap = new HashMap<TargetingReason, Filterable>();
        targetFiltersMap.put(TargetingReason.OTHER, _playingCardEffect.getCard());
        _action.addPrimaryTargetCards("", 1, 1, 1, true, false, null, targetMap, new HashMap<InactiveReason, Boolean>(), targetFiltersMap);
        cardTargeted(_playingCardEffect.getCard());

        return new FullEffectResult(true);
    }

    /**
     * This method is called when card has been targeted.
     * @param targetedCard the targeted card
     */
    protected abstract void cardTargeted(PhysicalCard targetedCard);
}
