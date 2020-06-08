package com.gempukku.swccgo.cards.set9.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.conditions.AloneCondition;
import com.gempukku.swccgo.cards.conditions.ArmedWithCondition;
import com.gempukku.swccgo.cards.conditions.CapturedOnlyCondition;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Death Star II
 * Type: Character
 * Subtype: Rebel
 * Title: Luke Skywalker, Jedi Knight
 */
public class Card9_024 extends AbstractRebel {
    public Card9_024() {
        super(Side.LIGHT, 6, 8, 6, 6, 9, "Luke Skywalker, Jedi Knight", Uniqueness.UNIQUE);
        setLore("Scout trained in the ways of the Force. Key figure for both the Alliance and the Empire. Desired by Vader as an ally, by Palpatine as a servant and by the Alliance as its savior.");
        setGameText("Deploys -3 to Home One or Endor. Adds 2 to power of anything he pilots. Power +2 when armed with a lightsaber. Subtracts 3 from any attempt to cross him over (even if captured). Immune to attrition < 5 (< 6 if alone or armed with a lightsaber, < 7 if both).");
        addPersona(Persona.LUKE);
        addIcons(Icon.DEATH_STAR_II, Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.SCOUT);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToTargetModifier(self, -3, Filters.or(Filters.Deploys_aboard_Home_One, Filters.Deploys_at_Endor)));
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
