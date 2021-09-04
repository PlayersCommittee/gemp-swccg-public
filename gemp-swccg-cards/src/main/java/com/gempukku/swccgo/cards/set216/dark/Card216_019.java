package com.gempukku.swccgo.cards.set216.dark;

import com.gempukku.swccgo.cards.AbstractDeathStarWeapon;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.FireWeaponAction;
import com.gempukku.swccgo.logic.actions.FireWeaponActionBuilder;
import com.gempukku.swccgo.logic.modifiers.MayNotBeCanceledModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTargetedByModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 16
 * Type: Weapon
 * Subtype: Death Star
 * Title: Superlaser (V)
 */
public class Card216_019 extends AbstractDeathStarWeapon {
    public Card216_019() {
        super(Side.DARK, 3, Title.Superlaser, Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("The Death Star has more firepower than the combined might the entire Imperial fleet. Enormous generators power the devastating planetdestroying weapon.");
        setGameText("Deploy on Death Star. May not target planet systems (except Alderaan). Commence Primary Ignition may not be canceled. May target a capital starship at Death Star system, or at a system it orbits, for free. Draw destiny. Target hit if destiny +2 > defense value.");
        addIcons(Icon.A_NEW_HOPE, Icon.VIRTUAL_SET_16);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Death_Star_system;
    }

    @Override
    protected Filter getGameTextValidToUseWeaponFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.Death_Star_system;
    }

    @Override
    protected List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, final SwccgGame game, final PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        // Check condition(s)
        FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                .targetAtSameSystemOrSystemOrbitedForFree(Filters.capital_starship, TargetingReason.TO_BE_HIT).finishBuildPrep();
        if (actionBuilder != null) {

            // Build action using common utility
            FireWeaponAction action = actionBuilder.buildFireWeaponWithHitAction(1, 2, Statistic.DEFENSE_VALUE);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new MayNotBeCanceledModifier(self, Filters.Commence_Primary_Ignition));
        modifiers.add(new MayNotBeTargetedByModifier(self, Filters.and(Filters.planet_system, Filters.not(Filters.Alderaan_system)), self));
        return modifiers;
    }
}
