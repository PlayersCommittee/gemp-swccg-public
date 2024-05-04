package com.gempukku.swccgo.cards.set13.light;

import com.gempukku.swccgo.cards.AbstractCharacterWeapon;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Statistic;
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
 * Set: Reflections III
 * Type: Weapon
 * Subtype: Character
 * Title: Jar Jar's Electropole
 */
public class Card13_025 extends AbstractCharacterWeapon {
    public Card13_025() {
        super(Side.LIGHT, 7, "Jar Jar's Electropole", Uniqueness.UNIQUE, ExpansionSet.REFLECTIONS_III, Rarity.PM);
        setLore("Jar Jar had to be instructed on how to use an electropole prior to the battle on the great grass plains. It's not his weapon of choice.");
        setGameText("Deploys on Jar Jar. May 'throw' (place in Used Pile) to target a character, creature or vehicle. Draw destiny. If destiny +2 > defense value, target is landspeed = 0 and power -2 for remainder of turn.");
        addIcons(Icon.REFLECTIONS_III, Icon.EPISODE_I);
        addKeywords(Keyword.ELECTROPOLE);
        setMatchingCharacterFilter(Filters.Jar_Jar);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.Jar_Jar);
    }

    @Override
    protected Filter getGameTextValidToUseWeaponFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.Jar_Jar;
    }

    @Override
    protected List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, final SwccgGame game, final PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                .target(Filters.or(Filters.character, targetedAsCharacter, Filters.creature, Filters.vehicle), TargetingReason.OTHER).finishBuildPrep();
        if (actionBuilder != null) {

            // Build action using common utility
            FireWeaponAction action = actionBuilder.buildFireWeaponElectropoleAction(true, 1, 2, Statistic.DEFENSE_VALUE, true, -2);
            return Collections.singletonList(action);
        }
        return null;
    }
}
