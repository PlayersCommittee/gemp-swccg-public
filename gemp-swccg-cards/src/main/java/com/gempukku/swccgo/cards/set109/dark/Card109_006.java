package com.gempukku.swccgo.cards.set109.dark;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.AbstractPermanentWeapon;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
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
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Premium (Enhanced Cloud City)
 * Type: Character
 * Subtype: Droid
 * Title: 4-LOM With Concussion Rifle
 */
public class Card109_006 extends AbstractDroid {
    public Card109_006() {
        super(Side.DARK, 3, 3, 2, 3, "4-LOM With Concussion Rifle", Uniqueness.UNIQUE, ExpansionSet.ENHANCED_CLOUD_CITY, Rarity.PM);
        setArmor(3);
        setLore("Accomplished thief and information broker. Modified by Jabba to be an effective bounty hunter. The Hutt often teams 4-LOM with other hired killers.");
        setGameText("Adds one battle destiny if with Jabba or Zuckuss. Permanent weapon is •4-LOM's Concussion Rifle (may target a character for free; target may not use its game text for remainder of turn).");
        addPersona(Persona._4_LOM);
        addIcons(Icon.PREMIUM, Icon.WARRIOR, Icon.PERMANENT_WEAPON);
        addKeywords(Keyword.THIEF, Keyword.INFORMATION_BROKER, Keyword.BOUNTY_HUNTER);
        addModelType(ModelType.PROTOCOL);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsBattleDestinyModifier(self, new WithCondition(self, Filters.or(Filters.Jabba, Filters.Zuckuss)), 1));
        return modifiers;
    }

    // Define "4-LOM's Concussion Rifle" permanent weapon
    @Override
    protected AbstractPermanentWeapon getGameTextPermanentWeapon() {
        AbstractPermanentWeapon permanentWeapon = new AbstractPermanentWeapon(Title._4LOMs_Concussion_Rifle, Uniqueness.UNIQUE) {
            @Override
            public List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
                FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, this, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                        .targetForFree(Filters.or(Filters.character, targetedAsCharacter), TargetingReason.OTHER).noWeaponDestinyNeeded().finishBuildPrep();
                if (actionBuilder != null) {

                    // Build action using common utility
                    FireWeaponAction action = actionBuilder.buildFireWeaponCancelGameTextAction(true);
                    return Collections.singletonList(action);
                }
                return null;
            }
        };
        return permanentWeapon;
    }
}


