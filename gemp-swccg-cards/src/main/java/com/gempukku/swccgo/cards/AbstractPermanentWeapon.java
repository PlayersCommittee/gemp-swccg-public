package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.FireWeaponAction;

import java.util.List;

/**
 * Defines the base implementation of a permanent weapon.
 */
public abstract class AbstractPermanentWeapon extends AbstractPermanent {

    /**
     * Creates the base implementation of a permanent weapon.
     * @param title the title
     */
    protected AbstractPermanentWeapon(String title) {
        super(title, title, null);
    }

    /**
     * Creates the base implementation of a permanent weapon.
     * @param title the title
     * @param uniqueness the uniqueness
     */
    protected AbstractPermanentWeapon(String title, Uniqueness uniqueness) {
        super(title, title, uniqueness);
    }

    /**
     * Creates the base implementation of a permanent weapon.
     * @param persona the persona
     */
    protected AbstractPermanentWeapon(Persona persona) {
        super(persona.getHumanReadable(), persona.getCrossedOverPersona().getHumanReadable(), Uniqueness.UNIQUE);
        addPersona(persona);
    }

    /**
     * Creates the base implementation of a permanent weapon.
     * @param persona the persona
     * @param title the title
     */
    protected AbstractPermanentWeapon(Persona persona, String title) {
        super(persona.getHumanReadable(), title, Uniqueness.UNIQUE);
        addPersona(persona);
    }

    /**
     * Determines if the built-in is a permanent weapon.
     * @return true or false
     */
    @Override
    public final boolean isWeapon() {
        return true;
    }

    /**
     * Gets the fire weapon actions for each way the permanent weapon can be fired.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param forFree true if playing card for free, otherwise false
     * @param extraForceRequired the extra amount of Force required to perform the fire weapon
     * @param sourceCard the card to initiate the firing
     * @param repeatedFiring true if this is a repeated firing, otherwise false
     * @param targetedAsCharacter filter for cards may be targeted as characters
     * @param defenseValueAsCharacter defense value to use for cards may be targeted as characters, or null
     * @param fireAtTargetFilter the filter for where the card can be played     @return the fire weapon actions
     * @param ignorePerAttackOrBattleLimit
     */
    @Override
    public List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        return null;
    }
}
