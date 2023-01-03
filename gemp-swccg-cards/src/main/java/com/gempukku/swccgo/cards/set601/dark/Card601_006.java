package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.AbstractPermanentWeapon;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.common.*;
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
 * Set: Block 7
 * Type: Character
 * Subtype: Alien
 * Title: Dengar With Blaster Carbine (V)
 */

public class Card601_006 extends AbstractAlien {
    public Card601_006() {
        super(Side.DARK, 2, 4, 3, 2, 4, "Dengar With Blaster Carbine", Uniqueness.UNIQUE, ExpansionSet.LEGACY, Rarity.V);
        setVirtualSuffix(true);
        setLore("Corellian bounty hunter. Skilled athlete. Expert shot. Has worked many times for Jabba the Hutt. Carries a long-standing grudge against Han Solo.");
        setGameText("While opponent's [Reflection II icon] objective on table, adds one battle destiny. Permanent weapon is •Dengar's Blaster Carbine (may target a character, creature or vehicle for free; draw destiny; target hit, and its forfeit = 0, if destiny +1 > defense value; may be fired twice per battle).");
        addPersona(Persona.DENGAR);
        addIcons(Icon.PREMIUM, Icon.PILOT, Icon.WARRIOR, Icon.PERMANENT_WEAPON, Icon.LEGACY_BLOCK_7);
        addKeywords(Keyword.BOUNTY_HUNTER);
        setSpecies(Species.CORELLIAN);
        setAsLegacy(true);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsBattleDestinyModifier(self, new OnTableCondition(self, Filters.and(Filters.opponents(self), Icon.REFLECTIONS_II, Filters.Objective)), 1));
        return modifiers;
    }

    // Define "Blaster Carbine" permanent weapon
    @Override
    protected AbstractPermanentWeapon getGameTextPermanentWeapon() {
        AbstractPermanentWeapon permanentWeapon = new AbstractPermanentWeapon(Title.Dengars_Blaster_Carbine, Uniqueness.UNIQUE) {
            @Override
            public List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
                FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, this, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                        .twicePerBattle().targetForFree(Filters.or(Filters.character, targetedAsCharacter, Filters.vehicle), TargetingReason.TO_BE_HIT).finishBuildPrep();
                if (actionBuilder != null) {

                    // Build action using common utility
                    FireWeaponAction action = actionBuilder.buildFireWeaponWithHitAction(1, 1, Statistic.DEFENSE_VALUE, true, 0);
                    return Collections.singletonList(action);
                }
                return null;
            }
        };
        permanentWeapon.addKeyword(Keyword.BLASTER);
        return permanentWeapon;
    }
}


