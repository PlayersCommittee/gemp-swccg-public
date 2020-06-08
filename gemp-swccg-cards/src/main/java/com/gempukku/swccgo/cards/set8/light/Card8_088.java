package com.gempukku.swccgo.cards.set8.light;

import com.gempukku.swccgo.cards.AbstractArtilleryWeapon;
import com.gempukku.swccgo.cards.evaluators.PresentEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.FireWeaponAction;
import com.gempukku.swccgo.logic.actions.FireWeaponActionBuilder;
import com.gempukku.swccgo.logic.modifiers.DoesNotRequirePowerSourceModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalWeaponDestinyModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Endor
 * Type: Weapon
 * Subtype: Artillery
 * Title: Ewok Catapult
 */
public class Card8_088 extends AbstractArtilleryWeapon {
    public Card8_088() {
        super(Side.LIGHT, 7, 2, 4, "Ewok Catapult");
        setLore("Crowning achievement of Chirpa's engineers. Heavy stones from these weapons distracted Imperial AT-ST walkers.");
        setGameText("Deploy on an exterior Endor site. Does not require a power source. Your Ewok present may target a vehicle with armor at same or adjacent site for free. Draw destiny. Add 1 for each Ewok present. Target crashes if total destiny > defense value.");
        addIcons(Icon.ENDOR);
        addKeywords(Keyword.EWOK_WEAPON);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.exterior_Endor_site;
    }

    @Override
    protected Filter getGameTextValidToUseWeaponFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.and(Filters.your(self), Filters.Ewok, Filters.present(self));
    }

    @Override
    protected List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, final SwccgGame game, final PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                .targetAtSameOrAdjacentSiteForFree(Filters.and(Filters.vehicle, Filters.hasArmorDefined), TargetingReason.TO_BE_CRASHED).finishBuildPrep();
        if (actionBuilder != null) {

            // Build action using common utility
            FireWeaponAction action = actionBuilder.buildFireWeaponByCharacterPresentWithCrashAction(1);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DoesNotRequirePowerSourceModifier(self));
        modifiers.add(new TotalWeaponDestinyModifier(self, new PresentEvaluator(self, Filters.Ewok)));
        return modifiers;
    }
}
