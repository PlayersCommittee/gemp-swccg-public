package com.gempukku.swccgo.cards.set9.dark;

import com.gempukku.swccgo.cards.AbstractDeathStarIIWeapon;
import com.gempukku.swccgo.cards.GameConditions;
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
 * Set: Death Star II
 * Type: Weapon
 * Subtype: Death Star II
 * Title: Superlaser Mark II
 */
public class Card9_182 extends AbstractDeathStarIIWeapon {
    public Card9_182() {
        super(Side.DARK, 3, Title.Superlaser_Mark_II, Uniqueness.UNIQUE);
        setLore("The redesign of the Death Star called for improved defenses against Rebel starships. The superlaser redesign, while not able to target snub fighters, can devastate capital starships.");
        setGameText("Deploys on Death Star II. May be fired only if That Thing's Operational is on table. May fire for free at a capital starship at Death Star II system (or at a system Death Star II is orbiting) for free. Draw three destiny. Target hit if total destiny > defense value.");
        addIcons(Icon.DEATH_STAR_II);
        addKeywords(Keyword.CANNON);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Death_Star_II_system;
    }

    @Override
    protected Filter getGameTextValidToUseWeaponFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.Death_Star_II_system;
    }

    @Override
    protected List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, final SwccgGame game, final PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        // Check condition(s)
        if (GameConditions.canSpot(game, self, Filters.That_Things_Operational)) {
            FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                    .targetAtSameSystemOrSystemOrbitedForFree(Filters.capital_starship, TargetingReason.TO_BE_HIT).finishBuildPrep();
            if (actionBuilder != null) {

                // Build action using common utility
                FireWeaponAction action = actionBuilder.buildFireWeaponWithHitAction(3, Statistic.DEFENSE_VALUE);
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
