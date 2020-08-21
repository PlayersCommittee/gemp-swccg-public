package com.gempukku.swccgo.cards.set501.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.AboardCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.evaluators.HereEvaluator;
import com.gempukku.swccgo.cards.evaluators.MinEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 13
 * Type: Character
 * SubType: Alien
 * Title: Aurodia Ventafoli
 */
public class Card501_083 extends AbstractAlien {
    public Card501_083() {
        super(Side.DARK, 3, 4, 1, 2, 4, "Aurodia Ventafoli", Uniqueness.UNIQUE);
        setLore("Crimson Dawn. Female musician.");
        setGameText("Deploys -2 to First Light or same site as Vos. X = number of musicians here (maximum 3). While aboard First Light or with Vos, defense value +X and your Force drains at same site are +X. Immune to attrition < X.");
        setSpecies(Species.IMROOSIAN);
        addKeywords(Keyword.CRIMSON_DAWN, Keyword.FEMALE, Keyword.MUSICIAN);
        addIcon(Icon.VIRTUAL_SET_13);
        setTestingText("Aurodia Ventafoli");
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new DeployCostToLocationModifier(self, -2, Filters.sameSiteAs(self, Filters.Vos)));
        modifiers.add(new DeployCostAboardModifier(self, -2, Persona.FIRST_LIGHT));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        Evaluator valueOfX = new MinEvaluator(new ConstantEvaluator(3), new HereEvaluator(self, Filters.musician));
        Condition aboardFirstLightCondition = new AboardCondition(self, Filters.persona(Persona.FIRST_LIGHT));
        Condition withVosCondition = new WithCondition(self, Filters.Vos);
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new DefenseValueModifier(self, self, new OrCondition(aboardFirstLightCondition, withVosCondition), valueOfX));
        modifiers.add(new ForceDrainModifier(self, Filters.here(self), new OrCondition(aboardFirstLightCondition, withVosCondition), valueOfX, self.getOwner()));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, valueOfX));
        return modifiers;
    }
}
