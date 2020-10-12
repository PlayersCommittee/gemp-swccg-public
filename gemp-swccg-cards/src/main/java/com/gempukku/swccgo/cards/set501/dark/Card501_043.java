package com.gempukku.swccgo.cards.set501.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.AbstractPermanentWeapon;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.FireWeaponAction;
import com.gempukku.swccgo.logic.actions.FireWeaponActionBuilder;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.modifiers.EachWeaponDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 13
 * Type: Character
 * SubType: Alien
 * Title: Aemon Gremm With Percussive Cannon
 */
public class Card501_043 extends AbstractAlien {
    public Card501_043() {
        super(Side.DARK, 2, 3, 3, 2, 4, "Aemon Gremm With Percussive Cannon", Uniqueness.UNIQUE);
        setLore("Crimson Dawn. Hylobon guard leader.");
        setGameText(" Permanent weapon is Percussive Cannon (may target a character or vehicle for free; draw destiny; if destiny > defense value, target hit and is power and forfeit -2). While with your guard or at an opponent's site, opponent's weapon destiny draws here are -1.");
        addKeywords(Keyword.CRIMSON_DAWN, Keyword.LEADER, Keyword.GUARD);
        setSpecies(Species.HYLOBON);
        addIcons(Icon.WARRIOR, Icon.PERMANENT_WEAPON, Icon.VIRTUAL_SET_13);
        setTestingText("Aemon Gremm With Percussive Cannon");
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        //While with another guard or at opponent’s site, each opponent’s weapon destiny is -1 here.
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        List<Modifier> modifiers = new LinkedList<>();
        Condition atOpponentsSiteCondition = new AtCondition(self, Filters.and(Filters.your(opponent), Filters.site));
        Condition withOtherGuardCondition = new WithCondition(self, Filters.and(Filters.other(self), Filters.your(self), Filters.character, Filters.guard));
        modifiers.add(new EachWeaponDestinyModifier(self, Filters.and(Filters.your(opponent), Filters.weapon, Filters.here(self)), new OrCondition(atOpponentsSiteCondition, withOtherGuardCondition), -1));
        return modifiers;
    }

    // Define "Percussive Cannon" permanent weapon
    @Override
    protected AbstractPermanentWeapon getGameTextPermanentWeapon() {
        AbstractPermanentWeapon permanentWeapon = new AbstractPermanentWeapon("Percussive Cannon") {
            @Override
            public List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
                FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, this, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                        .targetForFree(Filters.or(Filters.character, Filters.vehicle, targetedAsCharacter), TargetingReason.TO_BE_HIT).finishBuildPrep();
                if (actionBuilder != null) {

                    // Build action using common utility
                    FireWeaponAction action = actionBuilder.buildFireWeaponWithHitAction(1, Statistic.DEFENSE_VALUE, false, -2, -2);
                    return Collections.singletonList(action);
                }
                return null;
            }
        };
        permanentWeapon.addKeyword(Keyword.CANNON);
        return permanentWeapon;
    }
}
