package com.gempukku.swccgo.cards.set215.light;

import com.gempukku.swccgo.cards.AbstractCharacterWeapon;
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

import java.util.Collections;
import java.util.List;

/**
 * Set: Set 15
 * Type: Weapon
 * Subtype: Character
 * Title: Uncivilized Blaster
 */
public class Card215_021 extends AbstractCharacterWeapon {
    public Card215_021() {
        super(Side.LIGHT, 3, "Uncivilized Blaster", Uniqueness.UNIQUE, ExpansionSet.SET_15, Rarity.V);
        setLore("");
        setGameText("Deploy on your warrior. May target a character, creature, or vehicle for free. Draw destiny. Target hit if destiny +2 > defense value. If hit by Corran, Kanan, or Obi-Wan, target may not be used to satisfy attrition, and opponent loses 1 Force.");
        addIcons(Icon.JABBAS_PALACE, Icon.EPISODE_I, Icon.VIRTUAL_SET_15);
        addKeywords(Keyword.BLASTER);
        setMatchingCharacterFilter(Filters.or(Filters.Corran_Horn, Filters.Kanan, Filters.ObiWan));
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.warrior);
    }

    @Override
    protected Filter getGameTextValidToUseWeaponFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.warrior;
    }

    @Override
    protected List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                .targetUsingForce(Filters.or(Filters.character, targetedAsCharacter, Filters.creature, Filters.vehicle), 0, TargetingReason.TO_BE_HIT).finishBuildPrep();
        if (actionBuilder != null) {

            // Build action using common utility
            FireWeaponAction action = actionBuilder.buildFireWeaponUncivilizedBlasterAction();
            return Collections.singletonList(action);
        }
        return null;
    }
}
