package com.gempukku.swccgo.cards.set200.light;

import com.gempukku.swccgo.cards.AbstractPermanentWeapon;
import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.FireWeaponAction;
import com.gempukku.swccgo.logic.actions.FireWeaponActionBuilder;
import com.gempukku.swccgo.logic.modifiers.IsPoweredModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 0
 * Type: Character
 * Subtype: Rebel
 * Title: Captain Yutani With Blaster Cannon
 */
public class Card200_004 extends AbstractRebel {
    public Card200_004() {
        super(Side.LIGHT, 3, 3, 3, 1, 4, "Captain Yutani With Blaster Cannon", Uniqueness.UNIQUE);
        setLore("Scout.");
        setGameText("Artillery weapons here are 'powered'. Permanent weapon is Blaster Cannon (may target a character, creature, or vehicle at same or adjacent site for free; draw destiny; target 'hit' and forfeit = 0 if destiny +1 > defense value).");
        addIcons(Icon.HOTH, Icon.WARRIOR, Icon.PERMANENT_WEAPON, Icon.VIRTUAL_SET_0);
        addKeywords(Keyword.SCOUT, Keyword.CAPTAIN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new IsPoweredModifier(self, Filters.and(Filters.artillery_weapon, Filters.here(self))));
        return modifiers;
    }

    // Define "Blaster Cannon" permanent weapon
    @Override
    protected AbstractPermanentWeapon getGameTextPermanentWeapon() {
        AbstractPermanentWeapon permanentWeapon = new AbstractPermanentWeapon("Blaster Cannon") {
            @Override
            public List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
                FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, this, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                        .targetAtSameOrAdjacentSiteForFree(Filters.or(Filters.character, targetedAsCharacter, Filters.creature, Filters.vehicle), TargetingReason.TO_BE_HIT).finishBuildPrep();
                if (actionBuilder != null) {

                    // Build action using common utility
                    FireWeaponAction action = actionBuilder.buildFireWeaponWithHitAction(1, 1, Statistic.DEFENSE_VALUE, true, 0);
                    return Collections.singletonList(action);
                }
                return null;
            }
        };
        permanentWeapon.addKeyword(Keyword.BLASTER);
        permanentWeapon.addKeyword(Keyword.CANNON);
        return permanentWeapon;
    }
}
