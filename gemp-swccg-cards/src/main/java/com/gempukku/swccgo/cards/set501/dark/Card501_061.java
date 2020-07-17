package com.gempukku.swccgo.cards.set501.dark;

import com.gempukku.swccgo.cards.AbstractCharacterWeapon;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.FireWeaponAction;
import com.gempukku.swccgo.logic.actions.FireWeaponActionBuilder;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextDeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 13
 * Type: Weapon
 * Subtype: Character
 * Title: Black Sun Blaster
 */
public class Card501_061 extends AbstractCharacterWeapon {
    public Card501_061() {
        super(Side.DARK, 4, "Black Sun Blaster", Uniqueness.RESTRICTED_3);
        setLore("Highly specialized weapon used for disintegration. Commonly found on the black market and in criminal arsenals. One of the favored weapons of the Black Sun.");
        setGameText("Deploy on an alien warrior for 1 Force (free if your Black Sun leader on table). May target a character or vehicle; draw destiny. If destiny +1 > target's printed defense value, target hit and may not be used to satisfy attrition.");
        addIcons(Icon.SPECIAL_EDITION, Icon.VIRTUAL_SET_13);
        addKeyword(Keyword.BLASTER);
        setTestingText("Black Sun Blaster");
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {

        Filter blackSunLeader = Filters.and(Filters.Black_Sun_agent, Filters.leader);
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextDeployCostModifier(self, 1));
        modifiers.add(new DeploysFreeModifier(self, self, new OnTableCondition(self, blackSunLeader)));
        return modifiers;
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.warrior, Filters.alien);
    }

    @Override
    protected Filter getGameTextValidToUseWeaponFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.and(Filters.warrior, Filters.alien);
    }

    @Override
    protected List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, final SwccgGame game, final PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                .targetUsingForce(Filters.or(Filters.character, targetedAsCharacter, Filters.vehicle), 0, TargetingReason.TO_BE_HIT).finishBuildPrep();
        if (actionBuilder != null) {

            // Build action using common utility
            FireWeaponAction action = actionBuilder.buildDisruptorPistolVAction();
            return Collections.singletonList(action);
        }
        return null;
    }

}