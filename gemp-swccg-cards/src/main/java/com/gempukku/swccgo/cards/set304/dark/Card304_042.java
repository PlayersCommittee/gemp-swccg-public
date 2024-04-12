package com.gempukku.swccgo.cards.set304.dark;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.HasAboardCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: The Great Hutt Expansion
 * Type: Starship
 * Subtype: Starfighter
 * Title: Scholae Palatinae TIE Special Forces
 */

public class Card304_042 extends AbstractStarfighter {
    public Card304_042() {
        super(Side.DARK, 3, 2, 3, null, 4, 3, 4, "Scholae Palatinae TIE Special Forces", Uniqueness.RESTRICTED_3, ExpansionSet.GREAT_HUTT_EXPANSION, Rarity.V);
        setLore("Leveraging the industrial complex built by Thran, Scholae Palatinae has slowly begun to mass produce their own fighters. The ultimate goal to not depend upon Arx for equipment.");
        setGameText("May add 1 pilot and 1 passenger. Matching starfighter for any [CSP Icon] pilot of ability < 6. Power +1 at opponent's system. While matching pilot aboard, immune to attrition < 4.");
        addModelType(ModelType.TIE_SF);
        setPilotCapacity(1);
        setPassengerCapacity(1);
        setMatchingPilotFilter(Filters.and(Filters.CSP_pilot, Filters.abilityLessThan(6)));
        addIcons(Icon.CSP, Icon.NAV_COMPUTER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition CSPOnBoard = new HasAboardCondition(self, Filters.and(Filters.CSP_pilot, Filters.abilityLessThan(6)));
        //Condition CSPPiloting = new HasPilotingCondition(self, Filters.and(Filters.CSP_pilot, Filters.abilityLessThan(3)));
        Condition AtOpponentsSystem = new AtCondition(self, Filters.and(Filters.opponents(self), Filters.system));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, CSPOnBoard, 4));

        modifiers.add(new PowerModifier(self, AtOpponentsSystem, 1));

        return modifiers;
    }

}
