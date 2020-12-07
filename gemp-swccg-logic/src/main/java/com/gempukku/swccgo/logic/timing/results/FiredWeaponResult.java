package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgBuiltInCardBlueprint;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.EffectResult;
import java.util.Collection;

/**
 * This effect result is triggered when a weapon (or permanent weapon) has been fired.
 */
public class FiredWeaponResult extends EffectResult {
    private PhysicalCard _weaponCard;
    private SwccgBuiltInCardBlueprint _permanentWeapon;
    private PhysicalCard _cardFiringWeapon;
    private boolean _thrown;
    private Collection<PhysicalCard> _targets;

    /**
     * Creates an effect result that is triggered when a weapon (or permanent weapon) has been fired.
     * @param game the game
     * @param weaponCard the weapon card (or null if weapon was a permanent weapon)
     * @param permanentWeapon the permanent weapon (or null if weapon is not a permanent weapon)
     * @param cardFiringWeapon the card that fired the weapon (or null if the card is self-firing)
     */
    public FiredWeaponResult(SwccgGame game, PhysicalCard weaponCard, SwccgBuiltInCardBlueprint permanentWeapon, PhysicalCard cardFiringWeapon, boolean thrown, Collection<PhysicalCard> targets) {
        super(Type.FIRED_WEAPON, (weaponCard != null ? weaponCard.getOwner() :
                (permanentWeapon != null ? permanentWeapon.getPhysicalCard(game).getOwner()
                        : (cardFiringWeapon != null ? cardFiringWeapon.getOwner() : null))));
        _weaponCard = weaponCard;
        _permanentWeapon = permanentWeapon;
        _cardFiringWeapon = cardFiringWeapon;
        _thrown = thrown;
        _targets = targets;
    }

    /**
     * Gets the weapon card fired.
     * @return the weapon card, or null
     */
    public PhysicalCard getWeaponCardFired() {
        return _weaponCard;
    }

    /**
     * Gets the permanent weapon fired.
     * @return the permanent weapon built-in, or null
     */
    public SwccgBuiltInCardBlueprint getPermanentWeaponFired() {
        return _permanentWeapon;
    }

    /**
     * Gets the card that fired the weapon.
     * @return the card that fired the weapon, or null
     */
    public PhysicalCard getCardFiringWeapon() {
        return _cardFiringWeapon;
    }

    /**
     * True if the weapon was 'thrown'
     *
     * @return the card that fired the weapon, or null
     */
    public boolean wasThrown() {
        return _thrown;
    }

    /**
     * Returns the cards targeted by the weapon
     *
     * @return the cards that were targeted by the weapon that was fired
     */
    public Collection<PhysicalCard> getTargets() {
        return _targets;
    }
    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        String text = "Fired ";

        if (_thrown) {
            text = "Threw ";
        }

        if (_permanentWeapon != null) {
            return text + GameUtils.getCardLink(_permanentWeapon.getPhysicalCard(game));
        } else {
            return text + GameUtils.getCardLink(_weaponCard);
        }
    }
}
