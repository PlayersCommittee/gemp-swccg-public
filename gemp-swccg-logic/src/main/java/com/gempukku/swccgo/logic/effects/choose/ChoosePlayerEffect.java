package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.logic.timing.TargetingEffect;

/**
 * An interface for an effect that chooses a player and provides a method to get the player chosen.
 */
public interface ChoosePlayerEffect extends TargetingEffect {

    /**
     * Gets the player chosen.
     * @return the player chosen
     */
    String getPlayerChosen();
}

