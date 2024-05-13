package com.gempukku.swccgo.cards.set13.dark;

import com.gempukku.swccgo.cards.AbstractSith;
import com.gempukku.swccgo.cards.conditions.ArmedWithCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.EachBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.LightsaberCombatForceLossModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Reflections III
 * Type: Character
 * Subtype: Sith
 * Title: Lord Maul
 */
public class Card13_074 extends AbstractSith {
    public Card13_074() {
        super(Side.DARK, 1, 8, 7, 6, 8, "Lord Maul", Uniqueness.UNIQUE, ExpansionSet.REFLECTIONS_III, Rarity.PM);
        setLore("Sent by Darth Sidious to Naboo in order to assist the Trade Federation in their blockade there. His confrontation with Obi-Wan and Qui-Gon would impact history forever.");
        setGameText("Deploys -2 to Naboo. While with Qui-Gon, your battle destinies here are each +1. When Maul wins a lightsaber combat, adds 2 to opponent's Force loss. Immune to Disarmed, Clash Of Sabers, and attrition < 5 (or < 6 if armed with a lightsaber).");
        addPersona(Persona.MAUL);
        addIcons(Icon.REFLECTIONS_III, Icon.EPISODE_I, Icon.PILOT, Icon.WARRIOR);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, -2, Filters.Deploys_at_Naboo));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new EachBattleDestinyModifier(self, Filters.here(self), new WithCondition(self, Filters.QuiGon), 1, self.getOwner()));
        modifiers.add(new LightsaberCombatForceLossModifier(self, 2));
        modifiers.add(new ImmuneToTitleModifier(self, Title.Disarmed));
        modifiers.add(new ImmuneToTitleModifier(self, Title.Clash_Of_Sabers));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new ConditionEvaluator(5, 6, new ArmedWithCondition(self, Filters.lightsaber))));
        return modifiers;
    }
}
