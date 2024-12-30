package com.gempukku.swccgo.cards.set214.light;

import com.gempukku.swccgo.cards.AbstractCharacterWeapon;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
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
        super(Side.LIGHT, 5, "Ahsoka's Shoto Lightsaber", Uniqueness.UNIQUE, ExpansionSet.SET_14, Rarity.V);
        setLore("");
        setGameText("Deploy on a Jedi or Padawan. " +
                "May target a character or creature for free. " +
                "Draw two destiny. " +
                "Target hit, and may not be used to satisfy attrition, if total destiny > defense value (if hit by Ahsoka or a Padawan, opponent also loses 1 Force).");
        addIcon(Icon.VIRTUAL_SET_14);
        addKeyword(Keyword.LIGHTSABER);
        addPersona(Persona.AHSOKAS_SHOTO_LIGHTSABER);
        setMatchingCharacterFilter(Filters.Ahsoka);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        Filter jediOrPadawan = Filters.and(Filters.your(self), Filters.or(Filters.Jedi, Filters.padawan));
        return Filters.and(Filters.your(self), jediOrPadawan);
    }

    @Override
    protected Filter getGameTextValidToUseWeaponFilter(final SwccgGame game, final PhysicalCard self) {
        Filter jediOrPadawan = Filters.and(Filters.your(self), Filters.or(Filters.Jedi, Filters.padawan));
        return Filters.and(Filters.your(self), jediOrPadawan);
    }

    @Override
    protected List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, final SwccgGame game, final PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                .targetForFree(Filters.or(Filters.character, targetedAsCharacter, Filters.creature), TargetingReason.TO_BE_HIT).finishBuildPrep();
        if (actionBuilder != null) {
            // new builder
            FireWeaponAction action = actionBuilder.builderAhsokasShotoLightsaber();
            return Collections.singletonList(action);
        }
        return null;
    }
}
