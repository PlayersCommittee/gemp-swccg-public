package com.gempukku.swccgo.cards.set501.light;

import com.gempukku.swccgo.cards.AbstractCharacterWeapon;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.FireWeaponAction;
import com.gempukku.swccgo.logic.actions.FireWeaponActionBuilder;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.InBattleCondition;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifierFlag;
import com.gempukku.swccgo.logic.modifiers.SpecialFlagModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 13
 * Type: Weapon
 * Subtype: Character
 * Title: Rock
 */
public class Card501_039 extends AbstractCharacterWeapon {
    public Card501_039() {
        super(Side.LIGHT, 5, "Rock");
        setLore("");
        setGameText("Deploy on your warrior or Ewok. May “throw” (place in Used pile) to target a character at same site. Target is power -3 (if Proxima, she is excluded from battle) for remainder of turn. If deployed on a Correlian, you take the first weapon phase action during battle.");
        addIcon(Icon.VIRTUAL_SET_13);
        setTestingText("Rock");
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        Filter warriorOrEwok = Filters.and(Filters.your(self), Filters.or(Filters.warrior, Filters.Ewok));
        return Filters.and(Filters.your(self), warriorOrEwok);
    }

    @Override
    protected Filter getGameTextValidToUseWeaponFilter(final SwccgGame game, final PhysicalCard self) {
        Filter warriorOrEwok = Filters.and(Filters.your(self), Filters.or(Filters.warrior, Filters.Ewok));
        return Filters.and(Filters.your(self), warriorOrEwok);
    }

    @Override
    protected List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, final SwccgGame game, final PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                .targetForFree(Filters.or(Filters.character, targetedAsCharacter), TargetingReason.OTHER).finishBuildPrep();
        if (actionBuilder != null) {

            // Build action using common utility
            FireWeaponAction action = actionBuilder.buildFireWeaponRockAction();
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {

        Filter correlianWithRock = Filters.and(Filters.Corellian, Filters.armedWith(self));
        Condition correlianWithRockInBattle = new InBattleCondition(self, correlianWithRock);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new SpecialFlagModifier(self, correlianWithRockInBattle, ModifierFlag.TAKES_FIRST_BATTLE_WEAPONS_SEGMENT_ACTION, self.getOwner()));
        return modifiers;
    }

}
