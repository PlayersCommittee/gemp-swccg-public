package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.cards.AbstractCharacterWeapon;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
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
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextDeployCostToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Dagobah
 * Type: Weapon
 * Subtype: Character
 * Title: IG-88's Neural Inhibitor
 */
public class Card4_177 extends AbstractCharacterWeapon {
    public Card4_177() {
        super(Side.DARK, 1, "IG-88's Neural Inhibitor", Uniqueness.UNIQUE, ExpansionSet.DAGOBAH, Rarity.R);
        setLore("Menotor DAS-430 electromagnetic projectile launcher. Often mistaken for a blaster rifle. Fires small hollow darts filled with neurotoxin which cause temporary paralysis.");
        setGameText("Deploy on IG-88, or use 2 Force on any other bounty hunter. May target a non-droid character using 3 Force. Draw destiny. If destiny +1 > ability, until end of your next turn character is power, forfeit and landspeed = 0.");
        addIcons(Icon.DAGOBAH);
        setMatchingCharacterFilter(Filters.IG88);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextDeployCostToTargetModifier(self, 2, Filters.not(Filters.IG88)));
        return modifiers;
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.or(Filters.IG88, Filters.bounty_hunter));
    }

    @Override
    protected Filter getGameTextValidToUseWeaponFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.or(Filters.IG88, Filters.bounty_hunter);
    }

    @Override
    protected List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, final SwccgGame game, final PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                .targetUsingForce(Filters.and(Filters.or(Filters.non_droid_character, targetedAsCharacter), Filters.hasAbilityDefined), 3, TargetingReason.OTHER).finishBuildPrep();
        if (actionBuilder != null) {

            // Build action using common utility
            FireWeaponAction action = actionBuilder.buildFireWeaponIG88sNeuralInhibitorAction();
            return Collections.singletonList(action);
        }
        return null;
    }
}
