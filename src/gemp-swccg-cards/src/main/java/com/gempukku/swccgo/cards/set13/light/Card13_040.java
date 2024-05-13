package com.gempukku.swccgo.cards.set13.light;

import com.gempukku.swccgo.cards.AbstractCharacterWeapon;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Statistic;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.FireWeaponAction;
import com.gempukku.swccgo.logic.actions.FireWeaponActionBuilder;
import com.gempukku.swccgo.logic.modifiers.LightsaberCombatTotalModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Reflections III
 * Type: Weapon
 * Subtype: Character
 * Title: Qui-Gon's Lightsaber
 */
public class Card13_040 extends AbstractCharacterWeapon {
    public Card13_040() {
        super(Side.LIGHT, 5, "Qui-Gon's Lightsaber", Uniqueness.UNIQUE, ExpansionSet.REFLECTIONS_III, Rarity.PM);
        setLore("This lightsaber bore silent witness to the death of two great warriors in the same day. One a Jedi, one a Sith.");
        setGameText("Deploy on Qui-Gon or [Episode I] Obi-Wan. Adds 1 to this character's lightsaber combat total. May target a character for free. Draw two destiny. Target hit, and it forfeit = 0, if total destiny > defense value (if hit target is a Dark Jedi, opponent also loses 1 Force).");
        addPersona(Persona.QUIGON_JINNS_LIGHTSABER);
        addIcons(Icon.REFLECTIONS_III, Icon.EPISODE_I);
        addKeywords(Keyword.LIGHTSABER);
        setMatchingCharacterFilter(Filters.or(Filters.QuiGon, Filters.ObiWan));
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.or(Filters.QuiGon, Filters.and(Icon.EPISODE_I, Filters.ObiWan)));
    }

    @Override
    protected Filter getGameTextValidToUseWeaponFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.or(Filters.QuiGon, Filters.and(Icon.EPISODE_I, Filters.ObiWan));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new LightsaberCombatTotalModifier(self, Filters.hasAttached(self), 1));
        return modifiers;
    }

    @Override
    protected List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, final SwccgGame game, final PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                .targetForFree(Filters.or(Filters.character, targetedAsCharacter), TargetingReason.TO_BE_HIT).finishBuildPrep();
        if (actionBuilder != null) {

            // Build action using common utility
            FireWeaponAction action = actionBuilder.buildFireWeaponWithHitAction(2, Statistic.DEFENSE_VALUE, true, 0, Filters.Dark_Jedi, 1);
            return Collections.singletonList(action);
        }
        return null;
    }
}
