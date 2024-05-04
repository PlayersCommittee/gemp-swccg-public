package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.cards.AbstractDeathStarWeapon;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Statistic;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.FireWeaponAction;
import com.gempukku.swccgo.logic.actions.FireWeaponActionBuilder;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;

import java.util.Collections;
import java.util.List;


/**
 * Set: A New Hope
 * Type: Weapon
 * Subtype: Death Star
 * Title: Superlaser
 */
public class Card2_161 extends AbstractDeathStarWeapon {
    public Card2_161() {
        super(Side.DARK, 3, Title.Superlaser, Uniqueness.UNIQUE, ExpansionSet.A_NEW_HOPE, Rarity.R2);
        setLore("The Death Star has more firepower than the combined might the entire Imperial fleet. Enormous generators power the devastating planetdestroying weapon.");
        setGameText("Deploy on Death Star system at parsec 0. May target a capital starship at Death Star system, or at a system it orbits, using 4 Force. Draw two destiny. Target hit if total destiny > defense value.");
        addIcons(Icon.A_NEW_HOPE);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        if (game.getModifiersQuerying().hasGameTextModification(game.getGameState(), self, ModifyGameTextType.SUPERLASER_IGNORES_DEPLOYMENT_RESTRICTIONS))
            return Filters.Death_Star_system;

        return Filters.and(Filters.Death_Star_system, Filters.systemAtParsec(0));
    }

    @Override
    protected Filter getGameTextValidToUseWeaponFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.Death_Star_system;
    }

    @Override
    protected List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, final SwccgGame game, final PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        // Check condition(s)
        FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                .targetAtSameSystemOrSystemOrbitedUsingForce(Filters.capital_starship, 4, TargetingReason.TO_BE_HIT).finishBuildPrep();
        if (actionBuilder != null) {

            // Build action using common utility
            FireWeaponAction action = actionBuilder.buildFireWeaponWithHitAction(2, Statistic.DEFENSE_VALUE);
            return Collections.singletonList(action);
        }
        return null;
    }
}
