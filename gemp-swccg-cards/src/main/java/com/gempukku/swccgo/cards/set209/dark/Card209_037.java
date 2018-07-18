package com.gempukku.swccgo.cards.set209.dark;

import com.gempukku.swccgo.cards.AbstractFirstOrder;
import com.gempukku.swccgo.cards.AbstractPermanentWeapon;
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
 •Kylo Ren With Lightsaber [TBD (V)] 1
 [TBD – TBD]
 Lore: Leader.
 CHARACTER – FIRST ORDER
 POWER 6 ABILITY 5 FORCE-SENSITIVE
 Text: [Pilot]2. Permanent weapon is •Kylo’s Lightsaber (may target a character for free; draw two destiny; target hit, its forfeit = 0, and opponent loses 1 Force, if total destiny > defense value; otherwise you lose 1 Force).
 DEPLOY 5 FORFEIT 7
 [Pilot] [Warrior] [Permanent Weapon] [Episode VII] [Set 9]
*/

public class Card209_037 extends AbstractFirstOrder {
    public Card209_037() {
        super(Side.DARK, 1, 5, 6, 5, 7, "Kylo Ren With Lightsaber", Uniqueness.UNIQUE);
        setLore("Leader");
        addKeywords(Keyword.LEADER);
        setGameText("[Pilot]2. Permanent weapon is •Kylo’s Lightsaber (may target a character for free; draw two destiny; target hit, its forfeit = 0, and opponent loses 1 Force, if total destiny > defense value; otherwise you lose 1 Force).");
        addPersona(Persona.KYLO);
        addPersona(Persona.KYLOS_LIGHTSABER);
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
        final AbstractPermanentWeapon permanentWeapon = new AbstractPermanentWeapon("Kylo's Lightsaber") {
            //@Override
            public List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, Boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit)
            {
                FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, this, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                        .targetForFree(Filters.character, TargetingReason.TO_BE_HIT).finishBuildPrep();
                if (actionBuilder != null) {
                    FireWeaponAction action = actionBuilder.buildFireWeaponWithHitOrMissAction(2, Statistic.DEFENSE_VALUE, 1, 1);
                    return Collections.singletonList(action);
                }
                return null;
            }
        };
        permanentWeapon.addKeyword(Keyword.LIGHTSABER);
        return permanentWeapon;
    }

}
