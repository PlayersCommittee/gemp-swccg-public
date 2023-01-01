package com.gempukku.swccgo.cards.set210.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.cards.evaluators.CardMatchesEvaluator;
import com.gempukku.swccgo.cards.evaluators.InBattleEvaluator;
import com.gempukku.swccgo.cards.evaluators.InBattleEvaluatorNegativeOutput;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.AttritionModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 10
 * Type: Character
 * Subtype: Rebel
 * Title: Dutch (v)
 */


public class Card210_013 extends AbstractRebel {
    public Card210_013() {
        super(Side.LIGHT, 1, 2, 2, 2, 4, Title.Dutch, Uniqueness.UNIQUE, ExpansionSet.SET_10, Rarity.V);
        setGameText("[Pilot] 2, 3: Gold 1. While piloting a Y-wing, for each Y-wing you have in a battle, attrition against opponent is +1 and attrition against you is -1. Y-wing pilots are forfeit +1 here.");
        addPersona(Persona.DUTCH);
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_10);
        addKeywords(Keyword.GOLD_SQUADRON, Keyword.LEADER);
        setMatchingStarshipFilter(Filters.Gold_1);
        setVirtualSuffix(true);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        final String LSPlayer = self.getOwner();
        final String DSPlayer = game.getOpponent(LSPlayer);

        Condition pilotingYwing = new PilotingCondition(self, Filters.Y_wing);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, new CardMatchesEvaluator(2, 3, Filters.Gold_1)));
        modifiers.add(new ForfeitModifier(self, Filters.and(Filters.atSameLocation(self), Filters.piloting(Filters.Y_wing)), 1));


        modifiers.add(new AttritionModifier(self, pilotingYwing, (new InBattleEvaluator(self, Filters.and(Filters.your(LSPlayer), Filters.Y_wing))), DSPlayer));
        modifiers.add(new AttritionModifier(self, pilotingYwing, (new InBattleEvaluatorNegativeOutput(self, Filters.and(Filters.your(LSPlayer), Filters.Y_wing))), LSPlayer));


        return modifiers;
    }
}
