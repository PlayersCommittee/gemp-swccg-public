package com.gempukku.swccgo.cards.set3.dark;

import com.gempukku.swccgo.cards.AbstractCombatVehicle;
import com.gempukku.swccgo.cards.conditions.TargetingTheMainGeneratorCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TargetTheMainGeneratorTotalModifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Hoth
 * Type: Vehicle
 * Subtype: Combat
 * Title: Blizzard 1
 */
public class Card3_154 extends AbstractCombatVehicle {
    public Card3_154() {
        super(Side.DARK, 2, 6, 7, 7, null, 1, 7, Title.Blizzard_1, Uniqueness.UNIQUE);
        setLore("General Veers' AT-AT. Enclosed. Equipped with highly sophisticated communications gear. Employs an experimental targeting system.");
        setGameText("May add 2 pilots and 8 passengers. Immune to attrition < 4. When using AT-AT cannon to Target The Main Generator, adds 1 to total. Landspeed may not be increased.");
        addModelType(ModelType.AT_AT);
        addIcons(Icon.HOTH, Icon.SCOMP_LINK);
        addKeywords(Keyword.ENCLOSED);
        setPilotCapacity(2);
        addPersona(Persona.BLIZZARD_1);
        setPassengerCapacity(8);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 4));
        modifiers.add(new TargetTheMainGeneratorTotalModifier(self, new TargetingTheMainGeneratorCondition(self), 1));
        return modifiers;
    }
}
