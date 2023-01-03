package com.gempukku.swccgo.cards.set112.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.AbstractPermanentWeapon;
import com.gempukku.swccgo.cards.conditions.AtCondition;
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
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Premium (Jabba's Palace Sealed Deck)
 * Type: Character
 * Subtype: Alien
 * Title: Lando With Vibro-Ax
 */

public class Card112_003 extends AbstractAlien {
    public Card112_003() {
        super(Side.LIGHT, 1, 6, 4, 3, 6, "Lando With Vibro-Ax", Uniqueness.UNIQUE, ExpansionSet.JPSD, Rarity.PM);
        setLore("Gambler. Spy in Jabba's Court. Has walked the palace's passageways and overheard many details of Jabba's operations on Tatooine.");
        setGameText("Deploys -2 on Tatooine. When at a Tatooine site, adds one battle destiny. Permanent weapon is vibro-ax (may target character for free; both players draw destiny; target excluded from battle if your destiny +4 > target's power + opponent's destiny).");
        addPersona(Persona.LANDO);
        addIcons(Icon.PREMIUM, Icon.PILOT, Icon.WARRIOR, Icon.PERMANENT_WEAPON);
        addKeywords(Keyword.GAMBLER, Keyword.SPY);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, -2, Filters.Deploys_on_Tatooine));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsBattleDestinyModifier(self, new AtCondition(self, Filters.Tatooine_site), 1));
        return modifiers;
    }

    // Define "Vibro-Ax" permanent weapon
    @Override
    protected AbstractPermanentWeapon getGameTextPermanentWeapon() {
        AbstractPermanentWeapon permanentWeapon = new AbstractPermanentWeapon("vibro-ax") {
            @Override
            public List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
                FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, this, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                        .targetForFree(Filters.and(Filters.or(Filters.character, targetedAsCharacter), Filters.hasPowerDefined), TargetingReason.TO_BE_EXCLUDED_FROM_BATTLE).finishBuildPrep();
                if (actionBuilder != null) {

                    // Build action using common utility
                    FireWeaponAction action = actionBuilder.buildFireWeaponVibroAxAction(1, 4, 1);
                    return Collections.singletonList(action);
                }
                return null;
            }
        };
        permanentWeapon.addKeyword(Keyword.VIBRO_AX);
        return permanentWeapon;
    }
}


