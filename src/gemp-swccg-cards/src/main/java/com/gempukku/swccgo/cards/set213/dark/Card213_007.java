package com.gempukku.swccgo.cards.set213.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.AbstractPermanentWeapon;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Statistic;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.FireWeaponAction;
import com.gempukku.swccgo.logic.actions.FireWeaponActionBuilder;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 13
 * Type: Character
 * Subtype: Alien
 * Title: Hylobon Enforcer
 */
public class Card213_007 extends AbstractAlien {
    public Card213_007() {
        super(Side.DARK, 3, 4, 3, 2, 4, "Hylobon Enforcer", Uniqueness.RESTRICTED_2, ExpansionSet.SET_13, Rarity.V);
        setLore("Crimson Dawn. Hylobon guard.");
        setGameText("Deploy -1 and forfeit +1 at same or related location as Vos or at an opponent's site. Permanent weapon is Percussive Cannon (may target a character using 1 Force; draw destiny; if destiny +1 > defense value, target's game text is canceled for remainder of turn).");
        addIcons(Icon.WARRIOR, Icon.PERMANENT_WEAPON, Icon.VIRTUAL_SET_13);
        addKeywords(Keyword.CRIMSON_DAWN, Keyword.GUARD);
        setSpecies(Species.HYLOBON);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, -1,
                Filters.or(Filters.sameOrRelatedLocationAs(self, Filters.Vos), Filters.and(Filters.opponents(self.getOwner()), Filters.site))));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForfeitModifier(self, new AtCondition(self, Filters.or(Filters.sameOrRelatedLocationAs(self, Filters.Vos),
                Filters.and(Filters.opponents(self.getOwner()), Filters.site))), 1));
        return modifiers;
    }


    @Override
    protected AbstractPermanentWeapon getGameTextPermanentWeapon() {
        AbstractPermanentWeapon permanentWeapon = new AbstractPermanentWeapon("Percussive Cannon") {
            @Override
            public List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
                FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, this, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                        .targetUsingForce(Filters.or(Filters.character, targetedAsCharacter), 1, TargetingReason.OTHER).finishBuildPrep();
                if (actionBuilder != null) {

                    // Build action
                    FireWeaponAction action = actionBuilder.buildFireWeaponWithCancelGameTextAction(1, 1, Statistic.DEFENSE_VALUE, true);
                    return Collections.singletonList(action);
                }
                return null;
            }
        };
        permanentWeapon.addKeyword(Keyword.CANNON);
        return permanentWeapon;
    }
}
