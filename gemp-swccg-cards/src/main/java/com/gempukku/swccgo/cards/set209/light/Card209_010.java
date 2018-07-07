package com.gempukku.swccgo.cards.set209.light ;

import com.gempukku.swccgo.cards.AbstractResistance;
import com.gempukku.swccgo.cards.AbstractPermanentWeapon;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.FireWeaponAction;
import com.gempukku.swccgo.logic.actions.FireWeaponActionBuilder;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.logic.modifiers.*;
import java.util.Collections;


import java.util.LinkedList;
import java.util.List;

/**
 •Rey With Lightsaber [TBD (V)] 1
 [TBD – TBD]
 Lore: Female.
 CHARACTER – Resistance
 POWER 5 ABILITY 5 FORCE-SENSITIVE
 Text: [Pilot]2. Permanent weapon is •Anakin’s Lightsaber (may target a character for free; draw two destiny; target hit, its forfeit = 0, and you may retrieve 1 Force, if total destiny > defense value).
 DEPLOY 5 FORFEIT 7
 [Pilot] [Warrior] [Permanent Weapon] [Episode VII] [Set 9]

 Breakdown:
 DONE: Side: Light
 DONE: Type: Character
 DONE: Subtype: Resistance
 DONE: Title: Rey With Lightsaber
 DONE: Uniqueness: Unique
 DONE: Lore: Female
 DONE: Destiny: 1
 DONE: Power: 5
 DONE: Ability: 5
 DONE: Deploy Cost: 5
 DONE: Forfeit: 7
 Icons:
 DONE: - Pilot
 DONE: - Warrior
 DONE: - Permanent Weapon
 DONE: - Episode VII (7)
 DONE: - Set 9
 DONE: Text, string: "[Pilot]2. Permanent weapon is •Anakin’s Lightsaber (may target a character for free; draw two destiny; target hit, its forfeit = 0, and you may retrieve 1 Force, if total destiny > defense value)."
 Text Functions:
 DONE: - Pilot 2
 DONE: - Permanent Weapon is Anakin's Lightsaber
 - may target a character for free; draw two destiny;
 - target hit, its forfeit = 0, and you retrieve 1 force, if total destiny > defense value;
 - otherwise you lose 1 Force
 */

public class Card209_010 extends AbstractResistance {
    public Card209_010() {
        super(Side.LIGHT, 1, 5, 5, 5, 7, "Rey With Lightsaber", Uniqueness.UNIQUE);
        setLore("Female");
        addKeywords(Keyword.FEMALE);
        setGameText("[Pilot]2. Permanent weapon is •Anakin’s Lightsaber (may target a character for free; draw two destiny; target hit, its forfeit = 0, and you may retrieve 1 Force, if total destiny > defense value).");
        addPersona(Persona.REY);
        addPersona(Persona.ANAKINS_LIGHTSABER);
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.PERMANENT_WEAPON, Icon.EPISODE_VII, Icon.VIRTUAL_SET_9);
    }

    // Add 2 to stuff he pilots.
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        return modifiers;
    }

    // Define "Kylo's Lightsaber permanent weapon
    @Override
    protected AbstractPermanentWeapon getGameTextPermanentWeapon() {
        final AbstractPermanentWeapon permanentWeapon = new AbstractPermanentWeapon("Anakin's Lightsaber") {
            //@Override
            public List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, Boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit)
            {
                FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, this, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                        .targetForFree(Filters.character, TargetingReason.TO_BE_HIT).finishBuildPrep();
                if (actionBuilder != null) {
                    FireWeaponAction action = actionBuilder.buildFireWeaponWithHitAndRetrieveAction(2, Statistic.DEFENSE_VALUE, 1);
                    return Collections.singletonList(action);
                }
                return null;
            }
        };
        permanentWeapon.addKeyword(Keyword.LIGHTSABER);
        return permanentWeapon;
    }

}