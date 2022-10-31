package com.gempukku.swccgo.cards.set204.light;

import com.gempukku.swccgo.cards.AbstractAlienResistance;
import com.gempukku.swccgo.cards.AbstractPermanentWeapon;
import com.gempukku.swccgo.cards.conditions.HitCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Statistic;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.FireWeaponAction;
import com.gempukku.swccgo.logic.actions.FireWeaponActionBuilder;
import com.gempukku.swccgo.logic.modifiers.DeployCostToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 4
 * Type: Character
 * Subtype: Alien/Resistance
 * Title: Chewie With Bowcaster
 */
public class Card204_004 extends AbstractAlienResistance {
    public Card204_004() {
        super(Side.LIGHT, 1, 5, 6, 2, 6, "Chewie With Bowcaster", Uniqueness.UNIQUE, ExpansionSet.SET_4, Rarity.V);
        setLore("Wookiee smuggler.");
        setGameText("Deploys -1 to Falcon. While 'hit', draws one battle destiny if unable to otherwise. Permanent weapon is •Chewbacca's Bowcaster (may target a character or vehicle for free; draw two destiny; target hit, and its forfeit = 0, if total destiny > defense value).");
        addPersona(Persona.CHEWIE);
        addIcons(Icon.EPISODE_VII, Icon.PILOT, Icon.WARRIOR, Icon.PERMANENT_WEAPON, Icon.VIRTUAL_SET_4);
        addKeywords(Keyword.SMUGGLER);
        setSpecies(Species.WOOKIEE);
        setMatchingStarshipFilter(Filters.Falcon);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToTargetModifier(self, -1, Filters.Falcon));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, new HitCondition(self), 1));
        return modifiers;
    }

    // Define "Chewbacca's Bowcaster" permanent weapon
    @Override
    protected AbstractPermanentWeapon getGameTextPermanentWeapon() {
        AbstractPermanentWeapon permanentWeapon = new AbstractPermanentWeapon(Title.Chewbaccas_Bowcaster) {
            @Override
            public List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
                FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, this, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                        .targetForFree(Filters.or(Filters.character, targetedAsCharacter, Filters.vehicle), TargetingReason.TO_BE_HIT).finishBuildPrep();
                if (actionBuilder != null) {

                    // Build action using common utility
                    FireWeaponAction action = actionBuilder.buildFireWeaponWithHitAction(2, Statistic.DEFENSE_VALUE, true, 0);
                    return Collections.singletonList(action);
                }
                return null;
            }
        };
        permanentWeapon.addKeyword(Keyword.BOWCASTER);
        return permanentWeapon;
    }
}
