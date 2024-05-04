package com.gempukku.swccgo.cards.set200.light;

import com.gempukku.swccgo.cards.AbstractPermanentWeapon;
import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
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
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 0
 * Type: Character
 * Subtype: Republic
 * Title: Captain Rex, 501st Legion
 */
public class Card200_003 extends AbstractRepublic {
    public Card200_003() {
        super(Side.LIGHT, 2, 4, 3, 3, 4, Title.Rex, Uniqueness.UNIQUE, ExpansionSet.SET_0, Rarity.V);
        setArmor(4);
        setLore("Leader. Clone trooper.");
        setGameText("Your clones deploy -1 here. While at an [Episode 1] battleground site, adds one battle destiny. Permanent weapon is •Twin Blasters (may target a character for free; draw two destiny; target hit and forfeit -3 if total destiny -2 > defense value).");
        addIcons(Icon.EPISODE_I, Icon.WARRIOR, Icon.PERMANENT_WEAPON, Icon.CLONE_ARMY, Icon.VIRTUAL_SET_0);
        addKeywords(Keyword.LEADER, Keyword.CLONE_TROOPER, Keyword.CAPTAIN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.your(self), Filters.clone), -1, Filters.here(self)));
        modifiers.add(new AddsBattleDestinyModifier(self, new AtCondition(self, Filters.and(Icon.EPISODE_I, Filters.battleground_site)), 1));
        return modifiers;
    }

    // Define "Twin Blasters" permanent weapon
    @Override
    protected AbstractPermanentWeapon getGameTextPermanentWeapon() {
        AbstractPermanentWeapon permanentWeapon = new AbstractPermanentWeapon("Twin Blasters", Uniqueness.UNIQUE) {
            @Override
            public List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
                FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, this, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                        .targetForFree(Filters.or(Filters.character, targetedAsCharacter), TargetingReason.TO_BE_HIT).finishBuildPrep();
                if (actionBuilder != null) {

                    // Build action using common utility
                    FireWeaponAction action = actionBuilder.buildFireWeaponWithHitAction(2, -2, Statistic.DEFENSE_VALUE, false, -3);
                    return Collections.singletonList(action);
                }
                return null;
            }
        };
        permanentWeapon.addKeyword(Keyword.BLASTER);
        return permanentWeapon;
    }
}
