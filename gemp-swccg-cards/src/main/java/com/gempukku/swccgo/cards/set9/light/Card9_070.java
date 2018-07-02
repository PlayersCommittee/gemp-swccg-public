package com.gempukku.swccgo.cards.set9.light;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.modifiers.AddsDestinyToPowerModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Death Star II
 * Type: Starship
 * Subtype: Starfighter
 * Title: Gray Squadron 2
 */
public class Card9_070 extends AbstractStarfighter {
    public Card9_070() {
        super(Side.LIGHT, 3, 1, 2, null, 3, 4, 2, "Gray Squadron 2", Uniqueness.UNIQUE);
        setLore("Flown by Lieutenant Telsij as Colonel Salm's wingman. Part of gray squadron at the battle of Endor.");
        setGameText("May add 2 pilots or passengers. Immune to attrition < 3 when Lieutenant Telsij or Karie Neth piloting (when both immune to attrition < 5 and adds one destiny to power only).");
        addIcons(Icon.DEATH_STAR_II, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addModelType(ModelType.Y_WING);
        addKeywords(Keyword.GRAY_SQUADRON);
        setPilotOrPassengerCapacity(2);
        setMatchingPilotFilter(Filters.or(Filters.Telsij, Filters.Karie_Neth));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition telsijPiloting = new HasPilotingCondition(self, Filters.Telsij);
        Condition karieNethPiloting = new HasPilotingCondition(self, Filters.Karie_Neth);
        Condition bothPiloting = new AndCondition(telsijPiloting, karieNethPiloting);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new OrCondition(telsijPiloting, karieNethPiloting),
                new ConditionEvaluator(3, 5, bothPiloting)));
        modifiers.add(new AddsDestinyToPowerModifier(self, bothPiloting, 1));
        return modifiers;
    }
}
