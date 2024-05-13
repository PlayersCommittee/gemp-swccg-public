package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.cards.AbstractCharacterWeapon;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.PlayCardOptionId;
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
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextDeployCostToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Dagobah
 * Type: Weapon
 * Subtype: Character
 * Title: Zuckuss' Snare Rifle
 */
public class Card4_180 extends AbstractCharacterWeapon {
    public Card4_180() {
        super(Side.DARK, 1, "Zuckuss' Snare Rifle", Uniqueness.UNIQUE, ExpansionSet.DAGOBAH, Rarity.R);
        setLore("Merr-Sonn Munitions, Inc. GRS-1 Snare Rifle. Shoots liquefied shockstun mist up to 150 meters. Liquid spraynet Hardens into a translucent web, confining the stunned target.");
        setGameText("Deploy on Zuckuss, or use 1 Force to deploy on any other bounty hunter. May target a character or creature using 2 Force. Draw destiny. Character captured if destiny -1 > defense value. Creature lost if destiny +1 > defense value.");
        addPersona(Persona.ZUCKUSS_SNARE_RIFLE);
        addIcons(Icon.DAGOBAH);
        addKeywords(Keyword.RIFLE);
        setMatchingCharacterFilter(Filters.Zuckuss);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextDeployCostToTargetModifier(self, 1, Filters.not(Filters.Zuckuss)));
        return modifiers;
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.or(Filters.Zuckuss, Filters.bounty_hunter));
    }

    @Override
    protected Filter getGameTextValidToUseWeaponFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.or(Filters.Zuckuss, Filters.bounty_hunter);
    }

    @Override
    protected List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, final SwccgGame game, final PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                .targetUsingForce(Filters.or(Filters.character, targetedAsCharacter), 2, TargetingReason.TO_BE_CAPTURED)
                .targetUsingForce(Filters.creature, 2, TargetingReason.TO_BE_LOST).finishBuildPrep();
        if (actionBuilder != null) {

            // Build action using common utility
            FireWeaponAction action = actionBuilder.buildFireWeaponZuckussSnareRifleAction();
            return Collections.singletonList(action);
        }
        return null;
    }
}
