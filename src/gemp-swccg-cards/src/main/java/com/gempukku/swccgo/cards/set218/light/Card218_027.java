package com.gempukku.swccgo.cards.set218.light;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
import com.gempukku.swccgo.cards.evaluators.MaxLimitEvaluator;
import com.gempukku.swccgo.cards.evaluators.StackedEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.ImmunityToAttritionChangeModifier;
import com.gempukku.swccgo.logic.modifiers.ManeuverModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 18
 * Type: Starship
 * Subtype: Starfighter
 * Title: Outrider (V)
 */
public class Card218_027 extends AbstractStarfighter {
    public Card218_027() {
        super(Side.LIGHT, 2, 2, 3, null, 3, 5, 5, Title.Outrider, Uniqueness.UNIQUE, ExpansionSet.SET_18, Rarity.V);
        setVirtualSuffix(true);
        setLore("Highly modified Corellian Engineering Corporation YT-2400. KonGar KGDefender military grade ion engines. Griffyn/Y2TG hyperdrive. Never boarded by Imperial customs.");
        setGameText("May add 2 pilots and 1 passenger. Power, maneuver, and immunity to attrition +1 for each card stacked on A Useless Gesture (limit +3). While Dash or Leebo piloting, immune to attrition < 4.");
        addIcons(Icon.REFLECTIONS_II, Icon.INDEPENDENT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_18);
        addModelType(ModelType.MODIFIED_LIGHT_FREIGHTER);
        setPilotCapacity(2);
        setPassengerCapacity(1);
        setMatchingPilotFilter(Filters.or(Filters.Dash, Filters.Leebo));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new HasPilotingCondition(self, Filters.or(Filters.Dash, Filters.Leebo)), 4));
        modifiers.add(new PowerModifier(self, new MaxLimitEvaluator(new StackedEvaluator(self, Filters.A_Useless_Gesture), 3)));
        modifiers.add(new ManeuverModifier(self, new MaxLimitEvaluator(new StackedEvaluator(self, Filters.A_Useless_Gesture), 3)));
        modifiers.add(new ImmunityToAttritionChangeModifier(self, self, new MaxLimitEvaluator(new StackedEvaluator(self, Filters.A_Useless_Gesture), 3)));
        return modifiers;
    }
}
