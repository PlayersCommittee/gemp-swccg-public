package com.gempukku.swccgo.cards.set219.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.AbstractPermanentWeapon;
import com.gempukku.swccgo.cards.conditions.BlownAwayCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.FireWeaponAction;
import com.gempukku.swccgo.logic.actions.FireWeaponActionBuilder;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 19
 * Type: Character
 * Subtype: Alien
 * Title: Cara Dune With Heavy Blaster Rifle
 */
public class Card219_030 extends AbstractAlien {
    public Card219_030() {
        super(Side.LIGHT, 2, 4, 4, 2, 5, "Cara Dune With Heavy Blaster Rifle", Uniqueness.UNIQUE);
        setArmor(5);
        setLore("Female Alderaanian. Gambler. Trooper.");
        setGameText(" If Alderaan has been 'blown away,' Force drain +1 here. " +
                "Permanent weapon is •Cara's Heavy Blaster Rifle (twice per battle, may target a character or vehicle; " +
                "draw destiny; target hit, and its forfeit is cumulatively -3, if destiny +1 > defense value).");
        addKeywords(Keyword.FEMALE, Keyword.GAMBLER, Keyword.TROOPER);
        setSpecies(Species.ALDERAANIAN);
        addIcons(Icon.WARRIOR, Icon.PERMANENT_WEAPON, Icon.VIRTUAL_SET_19);
    }

    @Override
    protected AbstractPermanentWeapon getGameTextPermanentWeapon() {
        AbstractPermanentWeapon permanentWeapon = new AbstractPermanentWeapon("Cara's Heavy Blaster Rifle") {
            @Override
            public List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
                FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, this, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                        .twicePerBattle().targetForFree(Filters.or(Filters.character, targetedAsCharacter, Filters.vehicle), TargetingReason.TO_BE_HIT).finishBuildPrep();
                if (actionBuilder != null) {

                    // Build action using common utility
                    FireWeaponAction action = actionBuilder.buildFireWeaponWithHitAction(1, 1, Statistic.DEFENSE_VALUE, false, -3);
                    return Collections.singletonList(action);
                }
                return null;
            }
        };
        permanentWeapon.addKeyword(Keyword.BLASTER_RIFLE);
        return permanentWeapon;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();

        modifiers.add(new ForceDrainModifier(self, Filters.here(self), new BlownAwayCondition(Filters.and(CardSubtype.SYSTEM, Filters.title(Title.Alderaan, true))), 1, self.getOwner()));
        return modifiers;
    }
}
