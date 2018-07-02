package com.gempukku.swccgo.cards.set14.dark;

import com.gempukku.swccgo.cards.AbstractVehicleWeapon;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.FireWeaponAction;
import com.gempukku.swccgo.logic.actions.FireWeaponActionBuilder;

import java.util.ArrayList;
import java.util.List;


/**
 * Set: Theed Palace
 * Type: Weapon
 * Subtype: Vehicle
 * Title: AAT Laser Cannon
 */
public class Card14_126 extends AbstractVehicleWeapon {
    public Card14_126() {
        super(Side.DARK, 4, Title.AAT_Laser_Cannon);
        setLore("High caliber primary tank weapon. Just as effective against massed ground troops as vehicles. Cannot penetrate energy shields.");
        setGameText("Deploy on your AAT. May target a character (use 1 Force) or vehicle (for free) at same or adjacent site. Draw destiny. Character lost if destiny +1 > defense value. Vehicle lost if destiny +3 > defense value. May target a creature for free. Creature is lost.");
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I);
        addKeywords(Keyword.LASER_CANNON);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.AAT);
    }

    @Override
    protected Filter getGameTextValidToUseWeaponFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.AAT;
    }

    @Override
    protected List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, final SwccgGame game, final PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        List<FireWeaponAction> actions = new ArrayList<FireWeaponAction>();

        // Fire action 1
        FireWeaponActionBuilder actionBuilder1 = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                .targetAtSameOrAdjacentSiteUsingForce(Filters.or(Filters.character, targetedAsCharacter), 1, TargetingReason.TO_BE_LOST)
                .targetAtSameOrAdjacentSiteForFree(Filters.vehicle, TargetingReason.TO_BE_LOST).finishBuildPrep();
        if (actionBuilder1 != null) {

            // Build action using common utility
            FireWeaponAction action = actionBuilder1.buildFireWeaponLostAction(1, 1, 3, Statistic.DEFENSE_VALUE);
            action.setText("Fire at character or vehicle");
            actions.add(action);
        }
        // Fire action 2
        FireWeaponActionBuilder actionBuilder2 = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                .targetForFree(Filters.creature, TargetingReason.TO_BE_LOST).finishBuildPrep();
        if (actionBuilder2 != null) {

            // Build action using common utility
            FireWeaponAction action = actionBuilder2.buildFireWeaponCreatureLostAction();
            action.setText("Fire at creature");
            actions.add(action);
        }

        return actions;
    }
}
