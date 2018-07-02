package com.gempukku.swccgo.cards.set3.light;

import com.gempukku.swccgo.cards.AbstractArtilleryWeapon;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.FireWeaponAction;
import com.gempukku.swccgo.logic.actions.FireWeaponActionBuilder;

import java.util.Collections;
import java.util.List;

/**
 * Set: Hoth
 * Type: Weapon
 * Subtype: Artillery
 * Title: Atgar Laser Cannon
 */
public class Card3_072 extends AbstractArtilleryWeapon {
    public Card3_072() {
        super(Side.LIGHT, 5, 2, 2, "Atgar Laser Cannon");
        setLore("Atgar 1.4 FD P-tower anti-vehicle cannon. Adapted to operate with minimal performance loss in the extremes of a cold environment.");
        setGameText("Deploy on an exterior planet site. Your warrior present may target a vehicle at same or adjacent site using 2 Force. Draw destiny. Target crashes if destiny +2 > armor. Target hit if destiny +1 > maneuver.");
        addIcons(Icon.HOTH);
        addKeywords(Keyword.LASER_CANNON);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.exterior_planet_site;
    }

    @Override
    protected Filter getGameTextValidToUseWeaponFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.and(Filters.your(self), Filters.warrior, Filters.present(self));
    }

    @Override
    protected List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, final SwccgGame game, final PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                .targetAtSameOrAdjacentSiteUsingForce(Filters.and(Filters.vehicle, Filters.hasArmorDefined), 2, TargetingReason.TO_BE_CRASHED)
                .targetAtSameOrAdjacentSiteUsingForce(Filters.and(Filters.vehicle, Filters.hasManeuverDefined), 2, TargetingReason.TO_BE_HIT).finishBuildPrep();
        if (actionBuilder != null) {

            // Build action using common utility
            FireWeaponAction action = actionBuilder.buildFireWeaponAtgarLaserCannonAction();
            return Collections.singletonList(action);
        }
        return null;
    }
}
