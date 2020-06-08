package com.gempukku.swccgo.cards.set110.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.AbstractPermanentWeapon;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
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
 * Set: Premium (Enhanced Jabba's Palace)
 * Type: Character
 * Subtype: Alien
 * Title: Bossk With Mortar Gun
 */
public class Card110_005 extends AbstractAlien {
    public Card110_005() {
        super(Side.DARK, 1, 5, 4, 2, 3, "Bossk With Mortar Gun", Uniqueness.UNIQUE);
        setLore("Trandoshan bounty hunter. Modified his mortar gun to fire stun cartridges for live captures. Uses non-fragmentary capture rounds to minimize collateral damage.");
        setGameText("Adds 2 to power of anything he pilots. Permanent weapon is •Bossk's Mortar Gun (may fire for free; draw destiny; may subtract or add 1 if at same site as a bounty; choose one character with that destiny number present to be captured.)");
        addPersona(Persona.BOSSK);
        addIcons(Icon.PREMIUM, Icon.PILOT, Icon.WARRIOR, Icon.PERMANENT_WEAPON);
        addKeywords(Keyword.BOUNTY_HUNTER);
        setSpecies(Species.TRANDOSHAN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        return modifiers;
    }

    // Define "Bossk's Mortar Gun" permanent weapon
    @Override
    protected AbstractPermanentWeapon getGameTextPermanentWeapon() {
        return new AbstractPermanentWeapon(Title.Bossks_Mortar_Gun, Uniqueness.UNIQUE) {
            @Override
            public List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
                FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, this, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                        .firesForFreeWithoutTargeting().finishBuildPrep();
                if (actionBuilder != null) {

                    // Build action using common utility
                    FireWeaponAction action = actionBuilder.buildFireWeaponBosskWithMortarGunAction();
                    return Collections.singletonList(action);
                }
                return null;
            }
        };
    }
}


