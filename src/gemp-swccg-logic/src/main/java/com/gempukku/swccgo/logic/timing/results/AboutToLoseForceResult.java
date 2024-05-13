package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;

/**
 * This interface specifies the methods that any effect results that represent "about to lose Force" must implement.
 */
public interface AboutToLoseForceResult {

    /**
     * Gets the source card of the Force loss.
     * @return the source card
     */
    PhysicalCard getSourceCard();

    /**
     * Gets the player to lose Force.
     * @return the player to lose Force
     */
    String getPlayerToLoseForce();

    /**
     * Determines if the Force loss is from a Force drain.
     * @return true or false
     */
    boolean isForceDrain();

    /**
     * Determines if the Force loss is from an 'insert' card.
     * @return true or false
     */
    boolean isFromInsertCard();

    /**
     * Determines if the Force loss is from battle damage.
     * @return true or false
     */
    boolean isBattleDamage();

    /**
     * Gets the amount of Force to lose. This value is calculated at the time this method is called.
     * @param game the game
     * @return the amount of Force to lose
     */
    float getForceLossAmount(SwccgGame game);

    /**
     * Determines if the Force loss may not be reduced.
     * @param game the game
     * @return true or false
     */
    boolean isCannotBeReduced(SwccgGame game);
}
