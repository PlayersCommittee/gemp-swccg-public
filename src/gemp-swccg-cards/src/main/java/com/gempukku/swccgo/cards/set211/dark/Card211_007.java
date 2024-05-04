package com.gempukku.swccgo.cards.set211.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.AbstractPermanentWeapon;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.FireWeaponAction;
import com.gempukku.swccgo.logic.actions.FireWeaponActionBuilder;

import java.util.Collections;
import java.util.List;

/**
 * Set: Set 11
 * Type: Character
 * Subtype: Imperial
 * Title: Sergeant Narthax With E-web Blaster
 */
public class Card211_007 extends AbstractImperial {
    public Card211_007(){
        super(Side.DARK, 2, 3, 3, 2, 4, "Sergeant Narthax With E-web Blaster", Uniqueness.UNIQUE, ExpansionSet.SET_11, Rarity.V);
        setLore("Snowtrooper.");
        setGameText("Permanent weapon is E-web Blaster (may target a character, landed starfighter, or vehicle for free; if targeting a starfighter or vehicle, add one destiny to attrition; otherwise, subtract 3 from target's immunity to attrition (if any) until end of turn).");
        addIcons(Icon.VIRTUAL_SET_11, Icon.WARRIOR, Icon.PERMANENT_WEAPON);
        addPersona(Persona.NARTHAX);
        addKeywords(Keyword.SNOWTROOPER);
    }

    // Define "E-web Blaster" permanent weapon
    @Override
    protected AbstractPermanentWeapon getGameTextPermanentWeapon() {
        AbstractPermanentWeapon permanentWeapon = new AbstractPermanentWeapon("E-web Blaster") {
            @Override
            public List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
                FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, this, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                        .targetForFree(Filters.or(Filters.character, targetedAsCharacter, Filters.and(Filters.starfighter, Filters.landed), Filters.vehicle), TargetingReason.OTHER).finishBuildPrep();
                if (actionBuilder != null) {

                    // Build action using common utility
                    FireWeaponAction action = actionBuilder.buildFireWeaponNarthaxEWebBlasterAction();
                    return Collections.singletonList(action);
                }
                return null;
            }
        };
        permanentWeapon.addKeyword(Keyword.BLASTER);
        return permanentWeapon;
    }
}
