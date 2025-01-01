package com.gempukku.swccgo.cards.set304.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.conditions.AloneCondition;
import com.gempukku.swccgo.cards.conditions.ArmedWithCondition;
import com.gempukku.swccgo.cards.conditions.CapturedOnlyCondition;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.CrossOverAttemptTotalModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: The Great Hutt Expansion
 * Type: Character
 * Subtype: Rebel
 * Title: Kai Lap'lamiz, Jedi Knight
 */
public class Card304_145 extends AbstractRebel {
    public Card304_145() {
        super(Side.LIGHT, 6, 7, 5, 6, 8, "Kai Lap'lamiz, Jedi Knight", Uniqueness.UNIQUE, ExpansionSet.GREAT_HUTT_EXPANSION, Rarity.V);
        setLore("After his father's assassination attempt Kai has emerged as a fully trained Jedi Knight. Like other Bokken Jedi he helps all he encounters.");
        setGameText("Deploys -3 to Ulress or Koudooine. Adds 2 to power of anything he pilots. Power +2 when armed with a lightsaber. Subtracts 3 from any attempt to cross him over (even if captured). Immune to attrition < 5 (< 6 if alone or armed with a lightsaber, < 7 if both).");
        addPersona(Persona.KAI);
        addIcons(Icon.PILOT, Icon.WARRIOR);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToTargetModifier(self, -3, Filters.or(Filters.Deploys_at_Koudooine, Filters.Deploys_at_Ulress)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition alone = new AloneCondition(self);
        Condition armedWithLightsaber = new ArmedWithCondition(self, Filters.lightsaber);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new PowerModifier(self, armedWithLightsaber, 2));
        modifiers.add(new CrossOverAttemptTotalModifier(self, -3));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new ConditionEvaluator(5, new ConditionEvaluator(6, 7,
                new AndCondition(alone, armedWithLightsaber)), new OrCondition(alone, armedWithLightsaber))));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileInactiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        Condition capturedOnly = new CapturedOnlyCondition(self);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new CrossOverAttemptTotalModifier(self, capturedOnly, -3));
        return modifiers;
    }
}
