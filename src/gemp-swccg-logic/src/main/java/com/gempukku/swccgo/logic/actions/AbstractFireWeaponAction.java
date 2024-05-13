package com.gempukku.swccgo.logic.actions;

import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgBuiltInCardBlueprint;
import com.gempukku.swccgo.game.SwccgGame;


/**
 * An abstract action that has the base implementation for all fire weapon actions.
 */
public abstract class AbstractFireWeaponAction extends AbstractGameTextAction implements FireWeaponAction {
    protected PhysicalCard _actionSource;
    protected PhysicalCard _weaponToFire;
    protected SwccgBuiltInCardBlueprint _permanentWeapon;
    protected PhysicalCard _cardFiringWeapon;
    protected boolean _repeatedFiring;
    protected Filter _fireAtTargetFilter;

    /**
     * Creates an action for firing the specified weapon (or card with permanent weapon).
     * @param weaponToFire the weapon (or card with a permanent weapon) to fire
     * @param permanentWeapon the permanent weapon to fire
     * @param repeatedFiring true if repeated firing, otherwise false
     * @param fireAtTargetFilter the filter for where the card can be played
     */
    public AbstractFireWeaponAction(PhysicalCard weaponToFire, SwccgBuiltInCardBlueprint permanentWeapon, boolean repeatedFiring, Filter fireAtTargetFilter) {
        super(weaponToFire, weaponToFire.getOwner(), weaponToFire.getCardId());
        _weaponToFire = weaponToFire;
        _permanentWeapon = permanentWeapon;
        _repeatedFiring = repeatedFiring;
        _fireAtTargetFilter = fireAtTargetFilter;
    }

    @Override
    public Type getType() {
        return Type.GAME_TEXT_FIRE_WEAPON;
    }

    @Override
    public PhysicalCard getActionSource() {
        return _actionSource;
    }

    /**
     * Sets the action source for this fire weapon action. This is used when a weapon is being fired using another card's text.
     * @param source the action source
     */
    @Override
    public void setActionSource(PhysicalCard source) {
        _actionSource = source;
    }

    @Override
    public PhysicalCard getActionAttachedToCard() {
        return  _weaponToFire;
    }

    /**
     * Gets the card built-in that is the source of the action or null if not associated with a card built-in.
     * @return the card built-in, or null
     */
    @Override
    public SwccgBuiltInCardBlueprint getActionAttachedToCardBuiltIn() {
        return getPermanentWeaponToFire();
    }

    /**
     * Gets the weapon (or card with permanent weapon) to fire.
     * @return the weapon to fire
     */
    @Override
    public PhysicalCard getWeaponToFire() {
        return  _weaponToFire;
    }

    /**
     * Gets the permanent weapon to fire, or null.
     * @return the permanent weapon to fire, or null
     */
    @Override
    public SwccgBuiltInCardBlueprint getPermanentWeaponToFire() {
        return _permanentWeapon;
    }

    /**
     * Sets the card firing the weapon
     * @param cardFiringWeapon the card firing the weapon
     */
    public void setCardFiringWeapon(PhysicalCard cardFiringWeapon) {
        _cardFiringWeapon = cardFiringWeapon;
    }

    /**
     * Gets the card firing the weapon.
     * @return the card firing the weapon
     */
    @Override
    public PhysicalCard getCardFiringWeapon() {
        return _cardFiringWeapon;
    }

    /**
     * Determines if this is a repeated firing.
     * @return true or false
     */
    public boolean isRepeatedFiring() {
        return _repeatedFiring;
    }

    /**
     * Gets the title of the weapon (or permanent weapon) to fire.
     * @param game the game
     * @return the title of the weapon to fire
     */
    @Override
    public String getWeaponTitle(SwccgGame game) {
        if (_permanentWeapon != null)
            return _permanentWeapon.getTitle(game);
        else
            return _weaponToFire.getTitle();
    }
}
