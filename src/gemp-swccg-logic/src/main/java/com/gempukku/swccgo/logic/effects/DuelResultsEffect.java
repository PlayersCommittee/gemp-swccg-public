package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.StandardEffect;

/**
 * An interface to define the methods that effects that provide duel results need to implement.
 */
public interface DuelResultsEffect extends StandardEffect {

    /**
     * Sets the dark side character in the duel.
     * @param card the game
     * @return the dark side character
     */
    void setDarkSideParticipant(PhysicalCard card);

    /**
     * Sets the light side character in the duel.
     * @param card the game
     * @return the light side character
     */
    void setLightSideParticipant(PhysicalCard card);

    /**
     * Gets the dark side duel total.
     * @param duelTotal the dark side duel total
     */
    void setDarkSideDuelTotal(int duelTotal);

    /**
     * Gets the light side duel total.
     * @param duelTotal the light side duel total
     */
    void setLightSideDuelTotal(int duelTotal);
}
