package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Character
 * Subtype: Droid
 * Title: R3-T2 (Arthree-Teetoo)
 */
public class Card7_037 extends AbstractDroid {
    public Card7_037() {
        super(Side.LIGHT, 4, 2, 1, 3, "R3-T2 (Arthree-Teetoo)", Uniqueness.UNIQUE);
        setLore("Expanded memory capacity. Escaped from pirates based in Mos Eisley. Programmed with the ability to both enhance and sabotage hyperdrive systems.");
        setGameText("While aboard a capital starship, adds 1 to power and 2 to hyperspeed, and that starship is immune to attrition < 4. While at Central Core, Death Star requires +2 to move.");
        addIcons(Icon.SPECIAL_EDITION, Icon.NAV_COMPUTER);
        addModelType(ModelType.ASTROMECH);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, Filters.and(Filters.capital_starship, Filters.hasAboard(self)), 1));
        modifiers.add(new HyperspeedModifier(self, Filters.and(Filters.capital_starship, Filters.hasAboard(self)), 2));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, Filters.and(Filters.capital_starship, Filters.hasAboard(self)), 4));
        modifiers.add(new MoveCostModifier(self, Filters.Death_Star_system, new AtCondition(self, Filters.Death_Star_Central_Core), 2));
        return modifiers;
    }
}
