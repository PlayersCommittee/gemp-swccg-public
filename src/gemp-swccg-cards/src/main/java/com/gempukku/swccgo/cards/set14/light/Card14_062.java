package com.gempukku.swccgo.cards.set14.light;

import com.gempukku.swccgo.cards.AbstractCharacterWeapon;
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

import java.util.Collections;
import java.util.List;

/**
 * Set: Theed Palace
 * Type: Weapon
 * Subtype: Character
 * Title: Amidala's Blaster
 */
public class Card14_062 extends AbstractCharacterWeapon {
    public Card14_062() {
        super(Side.LIGHT, 6, "Amidala's Blaster", Uniqueness.UNIQUE, ExpansionSet.THEED_PALACE, Rarity.R);
        setLore("Amidala's ability as a warrior is underestimated by those who encounter her. With her weapon of choice, she often uses this fact to her advantage.");
        setGameText("Deploy on Amidala. May target a character or creature for free. Draw destiny. Character is power and forfeit = 0 if destiny > defense value. Creature lost if destiny +2 > defense value.");
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I);
        addKeywords(Keyword.BLASTER);
        setMatchingCharacterFilter(Filters.Amidala);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.Amidala);
    }

    @Override
    protected Filter getGameTextValidToUseWeaponFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.Amidala;
    }

    @Override
    protected List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, final SwccgGame game, final PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                .targetForFree(Filters.or(Filters.character, targetedAsCharacter), TargetingReason.OTHER)
                .targetForFree(Filters.creature, TargetingReason.TO_BE_LOST)
                .finishBuildPrep();
        if (actionBuilder != null) {

            // Build action using common utility
            FireWeaponAction action = actionBuilder.buildFireWeaponAmidalasBlasterAction(1, 2, Statistic.DEFENSE_VALUE);
            return Collections.singletonList(action);
        }
        return null;
    }
}
