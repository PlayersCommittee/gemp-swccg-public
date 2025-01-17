package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * An effect that causes the player performing the action to choose and play an Interrupt from Outside The Game.
 */
public class PlayInterruptFromOutsideTheGameEffect extends PlayInterruptFromPileEffect {

    /**
     * Creates an effect that causes the player performing the action to choose and play an Interrupt from Outside The Game.
     * @param action the action performing this effect
     * @param placeOutOfPlay true if Interrupt is placed out of play, otherwise false
     */
    public PlayInterruptFromOutsideTheGameEffect(Action action, boolean placeOutOfPlay) {
        super(action, Zone.OUTSIDE_OF_DECK, Filters.any, null, null, false, placeOutOfPlay);
    }

    /**
     * Creates an effect that causes the player performing the action to choose and play an Interrupt from Outside The Game.
     * @param action the action performing this effect
     * @param interruptFilter the interrupt filter
     * @param placeOutOfPlay true if Interrupt is placed out of play, otherwise false
     */
    public PlayInterruptFromOutsideTheGameEffect(Action action, Filter interruptFilter, boolean placeOutOfPlay) {
        super(action, Zone.OUTSIDE_OF_DECK, interruptFilter, null, null, false, placeOutOfPlay);
    }

    /**
     * Creates an effect that causes the player performing the action to choose and play an Interrupt from Outside The Game as
     * a response to the specified effect.
     * @param action the action performing this effect
     * @param effect the effect to response to
     * @param placeOutOfPlay true if Interrupt is placed out of play, otherwise false
     */
    public PlayInterruptFromOutsideTheGameEffect(Action action, Effect effect, boolean placeOutOfPlay) {
        super(action, Zone.OUTSIDE_OF_DECK, Filters.any, effect, null, false, placeOutOfPlay);
    }

    /**
     * Creates an effect that causes the player performing the action to choose and play an Interrupt from Outside The Game as
     * a response to the specified effect result.
     * @param action the action performing this effect
     * @param effectResult result the effect result to response to
     * @param placeOutOfPlay true if Interrupt is placed out of play, otherwise false
     */
    public PlayInterruptFromOutsideTheGameEffect(Action action, EffectResult effectResult, boolean placeOutOfPlay) {
        super(action, Zone.OUTSIDE_OF_DECK, Filters.any, null, effectResult, false, placeOutOfPlay);
    }

    /**
     * Creates an effect that causes the player performing the action to choose and play an Interrupt from Outside The Game as
     * a response to the specified effect result.
     * @param action the action performing this effect
     * @param interruptFilter the interrupt filter
     * @param effectResult result the effect result to response to
     * @param placeOutOfPlay true if Interrupt is placed out of play, otherwise false
     */
    public PlayInterruptFromOutsideTheGameEffect(Action action, Filter interruptFilter, EffectResult effectResult, boolean placeOutOfPlay) {
        super(action, Zone.OUTSIDE_OF_DECK, interruptFilter, null, effectResult, false, placeOutOfPlay);
    }
}
