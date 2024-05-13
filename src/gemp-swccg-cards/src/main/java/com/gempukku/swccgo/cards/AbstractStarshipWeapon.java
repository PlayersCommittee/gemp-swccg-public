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
 * The abstract class providing the common implementation for starship weapons.
 */
public abstract class AbstractStarshipWeapon extends AbstractWeapon {

    /**
     * Creates a blueprint for a starship weapon.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param title the card title
     * @param uniqueness the uniqueness
     * @param expansionSet the ExpansionSet
     * @param rarity the rarity
     */
    protected AbstractStarshipWeapon(Side side, float destiny, String title, Uniqueness uniqueness, ExpansionSet expansionSet, Rarity rarity) {
        super(side, destiny, PlayCardZoneOption.ATTACHED, null, title, uniqueness, expansionSet, rarity);
        setCardSubtype(CardSubtype.STARSHIP);
    }

    /**
     * Gets the valid target filter that the card can remain attached to. If the card becomes attached to a card that is
     * not accepted by this filter, then the attached card will be lost by rule.
     * @param game the game
     * @param self the card
     * @return the filter
     */
    @Override
    public Filter getValidTargetFilterToRemainAttachedTo(final SwccgGame game, final PhysicalCard self) {
        return Filters.and(super.getValidTargetFilterToRemainAttachedTo(game, self), Filters.or(Filters.starship, Filters.location));
    }
}
