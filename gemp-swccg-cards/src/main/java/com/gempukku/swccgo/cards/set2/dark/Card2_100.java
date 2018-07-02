package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.conditions.AboardCondition;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: A New Hope
 * Type: Character
 * Subtype: Droid
 * Title: R3-T6 (Arthree-Teesix)
 */
public class Card2_100 extends AbstractDroid {
    public Card2_100() {
        super(Side.DARK, 2, 2, 1, 3, "R3-T6 (Arthree-Teesix)", Uniqueness.UNIQUE);
        setLore("R3 units have larger memory and more advanced circuitry than their R1 predecessors, allowing for more efficient astrogation plots. R3-T6 served on the Death Star.");
        setGameText("While aboard a capital starship, adds 1 to power and 2 to hyperspeed, and that starship is immune to attrition < 4. While at Death Star: Central Core, hyperspeed of Death Star system = 2.");
        addModelType(ModelType.ASTROMECH);
        addIcons(Icon.A_NEW_HOPE);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition aboardCapitalStarship = new AboardCondition(self, Filters.capital_starship);
        Filter capitalStarshipAboard = Filters.and(Filters.capital_starship, Filters.hasAboard(self));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, capitalStarshipAboard, aboardCapitalStarship, 1));
        modifiers.add(new HyperspeedModifier(self, capitalStarshipAboard, aboardCapitalStarship, 2));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, capitalStarshipAboard, aboardCapitalStarship, 4));
        modifiers.add(new ResetHyperspeedModifier(self, Filters.Death_Star_system, new AtCondition(self, Filters.Death_Star_Central_Core), 2));
        return modifiers;
    }
}
