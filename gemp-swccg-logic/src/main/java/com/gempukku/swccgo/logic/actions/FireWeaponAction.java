package com.gempukku.swccgo.logic.actions;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgBuiltInCardBlueprint;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An interface to define the methods that fire weapon actions need to implement.
 */
public interface FireWeaponAction extends Action {

    /**
     * Gets the weapon (or card with permanent weapon) to fire.
     * @return the weapon to fire
     */
    PhysicalCard getWeaponToFire();

    /**
     * Gets the permanent weapon to fire, or null.
     * @return the permanent weapon to fire, or null
     */
    SwccgBuiltInCardBlueprint getPermanentWeaponToFire();

    /**
     * Gets the card firing the weapon.
     * @return the card firing the weapon
     */
    PhysicalCard getCardFiringWeapon();

    /**
     * Gets the title of the weapon (or permanent weapon) to fire.
     * @param game the game
     * @return the title of the weapon to fire
     */
    String getWeaponTitle(SwccgGame game);

    /**
     * Sets the action source for this fire weapon action. This is used when a weapon is being fired using another card's text.
     * @param source the action source
     */
    void setActionSource(PhysicalCard source);

    /**
     * Sets the text to show on the action pop-up on the User Interface.
     * @param text the text
     */
    void setText(String text);
}
