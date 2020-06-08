package com.gempukku.swccgo.cards.set108.light;

import com.gempukku.swccgo.cards.AbstractPermanentWeapon;
import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.FireWeaponAction;
import com.gempukku.swccgo.logic.actions.FireWeaponActionBuilder;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Premium (Enhanced Premiere)
 * Type: Character
 * Subtype: Rebel
 * Title: Han With Heavy Blaster Pistol
 */

public class Card108_001 extends AbstractRebel {
    public Card108_001() {
        super(Side.LIGHT, 1, 4, 4, 3, 6, "Han With Heavy Blaster Pistol", Uniqueness.UNIQUE);
        setLore("Scoundrel, smuggler, gambler and risk taker. 'Everything's going to be fine. Trust me.'");
        setGameText("Adds 3 to power of anything he pilots. Adds one battle destiny if with Luke or Chewie. Permanent weapon is •Han's Heavy Blaster Pistol (may target a character or creature for free; draw destiny; target hit, and its forfeit = 0, if destiny +1 > defense value).");
        addPersona(Persona.HAN);
        addIcons(Icon.PREMIUM, Icon.PILOT, Icon.WARRIOR, Icon.PERMANENT_WEAPON);
        addKeywords(Keyword.SMUGGLER, Keyword.GAMBLER);
        setSpecies(Species.CORELLIAN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new AddsBattleDestinyModifier(self, new WithCondition(self, Filters.or(Filters.Luke, Filters.Chewie)), 1));
        return modifiers;
    }

    // Define "Han's Heavy Blaster Pistol" permanent weapon
    @Override
    protected AbstractPermanentWeapon getGameTextPermanentWeapon() {
        AbstractPermanentWeapon permanentWeapon = new AbstractPermanentWeapon(Title.Hans_Heavy_Blaster_Pistol, Uniqueness.UNIQUE) {
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
        permanentWeapon.addKeyword(Keyword.BLASTER);
        return permanentWeapon;
    }
}


