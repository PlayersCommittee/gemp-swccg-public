package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * An effect that causes the player performing the action to choose and play an Interrupt from Reserve Deck.
 */
public class PlayInterruptFromReserveDeckEffect extends PlayInterruptFromPileEffect {

    /**
     * Creates an effect that causes the player performing the action to choose and play an Interrupt from Reserve Deck.
     * @param action the action performing this effect
     * @param reshuffle true if pile is reshuffled, otherwise false
     * @param placeOutOfPlay true if Interrupt is placed out of play, otherwise false
     */
    public PlayInterruptFromReserveDeckEffect(Action action, boolean reshuffle, boolean placeOutOfPlay) {
        super(action, Zone.RESERVE_DECK, Filters.any, null, null, reshuffle, placeOutOfPlay);
    }

    /**
     * Creates an effect that causes the player performing the action to choose and play an Interrupt from Reserve Deck as
     * a response to the specified effect.
     * @param action the action performing this effect
     * @param effect the effect to response to
     * @param reshuffle true if pile is reshuffled, otherwise false
     * @param placeOutOfPlay true if Interrupt is placed out of play, otherwise false
     */
    public PlayInterruptFromReserveDeckEffect(Action action, Effect effect, boolean reshuffle, boolean placeOutOfPlay) {
        super(action, Zone.RESERVE_DECK, Filters.any, effect, null, reshuffle, placeOutOfPlay);
    }

    /**
     * Creates an effect that causes the player performing the action to choose and play an Interrupt from Reserve Deck as
     * a response to the specified effect result.
     * @param action the action performing this effect
     * @param effectResult result the effect result to response to
     * @param reshuffle true if pile is reshuffled, otherwise false
     * @param placeOutOfPlay true if Interrupt is placed out of play, otherwise false
     */
    public PlayInterruptFromReserveDeckEffect(Action action, EffectResult effectResult, boolean reshuffle, boolean placeOutOfPlay) {
        super(action, Zone.RESERVE_DECK, Filters.any, null, effectResult, reshuffle, placeOutOfPlay);
    }
}
