package com.gempukku.swccgo.cards.set14.light;

import com.gempukku.swccgo.cards.AbstractVehicleWeapon;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
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
 * Set: Theed Palace
 * Type: Weapon
 * Subtype: Vehicle
 * Title: Booma
 */
public class Card14_063 extends AbstractVehicleWeapon {
    public Card14_063() {
        super(Side.LIGHT, 4, Title.Booma, Uniqueness.UNRESTRICTED, ExpansionSet.THEED_PALACE, Rarity.C);
        setLore("Lunched from fambaa-mounted catapults. Gungan 'energy balls' are used to disable a variety of mechanized threats. Effectiveness is enhanced by the skill of the user.");
        setGameText("Deploy on your Fambaa. Twice during battle may draw destiny. If destiny < total number of opponent's characters and vehicles present, one of them is lost (opponent's choice).");
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.Fambaa);
    }

    @Override
    protected Filter getGameTextValidToUseWeaponFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.Fambaa;
    }

    @Override
    protected List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, final SwccgGame game, final PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                .twicePerBattle().firesForFreeWithoutTargeting().finishBuildPrep();
        if (actionBuilder != null) {

            // Build action using common utility
            FireWeaponAction action = actionBuilder.buildFireWeaponBoomaAction();
            return Collections.singletonList(action);
        }
        return null;
    }
}
