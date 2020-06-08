package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.DeployAsCaptiveOption;
import com.gempukku.swccgo.game.DeploymentRestrictionsOption;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

/**
 * The abstract class providing the common implementation for weapons.
 */
public abstract class AbstractWeapon extends AbstractDeployable {

    /**
     * Creates a blueprint for a weapon.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param playCardZoneOption the zone option for playing the card, or null if card has multiple play options
     * @param title the card title
     * @param deployCost the deploy cost
     * @param uniqueness the uniqueness
     */
    protected AbstractWeapon(Side side, Float destiny, PlayCardZoneOption playCardZoneOption, Float deployCost, String title, Uniqueness uniqueness) {
        super(side, destiny, playCardZoneOption, deployCost, title, uniqueness);
        setCardCategory(CardCategory.WEAPON);
        addCardType(CardType.WEAPON);
        addIcon(Icon.WEAPON);
    }

    /**
     * Gets a filter for the cards that are matching characters for this.
     * @return the filter
     */
    @Override
    public Filter getMatchingCharacterFilter() {
        return _matchingCharacterFilter;
    }

    /**
     * Sets the matching character filter.
     * @param filter the filter
     */
    protected final void setMatchingCharacterFilter(Filter filter) {
        _matchingCharacterFilter = filter;
    }

    /**
     * Determines if card can be deployed on opponent's card.
     * @return true if can be deployed on opponent's card, otherwise false
     */
    protected boolean canBeDeployedOnOpponentsCard() {
        return false;
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
        if (canBeDeployedOnOpponentsCard())
            return Filters.any;
        else
            return Filters.or(Filters.your(self), Filters.location);
    }

    /**
     * Gets a filter for the cards that are valid to use the specified weapon.
     * @param playerId the player
     * @param game the game
     * @param self the weapon
     * @return the filter
     */
    @Override
    public Filter getValidToUseWeaponFilter(String playerId, final SwccgGame game, final PhysicalCard self) {
        return Filters.or(getGameTextValidToUseWeaponFilter(game, self), Filters.grantedToUseWeapon(self), Filters.grantedToDeployTo(self, null));
    }

    /**
     * Determines if the weapon (or card with a permanent weapon) can be fired.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param repeatedFiring true if this is a repeated firing, otherwise false
     * @return true if card can be fired, otherwise false
     */
    @Override
    protected boolean checkFireWeaponRequirements(String playerId, SwccgGame game, PhysicalCard self, boolean repeatedFiring) {
        GameState gameState = game.getGameState();
        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

        // In order for a weapon to be fired, the following must be satisfied:
        // 1) It must be able to be used and fired.
        // 2) If attached to a card, the card it is attached to must be able to use it and fire it.
        // 3) If an artillery weapon, it must be powered and have a card present that can use it and fire it.

        // Check if weapon cannot be used
        if (modifiersQuerying.mayNotBeUsed(gameState, self))
            return false;

        // Check if weapon cannot be fired
        if (modifiersQuerying.mayNotBeFired(gameState, self))
            return false;

        // Check if weapon is allowed to fire repeatedly
        if (repeatedFiring && !modifiersQuerying.mayFireWeaponRepeatedly(gameState, self))
            return false;

        // Check that weapon is present at the location
        if (modifiersQuerying.getLocationThatCardIsPresentAt(gameState, self) == null)
            return false;

        // Check if powered (if an artillery weapon that requires a power source)
        if (Filters.artillery_weapon.accepts(game, self) && !modifiersQuerying.doesNotRequirePowerSource(gameState, self)) {
            if (!modifiersQuerying.isPowered(gameState, self))
                return false;
        }

        return true;
    }

    /**
     * Gets the filter that accepts cards that can use this weapon.
     * @param game the game
     * @param self the card
     * @return the filter
     */
    protected Filter getValidFiredByFilter(SwccgGame game, PhysicalCard self) {
        return Filters.and(Filters.owner(self.getOwner()), Filters.hasAttached(self), Filters.canFireWeapon(self));
    }

    /**
     * Determines if the weapon (or card with a permanent weapon) fires during the weapon segment of a battle.
     * @param game the game
     * @param self the card
     * @return true if fires during weapon segment, otherwise false
     */
    @Override
    protected boolean isFiresDuringWeaponsSegment(SwccgGame game, PhysicalCard self) {
        return true;
    }
}
