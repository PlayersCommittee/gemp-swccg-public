package com.gempukku.swccgo.cards.set3.light;

import com.gempukku.swccgo.cards.AbstractStarshipWeapon;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
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
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextDeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.MayBeUsedByLandedStarshipModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Hoth
 * Type: Weapon
 * Subtype: Starship
 * Title: Surface Defense Cannon
 */
public class Card3_080 extends AbstractStarshipWeapon {
    public Card3_080() {
        super(Side.LIGHT, 5, Title.Surface_Defense_Cannon, Uniqueness.UNRESTRICTED, ExpansionSet.HOTH, Rarity.R2);
        setLore("BlasTech Ax-108 'Ground Buzzer' blaster cannon. Designed to drop from a concealed gun pod on a starship. Targets using proximity motion sensors. Has 360-degree firing arc.");
        setGameText("Use 1 Force to deploy on your starfighter, free on Falcon. May target a character or creature at same site using 1 Force. Draw destiny. Target hit if destiny +1 > defense value.");
        addIcons(Icon.HOTH);
        addKeywords(Keyword.BLASTER, Keyword.CANNON, Keyword.STARSHIP_WEAPON_THAT_DEPLOYS_ON_STARFIGHTERS);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextDeployCostModifier(self, 1));
        modifiers.add(new DeploysFreeToTargetModifier(self, Filters.Falcon));
        modifiers.add(new MayBeUsedByLandedStarshipModifier(self));
        return modifiers;
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.or(Filters.Falcon, Filters.starfighter));
    }

    @Override
    protected Filter getGameTextValidToUseWeaponFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.or(Filters.Falcon, Filters.starfighter);
    }

    @Override
    protected List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, final SwccgGame game, final PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                .targetUsingForce(Filters.and(Filters.or(Filters.character, targetedAsCharacter, Filters.creature), Filters.atSameSite(self)), 1, TargetingReason.TO_BE_HIT).finishBuildPrep();
        if (actionBuilder != null) {

            // Build action using common utility
            FireWeaponAction action = actionBuilder.buildFireWeaponWithHitAction(1, 1, Statistic.DEFENSE_VALUE);
            return Collections.singletonList(action);
        }
        return null;
    }
}
