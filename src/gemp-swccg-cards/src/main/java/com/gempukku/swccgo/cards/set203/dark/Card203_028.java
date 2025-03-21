package com.gempukku.swccgo.cards.set203.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.AbstractPermanentWeapon;
import com.gempukku.swccgo.cards.conditions.RepCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Statistic;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.FireWeaponAction;
import com.gempukku.swccgo.logic.actions.FireWeaponActionBuilder;
import com.gempukku.swccgo.logic.modifiers.DeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 3
 * Type: Character
 * Subtype: Alien
 * Title: Ortugg (V)
 */
public class Card203_028 extends AbstractAlien {
    public Card203_028() {
        super(Side.DARK, 3, 3, 4, 2, 4, Title.Ortugg, Uniqueness.UNIQUE, ExpansionSet.SET_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("Gamorrean in charge of the Gamorreans at Jabba's palace. Posted to stand guard at the entrance cavern. Assigned by Jabba to keep an eye on Tessek.");
        setGameText("Permanent weapon is •Ortugg's Ax (may target a character or creature for free; draw destiny; target hit, and its forfeit = 0, if destiny +1 > defense value). While Ortugg is your Rep, your non-unique Gamorreans are deploy -1 and forfeit +3.");
        addIcons(Icon.JABBAS_PALACE, Icon.WARRIOR, Icon.PERMANENT_WEAPON, Icon.VIRTUAL_SET_3);
        setSpecies(Species.GAMORREAN);
        addKeywords(Keyword.GUARD);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter yourNonUniqueGamorreans = Filters.and(Filters.your(self), Filters.non_unique, Filters.Gamorrean);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostModifier(self, yourNonUniqueGamorreans, new RepCondition(self.getOwner(), Filters.title(Title.Ortugg)), -1));
        modifiers.add(new ForfeitModifier(self, yourNonUniqueGamorreans, new RepCondition(self.getOwner(), Filters.title(Title.Ortugg)), 3));
        return modifiers;
    }

    // Define "Ortugg's Ax" permanent weapon
    @Override
    protected AbstractPermanentWeapon getGameTextPermanentWeapon() {
        AbstractPermanentWeapon permanentWeapon = new AbstractPermanentWeapon(Title.Ortuggs_Ax, Uniqueness.UNIQUE) {
            @Override
            public List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
                FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, this, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                        .targetForFree(Filters.or(Filters.character, targetedAsCharacter, Filters.creature), TargetingReason.TO_BE_HIT).finishBuildPrep();
                if (actionBuilder != null) {

                    // Build action using common utility
                    FireWeaponAction action = actionBuilder.buildFireWeaponWithHitAction(1, 1, Statistic.DEFENSE_VALUE, true, 0);
                    return Collections.singletonList(action);
                }
                return null;
            }
        };
        permanentWeapon.addKeyword(Keyword.AX);
        return permanentWeapon;
    }
}
