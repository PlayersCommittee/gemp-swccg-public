package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;

/**
 * The abstract class providing the common implementation for artillery weapons.
 */
public abstract class AbstractArtilleryWeapon extends AbstractWeapon {
    private Float _forfeit;

    /**
     * Creates a blueprint for an artillery weapon.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param deployCost the deploy cost
     * @param forfeit the forfeit value
     * @param title the card title
     */
    protected AbstractArtilleryWeapon(Side side, float destiny, float deployCost, float forfeit, String title) {
        this(side, destiny, deployCost, forfeit, title, null);
    }

    /**
     * Creates a blueprint for an artillery weapon.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param deployCost the deploy cost
     * @param forfeit the forfeit value
     * @param title the card title
     * @param uniqueness the uniqueness
     */
    protected AbstractArtilleryWeapon(Side side, float destiny, float deployCost, float forfeit, String title, Uniqueness uniqueness) {
        super(side, destiny, PlayCardZoneOption.ATTACHED, deployCost, title, uniqueness);
        setCardSubtype(CardSubtype.ARTILLERY);
        _forfeit = forfeit;
    }

    /**
     * Determines if the weapon is fired by a character present rather than the card it is attached to.
     * @return true or false
     */
    @Override
    public boolean isFiredByCharacterPresentOrHere() {
        return true;
    }

    /**
     * Determines if this has a forfeit attribute.
     * @return true if has attribute, otherwise false
     */
    @Override
    public final boolean hasForfeitAttribute() {
        return true;
    }

    /**
     * Gets the forfeit value.
     * @return the forfeit value
     */
    @Override
    public final Float getForfeit() {
        return _forfeit;
    }

    /**
     * Gets the filter that accepts cards that can fire this weapon.
     * @param game the game
     * @param self the card
     * @return the filter
     */
    @Override
    protected final Filter getValidFiredByFilter(SwccgGame game, PhysicalCard self) {
        return Filters.and(Filters.owner(self.getOwner()), Filters.present(self), Filters.canFireWeapon(self));
    }
}
