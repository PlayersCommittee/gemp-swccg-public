package com.gempukku.swccgo.cards.set14.light;

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
 * Set: Theed Palace
 * Type: Weapon
 * Subtype: Character
 * Title: Captain Tarpals' Electropole
 */
public class Card14_064 extends AbstractCharacterWeapon {
    public Card14_064() {
        super(Side.LIGHT, 6, "Captain Tarpals' Electropole", Uniqueness.UNIQUE, ExpansionSet.THEED_PALACE, Rarity.C);
        setLore("Tarpals' electropole is nearly as old as he is. Used to enforce the laws of Otoh Gunga.");
        setGameText("Deploys on Captain Tarpals. May 'throw' (place in Used Pile) to target a character, creature or vehicle for free. Draw destiny. If destiny +3 > defense value, target is landspeed = 0 and power -3 for remainder of turn.");
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I);
        addKeywords(Keyword.ELECTROPOLE);
        setMatchingCharacterFilter(Filters.Captain_Tarpals);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.Captain_Tarpals);
    }

    @Override
    protected Filter getGameTextValidToUseWeaponFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.Captain_Tarpals;
    }

    @Override
    protected List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, final SwccgGame game, final PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                .targetForFree(Filters.or(Filters.character, targetedAsCharacter, Filters.creature, Filters.vehicle), TargetingReason.OTHER).finishBuildPrep();
        if (actionBuilder != null) {

            // Build action using common utility
            FireWeaponAction action = actionBuilder.buildFireWeaponElectropoleAction(true, 1, 3, Statistic.DEFENSE_VALUE, true, -3);
            return Collections.singletonList(action);
        }
        return null;
    }
}
