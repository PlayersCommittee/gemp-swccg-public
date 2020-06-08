package com.gempukku.swccgo.cards.set203.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.AbstractPermanentWeapon;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.FireWeaponAction;
import com.gempukku.swccgo.logic.actions.FireWeaponActionBuilder;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.DeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 3
 * Type: Character
 * Subtype: Alien
 * Title: Ortugg (V)
 */
public class Card203_028 extends AbstractAlien {
    public Card203_028() {
        super(Side.DARK, 3, 3, 4, 2, 4, "Ortugg", Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("Gamorrean in charge of the Gamorreans at Jabba's palace. Posted to stand guard at the entrance cavern. Assigned by Jabba to keep an eye on Tessek.");
        setGameText("While at a Jabba's Palace site, your other Gamorreans are deploy -1 and forfeit +3. Permanent weapon is •Ortugg's Ax (may target a character or creature for free; draw destiny; target hit, and its forfeit = 0, if destiny +1 > defense value).");
        addIcons(Icon.JABBAS_PALACE, Icon.WARRIOR, Icon.PERMANENT_WEAPON, Icon.VIRTUAL_SET_3);
        setSpecies(Species.GAMORREAN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition atJabbasPalaceSite = new AtCondition(self, Filters.Jabbas_Palace_site);
        Filter yourOtherGamorreans = Filters.and(Filters.your(self), Filters.other(self), Filters.Gamorrean);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostModifier(self, yourOtherGamorreans, atJabbasPalaceSite, -1));
        modifiers.add(new ForfeitModifier(self, yourOtherGamorreans, atJabbasPalaceSite, 3));
        return modifiers;
    }

    // Define "Ortugg's Ax" permanent weapon
    @Override
    protected AbstractPermanentWeapon getGameTextPermanentWeapon() {
        AbstractPermanentWeapon permanentWeapon = new AbstractPermanentWeapon(Title.Ortuggs_Ax, Uniqueness.UNIQUE) {
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
        return permanentWeapon;
    }
}
