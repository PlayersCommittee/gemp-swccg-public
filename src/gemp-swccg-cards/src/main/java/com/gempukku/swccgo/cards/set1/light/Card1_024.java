package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.conditions.AboardCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Keyword;
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
import com.gempukku.swccgo.logic.modifiers.ManeuverModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Premiere
 * Type: Character
 * Subtype: Droid
 * Title: R2-X2 (Artoo-Extoo)
 */
public class Card1_024 extends AbstractDroid {
    public Card1_024() {
        super(Side.LIGHT, 4, 1, 1, 3, "R2-X2 (Artoo-Extoo)", Uniqueness.UNRESTRICTED, ExpansionSet.PREMIERE, Rarity.C2);
        setLore("Typical starfighter pilot assistant. Contains ten coordinates for hyperspace jumps. Built-in tools and computer interfaces. R2-X2 was assigned to Red 10 at Yavin Base.");
        setGameText("While aboard any starfighter, adds 1 to power, maneuver, and hyperspeed.");
        addModelType(ModelType.ASTROMECH);
        addKeywords(Keyword.RED_SQUADRON);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition aboardStarfighter = new AboardCondition(self, Filters.starfighter);
        Filter starfighterAboard = Filters.and(Filters.starfighter, Filters.hasAboard(self));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, starfighterAboard, aboardStarfighter, 1));
        modifiers.add(new ManeuverModifier(self, starfighterAboard, aboardStarfighter, 1));
        modifiers.add(new HyperspeedModifier(self, starfighterAboard, aboardStarfighter, 1));
        return modifiers;
    }
}
