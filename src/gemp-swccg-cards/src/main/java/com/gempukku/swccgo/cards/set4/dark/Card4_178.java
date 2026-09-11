package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.cards.AbstractCharacterWeapon;
import com.gempukku.swccgo.cards.GameConditions;
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
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextDeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextDeployCostToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Dagobah
 * Type: Weapon
 * Subtype: Character
 * Title: IG-88's Pulse Cannon
 */
public class Card4_178 extends AbstractCharacterWeapon {
    public Card4_178() {
        super(Side.DARK, 1, "IG-88's Pulse Cannon", Uniqueness.UNIQUE, ExpansionSet.DAGOBAH, Rarity.R);
        setLore("IG-88's personal favorite for mass destruction. Rapid-fire fusion plasma bursts are extremely effective against multiple targets. Not widely used due to incidental damage.");
        setGameText("Use 1 Force to deploy on IG-88, 4 on your other warrior. Adds 2 to power. May target X non-droid characters or creatures using X Force. Draw destiny for each. If destiny = 0, character is power -1 and forfeit -1 until end of turn. If destiny -1 > defense value, target hit.");
        addIcons(Icon.DAGOBAH);
        addKeywords(Keyword.CANNON);
        setMatchingCharacterFilter(Filters.IG88);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextDeployCostModifier(self, 4));
        modifiers.add(new DefinedByGameTextDeployCostToTargetModifier(self, 1, Filters.IG88));
        return modifiers;
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.or(Filters.IG88, Filters.warrior));
    }

    @Override
    protected Filter getGameTextValidToUseWeaponFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.or(Filters.IG88, Filters.warrior);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, Filters.hasAttached(self), 2));
        return modifiers;
    }

    @Override
    protected List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, final SwccgGame game, final PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        // Free firing cannot be used with this weapon (needs X Force; free firing yields X=0).
        if (forFree) {
            return null;
        }

        // Creatures do not participate in battle — creature targeting is for attacks/sniper only.
        Filter targetFilter = Filters.or(Filters.non_droid_character, targetedAsCharacter);
        if (!GameConditions.isDuringBattle(game)) {
            targetFilter = Filters.or(targetFilter, Filters.creature);
        }

        List<FireWeaponAction> actions = new LinkedList<FireWeaponAction>();
        int forceAvailable = GameConditions.forceAvailableToUse(game, playerId);
        int maxX = Math.min(Math.max(forceAvailable, 0), 9);
        for (int x = 1; x <= maxX; x++) {
            if (!GameConditions.canUseForce(game, playerId, x + extraForceRequired)) {
                break;
            }
            FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                    .targetUsingForce(x, targetFilter, x, TargetingReason.TO_BE_HIT).finishBuildPrep();
            if (actionBuilder != null) {
                actions.add(actionBuilder.buildFireWeaponIG88sPulseCannonAction());
            }
        }
        return actions.isEmpty() ? null : actions;
    }
}
