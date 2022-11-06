package com.gempukku.swccgo.cards.set219.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.conditions.PresentAtCondition;
import com.gempukku.swccgo.cards.evaluators.OnTableEvaluator;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.GreaterThanCondition;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 19
 * Type: Character
 * Subtype: Imperial
 * Title: Magistrate Morgan Elsbeth
 */
public class Card219_015 extends AbstractImperial {
    public Card219_015() {
        super(Side.DARK, 2, 4, 4, 4, 6, "Magistrate Morgan Elsbeth", Uniqueness.UNIQUE);
        setLore("Female leader.");
        setGameText("While present at a battleground site and you control more systems than opponent, Force drain +1 here. " +
                    "Power and defense value +2 while Thrawn at a system with a parsec number > 5. Immune to attrition < 3.");
        addIcons(Icon.WARRIOR, Icon.VIRTUAL_SET_19);
        addKeywords(Keyword.FEMALE, Keyword.LEADER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        final String playerId = self.getOwner();
        final String opponent = game.getOpponent(playerId);

        Evaluator numYourSystems = new OnTableEvaluator(self, Filters.and(Filters.system, Filters.controls(playerId)));
        Evaluator numOpponentsSystems =  new OnTableEvaluator(self, Filters.and(Filters.system, Filters.controls(opponent)));

        Condition controlMoreSystemsThanOpponentCondition = new GreaterThanCondition(numYourSystems, numOpponentsSystems);

        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ForceDrainModifier(self, Filters.here(self), new AndCondition(new PresentAtCondition(self, Filters.battleground_site), controlMoreSystemsThanOpponentCondition), 1, playerId));
        modifiers.add(new PowerModifier(self, new OnTableCondition(self, Filters.and(Filters.Thrawn, Filters.at(Filters.systemAboveParsec(5)))), 2));
        modifiers.add(new DefenseValueModifier(self, new OnTableCondition(self, Filters.and(Filters.Thrawn, Filters.at(Filters.systemAboveParsec(5)))), 2));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 3));
        return modifiers;
    }
}