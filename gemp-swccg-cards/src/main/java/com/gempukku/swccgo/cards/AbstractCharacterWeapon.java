package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;

/**
 * The abstract class providing the common implementation for character weapons.
 */
public abstract class AbstractCharacterWeapon extends AbstractWeapon {

    /**
     * Creates a blueprint for a character weapon.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param title the card title
     */
    protected AbstractCharacterWeapon(Side side, float destiny, String title) {
        this(side, destiny, title, null);
    }

    /**
     * Creates a blueprint for a character weapon.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param title the card title
     * @param uniqueness the uniqueness
     */
    protected AbstractCharacterWeapon(Side side, float destiny, String title, Uniqueness uniqueness) {
        this(side, destiny, title, uniqueness, null, null);
    }

    /**
     * Creates a blueprint for a character weapon.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param title the card title
     * @param uniqueness the uniqueness
     * @param expansionSet the ExpansionSet
     * @param rarity the Rarity
     */
    protected AbstractCharacterWeapon(Side side, float destiny, String title, Uniqueness uniqueness, ExpansionSet expansionSet, Rarity rarity) {
        super(side, destiny, PlayCardZoneOption.ATTACHED, null, title, uniqueness, expansionSet, rarity);
        setCardSubtype(CardSubtype.CHARACTER);
    }

    @Override
    public boolean canBeDeployedOnCharacter() {
        return true;
    }

    /**
     * Gets the valid target filter that the card can remain attached to after the attached to card is crossed-over.
     * @param game the game
     * @param self the card
     * @return the filter
     */
    @Override
    public Filter getValidTargetFilterToRemainAttachedToAfterCrossingOver(final SwccgGame game, final PhysicalCard self) {
        return Filters.and(super.getValidTargetFilterToRemainAttachedToAfterCrossingOver(game, self), getGameTextValidToUseWeaponFilter(game, self));
    }
}
