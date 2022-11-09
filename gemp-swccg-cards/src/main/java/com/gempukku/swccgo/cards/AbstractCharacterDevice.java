package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.DeployAsCaptiveOption;
import com.gempukku.swccgo.game.DeploymentRestrictionsOption;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;

/**
 * The abstract class providing the for devices that deploy on characters.
 */
public abstract class AbstractCharacterDevice extends AbstractDevice {

    /**
     * Creates a blueprint for a device that deploys on characters.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param title the card title
     * @param uniqueness the uniqueness
     * @param expansionSet the expansionSet
￼    * @param rarity the rarity
￼    */
    public AbstractCharacterDevice(Side side, float destiny, String title, Uniqueness uniqueness, ExpansionSet expansionSet, Rarity rarity) {
        super(side, destiny, PlayCardZoneOption.ATTACHED, title, uniqueness, expansionSet, rarity);
    }

    /**
     * Determines if card can be deployed on opponent's characters.
     * @return true if can be deployed on opponent's characters, otherwise false
     */
    protected boolean canBeDeployedOnOpponentsCharacter() {
        return false;
    }

    @Override
    public boolean canBeDeployedOnCharacter() {
        return true;
    }

    /**
     * Gets the valid deploy target filter based on the game rules for this type, subtype, etc. of card.
     * This method is overridden when a card type, subtype, etc. has special rules about where it can deploy to.
     *
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param isSimDeployAttached true if during simultaneous deployment of pilot/weapon, otherwise false
     * @param ignorePresenceOrForceIcons true if this deployment ignores presence or Force icons requirement
     * @param deploymentRestrictionsOption specifies which deployment restrictions are to be ignored, or null
     * @param deployAsCaptiveOption specifies the way to deploy the card as a captive, or null
     * @return the deploy to target filter based on the card type, subtype, etc.
     */
    @Override
    protected Filter getValidDeployTargetFilterForCardType(String playerId, final SwccgGame game, final PhysicalCard self, boolean isSimDeployAttached, boolean ignorePresenceOrForceIcons, DeploymentRestrictionsOption deploymentRestrictionsOption, DeployAsCaptiveOption deployAsCaptiveOption) {
        if (canBeDeployedOnOpponentsCharacter())
            return Filters.character;
        else
            return Filters.and(Filters.your(self), Filters.character);
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
        return getValidDeployTargetFilter(self.getOwner(), game, self, self, null, true, 0, null, null, null, false, false);
    }

    /**
     * Gets the valid target filter that the card can remain attached to after the attached to card is crossed-over.
     * @param game the game
     * @param self the card
     * @return the filter
     */
    @Override
    public Filter getValidTargetFilterToRemainAttachedToAfterCrossingOver(final SwccgGame game, final PhysicalCard self) {
        return Filters.and(super.getValidTargetFilterToRemainAttachedToAfterCrossingOver(game, self), getGameTextValidToUseDeviceFilter(game, self));
    }
}
