package com.gempukku.swccgo.cards.set214.light;

import com.gempukku.swccgo.cards.AbstractCharacterWeapon;
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
 * Set: Set 14
 * Type: Weapon
 * Subtype: Character
 * Title: Ahsoka's Shoto Lightsaber
 */
public class Card214_015 extends AbstractCharacterWeapon {
    public Card214_015() {
        super(Side.LIGHT, 5, "Ahsoka's Shoto Lightsaber", Uniqueness.UNIQUE);
        setLore("");
        setGameText("Deploy on Ahsoka or a non-[Episode I] Padawan. May target a character or creature for free. Draw two destiny. Target hit, and may not be used to satisfy attrition, if total destiny > defense value (if hit target is an Inquisitor, opponent also loses 1 Force).");
        addIcon(Icon.VIRTUAL_SET_14);
        addKeyword(Keyword.LIGHTSABER);
        addPersona(Persona.AHSOKAS_SHOTO_LIGHTSABER);
        setMatchingCharacterFilter(Filters.Ahsoka);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        Filter ahsokaOrNonEpIPadawan = Filters.and(Filters.your(self), Filters.or(Filters.Ahsoka, Filters.and(Filters.padawan,Filters.not(Icon.EPISODE_I))));
        return Filters.and(Filters.your(self), ahsokaOrNonEpIPadawan);
    }

    @Override
    protected Filter getGameTextValidToUseWeaponFilter(final SwccgGame game, final PhysicalCard self) {
        Filter ahsokaOrNonEpIPadawan = Filters.and(Filters.your(self), Filters.or(Filters.Ahsoka, Filters.and(Filters.padawan,Filters.not(Icon.EPISODE_I))));
        return Filters.and(ahsokaOrNonEpIPadawan);
    }

    @Override
    protected List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, final SwccgGame game, final PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                .targetForFree(Filters.or(Filters.character, targetedAsCharacter, Filters.creature), TargetingReason.TO_BE_HIT).finishBuildPrep();
        if (actionBuilder != null) {
            // new builder
            FireWeaponAction action = actionBuilder.builderAhsokasShotoLightsaber(Filters.and(Keyword.INQUISITOR),1);
            return Collections.singletonList(action);
        }
        return null;
    }
}
