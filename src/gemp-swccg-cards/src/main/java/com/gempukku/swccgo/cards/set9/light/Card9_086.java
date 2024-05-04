package com.gempukku.swccgo.cards.set9.light;

import com.gempukku.swccgo.cards.AbstractStarshipWeapon;
import com.gempukku.swccgo.cards.evaluators.ManeuverOfAttachedToEvaluator;
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
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalWeaponDestinyModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Death Star II
 * Type: Weapon
 * Subtype: Starship
 * Title: A-wing Cannon
 */
public class Card9_086 extends AbstractStarshipWeapon {
    public Card9_086() {
        super(Side.LIGHT, 4, "A-wing Cannon", Uniqueness.UNRESTRICTED, ExpansionSet.DEATH_STAR_II, Rarity.C);
        setLore("Equipped with targeting sensor system. Weapon mounts pivot up to 60 degrees to improve targeting.");
        setGameText("Deploy on your A-Wing. May target a starfighter or squadron for free. Draw destiny. Add your starship's maneuver. Target hit if total destiny > twice target's defense value.");
        addIcons(Icon.DEATH_STAR_II);
        addKeywords(Keyword.CANNON, Keyword.STARSHIP_WEAPON_THAT_DEPLOYS_ON_STARFIGHTERS);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.A_wing);
    }

    @Override
    protected Filter getGameTextValidToUseWeaponFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.A_wing;
    }

    @Override
    protected List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, final SwccgGame game, final PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                .targetForFree(Filters.or(Filters.starfighter, Filters.canBeTargetedByWeaponAsStarfighter, Filters.squadron), TargetingReason.TO_BE_HIT).finishBuildPrep();
        if (actionBuilder != null) {

            // Build action using common utility
            FireWeaponAction action = actionBuilder.buildFireWeaponAwingCannonAction();
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new TotalWeaponDestinyModifier(self, new ManeuverOfAttachedToEvaluator(self)));
        return modifiers;
    }
}
