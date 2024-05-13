package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
import com.gempukku.swccgo.cards.conditions.LeadStarfighterInAttackRunCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AttackRunTotalModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: A New Hope
 * Type: Starship
 * Subtype: Starfighter
 * Title: Red 5
 */
public class Card2_071 extends AbstractStarfighter {
    public Card2_071() {
        super(Side.LIGHT, 2, 3, 3, null, 4, 5, 6, "Red 5", Uniqueness.UNIQUE, ExpansionSet.A_NEW_HOPE, Rarity.R1);
        setLore("Luke's Incom T-65 X-wing at the Battle of Yavin. Instrumentation similarities between Red 5 and the T-16 skyhopper allowed Luke to play a pivotal role in the conflict.");
        setGameText("May add 1 pilot and 1 astromech. Immune to attrition < 4 if Luke piloting. Must have pilot aboard to use power, maneuver or hyperspeed. When firing in an Attack Run, adds 1 to total.");
        addPersona(Persona.RED_5);
        addIcons(Icon.A_NEW_HOPE, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addModelType(ModelType.X_WING);
        setPilotCapacity(1);
        setAstromechCapacity(1);
        addKeywords(Keyword.RED_SQUADRON);
        setMatchingPilotFilter(Filters.Luke);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new HasPilotingCondition(self, Filters.Luke), 4));
        modifiers.add(new AttackRunTotalModifier(self, new LeadStarfighterInAttackRunCondition(self), 1));
        return modifiers;
    }
}
