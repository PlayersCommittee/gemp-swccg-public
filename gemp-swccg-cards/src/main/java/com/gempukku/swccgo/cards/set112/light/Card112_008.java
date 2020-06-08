package com.gempukku.swccgo.cards.set112.light;

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
 * Set: Premium (Jabba's Palace Sealed Deck)
 * Type: Weapon
 * Subtype: Character
 * Title: Stun Blaster
 */
public class Card112_008 extends AbstractCharacterWeapon {
    public Card112_008() {
        super(Side.LIGHT, 3, "Stun Blaster");
        setLore("Stolen Merr-Sonn blaster. Causes no permanent damage, though efficiently immobilizes its target. Effects are more severe on animals without a thick hide.");
        setGameText("Deploy on your warrior. May target a character or creature (except rancor or Sarlacc) using 2 Force. Draw destiny. Return character (and cards deployed on character) to owner's hand if destiny +1 > defense value. Creature lost if destiny +3 > defense value.");
        addIcons(Icon.PREMIUM);
        addKeywords(Keyword.BLASTER);
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
                .targetUsingForce(Filters.or(Filters.character, targetedAsCharacter), 2, TargetingReason.OTHER)
                .targetUsingForce(Filters.and(Filters.creature, Filters.except(Filters.or(Filters.Rancor, Filters.Sarlacc))), 2, TargetingReason.TO_BE_LOST)
                .finishBuildPrep();
        if (actionBuilder != null) {

            // Build action using common utility
            FireWeaponAction action = actionBuilder.buildFireWeaponStunBlasterAction(1, 1, 3, Statistic.DEFENSE_VALUE);
            return Collections.singletonList(action);
        }
        return null;
    }
}
