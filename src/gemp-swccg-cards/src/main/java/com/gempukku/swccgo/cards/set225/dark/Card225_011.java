package com.gempukku.swccgo.cards.set225.dark;

import com.gempukku.swccgo.cards.AbstractVehicleWeapon;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.FireWeaponAction;
import com.gempukku.swccgo.logic.actions.FireWeaponActionBuilder;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalWeaponDestinyModifier;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 25
 * Type: Weapon
 * Subtype: Vehicle
 * Title: AT-M6 Cannon
 */
public class Card225_011 extends AbstractVehicleWeapon {
    public Card225_011() {
        super(Side.DARK, 3, "AT-M6 Cannon", Uniqueness.UNRESTRICTED, ExpansionSet.SET_25, Rarity.V);
        setGameText("Deploy on your AT-M6. May target a character or vehicle at same site. Draw destiny. Add 1 if at an [E7] site. If destiny +1 > than defense value, target is 'hit', cumulatively forfeit -3, and opponent's characters and vehicles at same location are cumulatively power -1.");
        addIcons(Icon.EPISODE_VII, Icon.VIRTUAL_SET_25);
        addKeyword(Keyword.CANNON);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.AT_M6);
    }

    @Override
    protected Filter getGameTextValidToUseWeaponFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.AT_M6;
    }

    @Override
    protected List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, final SwccgGame game, final PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        List<FireWeaponAction> actions = new ArrayList<FireWeaponAction>();

        // Fire action 1
        FireWeaponActionBuilder actionBuilder1 = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
    .targetForFree(Filters.or(Filters.character, targetedAsCharacter, Filters.vehicle), TargetingReason.TO_BE_HIT).finishBuildPrep();
        if (actionBuilder1 != null) {

            // Build action using common utility
            FireWeaponAction action = actionBuilder1.buildFireWeaponATM6CannonAction(1);
            action.setText("Fire at character or vehicle");
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new TotalWeaponDestinyModifier(self, new AtCondition(self, Filters.and(Filters.site, Filters.icon(Icon.EPISODE_VII))), 1));
        return modifiers;
    }
}
