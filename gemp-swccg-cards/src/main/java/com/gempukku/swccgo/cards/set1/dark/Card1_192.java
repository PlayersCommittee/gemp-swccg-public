package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.conditions.AboardCondition;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premiere
 * Type: Character
 * Subtype: Droid
 * Title: R1-G4 (Arone-Geefour)
 */
public class Card1_192 extends AbstractDroid {
    public Card1_192() {
        super(Side.DARK, 3, 1, 1, 3, "R1-G4 (Arone-Geefour)");
        setLore("Typical of the old model astromechs still used on capital starships and large freighters. Has armored Mark II reactor drone shell. R1-G4 was abandoned after owner was captured.");
        setGameText("When aboard a capital starship, adds 1 to power and hyperspeed, and that starship is immune to attrition < 3. Too large to go aboard a starfighter.");
        addModelType(ModelType.ASTROMECH);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition aboardCapitalStarship = new AboardCondition(self, Filters.capital_starship);
        Filter capitalStarshipAboard = Filters.and(Filters.capital_starship, Filters.hasAboard(self));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, capitalStarshipAboard, aboardCapitalStarship, 1));
        modifiers.add(new HyperspeedModifier(self, capitalStarshipAboard, aboardCapitalStarship, 1));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, capitalStarshipAboard, aboardCapitalStarship, 3));
        modifiers.add(new MayNotExistAtTargetModifier(self, Filters.starfighter));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotDeployToTargetModifier(self, Filters.starfighter));
        return modifiers;
    }
}
