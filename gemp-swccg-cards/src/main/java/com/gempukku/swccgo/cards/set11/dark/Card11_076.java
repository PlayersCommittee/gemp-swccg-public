package com.gempukku.swccgo.cards.set11.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AttemptToBlowAwayDeathStarIITotalModifier;
import com.gempukku.swccgo.logic.modifiers.CalculationVariableModifier;
import com.gempukku.swccgo.logic.modifiers.ManeuverModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Tatooine
 * Type: Effect
 * Title: We Shall Double Our Efforts!
 */
public class Card11_076 extends AbstractNormalEffect {
    public Card11_076() {
        super(Side.DARK, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "We Shall Double Our Efforts!", Uniqueness.UNIQUE);
        setLore("'I hope so, Commander, for your sake...'");
        setGameText("Deploy on table. Adds 1 to X on That Thing's Operational. At Death Star II sectors, opponent's starfighters are maneuver -5. When opponent is attempting to 'blow away' Death Star II, subtract 2 from opponent's total. (Immune to Alter.)");
        addIcons(Icon.TATOOINE);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new CalculationVariableModifier(self, Filters.That_Things_Operational, 1, Variable.X));
        modifiers.add(new ManeuverModifier(self, Filters.and(Filters.opponents(self), Filters.starfighter, Filters.at(Filters.Death_Star_II_sector)), -5));
        modifiers.add(new AttemptToBlowAwayDeathStarIITotalModifier(self, -2));
        return modifiers;
    }
}