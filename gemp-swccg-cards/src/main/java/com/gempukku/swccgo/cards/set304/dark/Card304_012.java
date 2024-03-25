package com.gempukku.swccgo.cards.set304.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.PresentCondition;
import com.gempukku.swccgo.cards.evaluators.ForceIconsAtLocationEvaluator;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.cards.AbstractPermanentWeapon;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
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
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.CancelDestinyEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.cards.evaluators.AtSameSiteEvaluator;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: The Great Hutt Expansion
 * Type: Character
 * Subtype: Imperial
 * Title: Rohan Lap'lamiz With Lightsaber
 */

public class Card304_012 extends AbstractImperial {
    public Card304_012() {
        super(Side.DARK, 3, 4, 3, 3, 5, "Rohan Lap'lamiz With Lightsaber", Uniqueness.UNIQUE, ExpansionSet.GREAT_HUTT_EXPANSION, Rarity.V);
        setLore("The second son of Kamjin Lap'lamiz, Rohan was raised by his mother. While trained as a Jedi he's recently joined Scholae Palatinae as part of the Imperial Mission. ");
        setGameText("Power is +1 for each [light side icon] present at the same site; unless Kamjin is present. [Pilot] 2. Permanent weapon is nRohan's Lightsaber (may target a character or creature for free; draw two destiny; target hit, and its forfeit = 0, if total destiny > defense value).");
        addIcons(Icon.CSP, Icon.PILOT, Icon.WARRIOR, Icon.PERMANENT_WEAPON);
		addPersona(Persona.ROHAN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {  
		Evaluator lightSideIconsAtLocation = new ForceIconsAtLocationEvaluator(self, false, true);
	
		List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new PowerModifier(self, Filters.Rohan, new AndCondition(new AtCondition(self, Filters.site),
                new UnlessCondition(new PresentCondition(self, Filters.Kamjin))), lightSideIconsAtLocation));
        return modifiers;
    }

    // Define "Rohan's Lightsaber" permanent weapon
    @Override
    protected AbstractPermanentWeapon getGameTextPermanentWeapon() {
        AbstractPermanentWeapon permanentWeapon = new AbstractPermanentWeapon(Persona.ROHANS_LIGHTSABER) {
            @Override
            public List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
                FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, this, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                        .targetForFree(Filters.or(Filters.character, targetedAsCharacter, Filters.creature), TargetingReason.TO_BE_HIT).finishBuildPrep();
                if (actionBuilder != null) {

                    // Build action using common utility
                    FireWeaponAction action = actionBuilder.buildFireWeaponWithHitAction(2, Statistic.DEFENSE_VALUE, true, 0);
                    return Collections.singletonList(action);
                }
                return null;
            }
        };
        permanentWeapon.addKeyword(Keyword.LIGHTSABER);
        return permanentWeapon;
    }
}


