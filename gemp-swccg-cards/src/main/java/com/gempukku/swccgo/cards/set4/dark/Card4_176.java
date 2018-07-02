package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.cards.AbstractCharacterWeapon;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.FireWeaponAction;
import com.gempukku.swccgo.logic.actions.FireWeaponActionBuilder;
import com.gempukku.swccgo.logic.conditions.WeaponBeingFiredByCondition;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextDeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextDeployCostToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Dagobah
 * Type: Weapon
 * Subtype: Character
 * Title: Dengar's Blaster Carbine
 */
public class Card4_176 extends AbstractCharacterWeapon {
    public Card4_176() {
        super(Side.DARK, 2, Title.Dengars_Blaster_Carbine, Uniqueness.UNIQUE);
        setLore("Rugged, reliable Valken-38 carbine. Excellent sniper's weapon in the hands of a competent marksman. Stolen from a victim when Dengar was working as an Imperial Assassin.");
        setGameText("Use 1 Force to deploy on Dengar, 3 on your warrior. May target a character, creature or vehicle using 1 Force. Draw destiny. Target hit if destiny +1 > defense value. If hit by Dengar, target's forfeit = 0.");
        addIcons(Icon.DAGOBAH);
        addKeywords(Keyword.BLASTER);
        setMatchingCharacterFilter(Filters.Dengar);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextDeployCostModifier(self, 3));
        modifiers.add(new DefinedByGameTextDeployCostToTargetModifier(self, 1, Filters.Dengar));
        return modifiers;
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.or(Filters.Dengar, Filters.warrior));
    }

    @Override
    protected Filter getGameTextValidToUseWeaponFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.or(Filters.Dengar, Filters.warrior);
    }

    @Override
    protected List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, final SwccgGame game, final PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                .targetUsingForce(Filters.or(Filters.character, targetedAsCharacter, Filters.creature, Filters.vehicle), 1, TargetingReason.TO_BE_HIT).finishBuildPrep();
        if (actionBuilder != null) {

            // Build action using common utility
            FireWeaponAction action = actionBuilder.buildFireWeaponWithHitAction(1, 1, Statistic.DEFENSE_VALUE,
                    new WeaponBeingFiredByCondition(self, Filters.Dengar), true, 0);
            return Collections.singletonList(action);
        }
        return null;
    }
}
