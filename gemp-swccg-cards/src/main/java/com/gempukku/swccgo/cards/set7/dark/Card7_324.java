package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractStarshipWeapon;
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
 * Set: Special Edition
 * Type: Weapon
 * Subtype: Starship
 * Title: SFS L-s9.3 Laser Cannons
 */
public class Card7_324 extends AbstractStarshipWeapon {
    public Card7_324() {
        super(Side.DARK, 5, Title.SFS_Lx93_Laser_Cannons);
        setLore("Developed by Sienar Fleet Systems weapons division to provide added firepower against shielded Rebel fighters. Also provides added coverage to increase accuracy.");
        setGameText("Deploy on your TIE Avenger, TIE Interceptor or TIE Defender. May target a starfighter using X Force, where X = 0 to 3. Draw destiny. If destiny + X > defense value, target hit (lost instead if X = 3).");
        addIcons(Icon.SPECIAL_EDITION);
        addKeywords(Keyword.LASER_CANNON, Keyword.STARSHIP_WEAPON_THAT_DEPLOYS_ON_STARFIGHTERS);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.or(Filters.TIE_Avenger, Filters.TIE_Interceptor, Filters.TIE_Defender));
    }

    @Override
    protected Filter getGameTextValidToUseWeaponFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.or(Filters.TIE_Avenger, Filters.TIE_Interceptor, Filters.TIE_Defender);
    }

    @Override
    protected List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, final SwccgGame game, final PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                .targetUsingForceRange(Filters.or(Filters.starfighter, Filters.canBeTargetedByWeaponAsStarfighter), 0, 3, TargetingReason.TO_BE_HIT).finishBuildPrep();
        if (actionBuilder != null) {

            // Build action using common utility
            FireWeaponAction action = actionBuilder.buildFireWeaponXwingLaserCannonAndSFSLs93LaserCannonsAction();
            return Collections.singletonList(action);
        }
        return null;
    }
}