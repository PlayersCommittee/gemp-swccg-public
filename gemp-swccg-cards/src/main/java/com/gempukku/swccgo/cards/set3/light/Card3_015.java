package com.gempukku.swccgo.cards.set3.light;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.conditions.AboardCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.HyperspeedModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Hoth
 * Type: Character
 * Subtype: Droid
 * Title: R5-M2 (Arfive-Emmtoo)
 */
public class Card3_015 extends AbstractDroid {
    public Card3_015() {
        super(Side.LIGHT, 4, 1, 1, 3, "R5-M2 (Arfive-Emmtoo)", Uniqueness.UNRESTRICTED, ExpansionSet.HOTH, Rarity.C2);
        setLore("Programmed to pilot sublight tactical courses. R5 units are in high demand for combat starships. R5-M2 helped plan evacuation routes from Echo Base. Owned by Shawn Valdez.");
        setGameText("When aboard a capital starship, adds 1 to power and hyperspeed and that starship is immune to attrition < 3.");
        addIcons(Icon.HOTH);
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
        return modifiers;
    }
}
