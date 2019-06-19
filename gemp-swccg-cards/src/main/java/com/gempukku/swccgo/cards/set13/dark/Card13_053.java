package com.gempukku.swccgo.cards.set13.dark;

import com.gempukku.swccgo.cards.AbstractCharacterWeapon;
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
 * Set: Reflections III
 * Type: Weapon
 * Subtype: Character
 * Title: Aurra Sing's Blaster Rifle
 */
public class Card13_053 extends AbstractCharacterWeapon {
    public Card13_053() {
        super(Side.DARK, 7, "Aurra Sing's Blaster Rifle", Uniqueness.UNIQUE);
        setLore("Aurra Sing's weapon of choice when she isn't using an opponent's lightsaber against them. Targeting mechanism is so complex only Aurra Sing can decipher it.");
        setGameText("Deploy on Aurra Sing. May target a character or creature for free. Target loses all immunity to attrition for remainder of turn. Draw destiny. Target hit if destiny +1 > defense value. Jedi hit by Aurra Sing are power = 0 for remainder of battle.");
        addIcons(Icon.REFLECTIONS_III, Icon.EPISODE_I);
        addKeywords(Keyword.BLASTER_RIFLE);
        setMatchingCharacterFilter(Filters.Aurra);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.Aurra);
    }

    @Override
    protected Filter getGameTextValidToUseWeaponFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.Aurra;
    }

    @Override
    protected List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, final SwccgGame game, final PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                .targetForFree(Filters.or(Filters.character, targetedAsCharacter, Filters.creature), TargetingReason.TO_BE_HIT).finishBuildPrep();
        if (actionBuilder != null) {

            // Build action using common utility
            FireWeaponAction action = actionBuilder.buildFireWeaponAurraSingsBlasterRifleAction(1, 1, Statistic.DEFENSE_VALUE);
            return Collections.singletonList(action);
        }
        return null;
    }
}
