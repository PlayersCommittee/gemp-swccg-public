package com.gempukku.swccgo.cards.set14.light;

import com.gempukku.swccgo.cards.AbstractStarshipWeapon;
import com.gempukku.swccgo.cards.conditions.AttachedCondition;
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
import com.gempukku.swccgo.logic.modifiers.AttemptToBlowAwayBlockadeFlagshipTotalModifier;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextDeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Theed Palace
 * Type: Weapon
 * Subtype: Starship
 * Title: Proton Torpedoes
 */
public class Card14_066 extends AbstractStarshipWeapon {
    public Card14_066() {
        super(Side.LIGHT, 4, "Proton Torpedoes", Uniqueness.UNRESTRICTED, ExpansionSet.THEED_PALACE, Rarity.C);
        setLore("Mainstay weapon of Naboo Defense Fighters. Anakin Skywalker used them to surprising effect, as soon as he found the trigger.");
        setGameText("Use 1 Force to deploy on your N-1 starfighter. May target a starship using 1 Force. Draw Destiny. Target hit if destiny > defense value. While on Bravo Fighter and you are attempting to 'blow away' Blockade Flagship, adds 2 to your total.");
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I);
        addKeywords(Keyword.PROTON_TORPEDOES, Keyword.STARSHIP_WEAPON_THAT_DEPLOYS_ON_STARFIGHTERS);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextDeployCostModifier(self, 1));
        return modifiers;
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.N1_starfighter);
    }

    @Override
    protected Filter getGameTextValidToUseWeaponFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.N1_starfighter;
    }

    @Override
    protected List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, final SwccgGame game, final PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                .targetUsingForce(Filters.or(Filters.starship, Filters.canBeTargetedByWeaponAsStarfighter), 1, TargetingReason.TO_BE_HIT).finishBuildPrep();
        if (actionBuilder != null) {

            // Build action using common utility
            FireWeaponAction action = actionBuilder.buildFireWeaponWithHitAction(1, Statistic.DEFENSE_VALUE);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AttemptToBlowAwayBlockadeFlagshipTotalModifier(self, new AttachedCondition(self, Filters.Bravo_Fighter), 2));
        return modifiers;
    }
}
