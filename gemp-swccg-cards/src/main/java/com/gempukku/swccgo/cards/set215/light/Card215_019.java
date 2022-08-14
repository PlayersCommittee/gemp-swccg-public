package com.gempukku.swccgo.cards.set215.light;

import com.gempukku.swccgo.cards.AbstractCharacterWeapon;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.FireWeaponAction;
import com.gempukku.swccgo.logic.actions.FireWeaponActionBuilder;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.modifiers.TotalWeaponDestinyModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 15
 * Type: Weapon
 * Subtype: Character
 * Title: Stolen Stormtrooper Blaster Rifle
 */
public class Card215_019 extends AbstractCharacterWeapon {
    public Card215_019() {
        super(Side.LIGHT, 3, "Stolen Stormtrooper Blaster Rifle");
        setLore("BlasTech E-11 blaster rifle. Standard issue for Imperial forces. So numerous that many have been stolen by Rebels. Extendable stock. Carries energy for 100 shots.");
        setGameText("Deploy on your Rebel or stormtrooper. May target a character, creature, or vehicle for free. Draw destiny. Add 1 if targeting a vehicle. Target hit if total destiny +1 > defense value. If hit by Chewie or a stormtrooper, target may not be used to satisfy attrition.");
        addIcons(Icon.VIRTUAL_SET_15);
        addKeywords(Keyword.BLASTER_RIFLE);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.or(Filters.Rebel, Filters.stormtrooper));
    }

    @Override
    protected Filter getGameTextValidToUseWeaponFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.warrior;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new TotalWeaponDestinyModifier(self, 1, Filters.vehicle));
        return modifiers;
    }

    @Override
    protected List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                .targetForFree(Filters.or(Filters.character, targetedAsCharacter, Filters.creature, Filters.vehicle), TargetingReason.TO_BE_HIT).finishBuildPrep();
        if (actionBuilder != null) {

            // Build action using common utility
            FireWeaponAction action = actionBuilder.buildStolenStormtrooperBlasterRifleAction(1);
            return Collections.singletonList(action);
        }
        return null;
    }
}
