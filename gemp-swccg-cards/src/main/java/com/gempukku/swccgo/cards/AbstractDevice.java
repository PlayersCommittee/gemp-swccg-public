package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;

/**
 * The abstract class providing the common implementation for devices.
 */
public abstract class AbstractDevice extends AbstractDeployable {

    /**
     * Creates a blueprint for a device.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param playCardZoneOption the zone option for playing the card, or null if card has multiple play options
     * @param title the card title
     * @param uniqueness the uniqueness
     * @param expansionSet the expansionSet
     * @param rarity the rarity
     */
    protected AbstractDevice(Side side, float destiny, PlayCardZoneOption playCardZoneOption, String title, Uniqueness uniqueness, ExpansionSet expansionSet, Rarity rarity) {
        super(side, destiny, playCardZoneOption, null, title, uniqueness, expansionSet, rarity);
        setCardCategory(CardCategory.DEVICE);
        addCardType(CardType.DEVICE);
        addIcon(Icon.DEVICE);
    }

    /**
     * Gets a filter for the cards that are valid to use the specified device.
     * @param playerId the player
     * @param game the game
     * @param self the device
     * @return the filter
     */
    @Override
    public Filter getValidToUseDeviceFilter(String playerId, final SwccgGame game, final PhysicalCard self) {
        return Filters.or(getGameTextValidToUseDeviceFilter(game, self), Filters.grantedToUseDevice(self), Filters.grantedToDeployTo(self, null));
    }

    /**
     * Gets the valid place card target filter based on the game rules for this type, subtype, etc. of card.
     * This method is overridden when a card type, subtype, etc. has special rules about where it can be placed to.
     * @param game the game
     * @param self the card
     * @return the place card to target filter based on the card type, subtype, etc.
     */
    @Override
    protected Filter getValidPlaceCardTargetFilterForCardType(final SwccgGame game, final PhysicalCard self) {
        return getValidToUseDeviceFilter(self.getOwner(), game, self);
    }
}
