package com.gempukku.swccgo.cards.set3.light;

import com.gempukku.swccgo.cards.AbstractVehicleWeapon;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.FireWeaponAction;
import com.gempukku.swccgo.logic.actions.FireWeaponActionBuilder;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextDeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Hoth
 * Type: Weapon
 * Subtype: Vehicle
 * Title: Power Harpoon
 */
public class Card3_079 extends AbstractVehicleWeapon {
    public Card3_079() {
        super(Side.LIGHT, 5, Title.Power_Harpoon, Uniqueness.UNRESTRICTED, ExpansionSet.HOTH, Rarity.U1);
        setLore("High-powered harpoon attached to a retractable flexi-steel tow cable and fusion-head disk designed by Beryl Chiffonage as a reserve defense against Imperial AT-ATs.");
        setGameText("Use 2 Force to deploy on your combat or shuttle vehicle. May target an AT-AT or AT-ST for 1 Force. Draw destiny. Target crashes if destiny + your vehicle's maneuver > 8. Your vehicle crashes if destiny = 0.");
        addIcons(Icon.HOTH);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextDeployCostModifier(self, 2));
        return modifiers;
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.or(Filters.combat_vehicle, Filters.shuttle_vehicle));
    }

    @Override
    protected Filter getGameTextValidToUseWeaponFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.or(Filters.combat_vehicle, Filters.shuttle_vehicle);
    }

    @Override
    protected List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, final SwccgGame game, final PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                .targetUsingForce(Filters.or(Filters.AT_AT, Filters.AT_ST), 1, TargetingReason.TO_BE_CRASHED).finishBuildPrep();
        if (actionBuilder != null) {

            // Build action using common utility
            FireWeaponAction action = actionBuilder.buildFireWeaponPowerHarpoonAction();
            return Collections.singletonList(action);
        }
        return null;
    }
}
