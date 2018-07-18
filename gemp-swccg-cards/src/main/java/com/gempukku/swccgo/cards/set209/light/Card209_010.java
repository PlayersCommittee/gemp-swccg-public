package com.gempukku.swccgo.cards.set209.light;

import com.gempukku.swccgo.cards.AbstractPermanentWeapon;
import com.gempukku.swccgo.cards.AbstractResistance;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.FireWeaponAction;
import com.gempukku.swccgo.logic.actions.FireWeaponActionBuilder;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

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