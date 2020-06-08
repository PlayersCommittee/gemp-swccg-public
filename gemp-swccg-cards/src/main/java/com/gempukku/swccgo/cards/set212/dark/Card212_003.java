package com.gempukku.swccgo.cards.set212.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.AbstractPermanentWeapon;
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
 * Set: Set 12
 * Type: Character
 * Subtype: Alien
 * Title: Aurra Sing with Blaster Rifle
 */
public class Card212_003 extends AbstractAlien {
    public Card212_003() {
        super(Side.DARK, 2, 4, 4, 4, 5, "Aurra Sing With Blaster Rifle", Uniqueness.UNIQUE);
        setLore("Bounty hunter. Assassin.");
        setGameText("Permanent weapon is Aurra Sing's Blaster Rifle. May target a character for free, draw destiny(2 if targeting a jedi), target hit and forfeit=0, if destiny +1 > defense value.");
        addIcons(Icon.EPISODE_I, Icon.WARRIOR, Icon.VIRTUAL_SET_12, Icon.PERMANENT_WEAPON);
        addKeywords(Keyword.BOUNTY_HUNTER, Keyword.ASSASSIN, Keyword.FEMALE);
        addPersona(Persona.AURRA);
    }

    // Define "Aurra Sing's Blaster Rifle" permanent weapon
    @Override
    protected AbstractPermanentWeapon getGameTextPermanentWeapon() {
        AbstractPermanentWeapon permanentWeapon = new AbstractPermanentWeapon(Persona.AURRAS_BLASTER_RIFLE) {
            @Override
            public List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
                FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, this, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                        .targetForFree(Filters.or(Filters.character, targetedAsCharacter), TargetingReason.TO_BE_HIT).finishBuildPrep();
                if (actionBuilder != null) {

                    // Build action using common utility
                    FireWeaponAction action = actionBuilder.buildFireWeaponPermanentAurraSingsBlasterRifleAction();
                    return Collections.singletonList(action);
                }

                return null;
            }
        };
        permanentWeapon.addKeyword(Keyword.BLASTER_RIFLE);
        return permanentWeapon;
    }
}

