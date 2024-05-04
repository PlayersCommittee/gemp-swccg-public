package com.gempukku.swccgo.cards.set3.light;

import com.gempukku.swccgo.cards.AbstractArtilleryWeapon;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
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
 * Set: Hoth
 * Type: Weapon
 * Subtype: Artillery
 * Title: Planet Defender Ion Cannon
 */
public class Card3_078 extends AbstractArtilleryWeapon {
    public Card3_078() {
        super(Side.LIGHT, 4, 4, 4, Title.Planet_Defender_Ion_Cannon, Uniqueness.UNIQUE, ExpansionSet.HOTH, Rarity.R2);
        setLore("KDY v-150 surface-to-space heavy ion cannon. The most powerful ground-based weapon in the Rebel arsenal. Reserved for strategic installations due to limited supply.");
        setGameText("Deploy on an exterior Rebel Base site. During a battle at related system, may target a capital starship there using 2 Force. Draw destiny. If destiny +3 > armor, all starship weapons aboard target are lost, power = 0 and hyperspeed = 0.");
        addIcons(Icon.HOTH);
        addKeywords(Keyword.ION_CANNON);
    }

    @Override
    public boolean isFiredByCharacterPresentOrHere() {
        return false;
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.exterior_site, Filters.Rebel_Base_location);
    }

    @Override
    protected List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, final SwccgGame game, final PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                .targetAtRelatedSystemUsingForce(Filters.capital_starship, 2, TargetingReason.OTHER).finishBuildPrep();
        if (actionBuilder != null) {

            // Build action using common utility
            FireWeaponAction action = actionBuilder.buildFireWeaponPlanetDefenderIonCannonAction();
            return Collections.singletonList(action);
        }
        return null;
    }
}
