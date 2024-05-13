package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.conditions.AboardCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.LandspeedModifier;
import com.gempukku.swccgo.logic.modifiers.ManeuverModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Premiere
 * Type: Character
 * Subtype: Droid
 * Title: R4-M9 (Arfour-Emmnine)
 */
public class Card1_193 extends AbstractDroid {
    public Card1_193() {
        super(Side.DARK, 4, 1, 1, 3, "R4-M9 (Arfour-Emmnine)", Uniqueness.UNRESTRICTED, ExpansionSet.PREMIERE, Rarity.C2);
        setLore("Typical multiple-use droid. Controls and repairs vehicles and computers. R4-M9 was once used by the Empire to pull data from Tantive IV's computer banks.");
        setGameText("While aboard a non-creature vehicle, adds 1 to power, maneuver and landspeed. May drive transport vehicles.");
        addModelType(ModelType.VEHICLE);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition aboardNonCreatureVehicle = new AboardCondition(self, Filters.non_creature_vehicle);
        Filter nonCreatureVehicleAboard = Filters.and(Filters.non_creature_vehicle, Filters.hasAboard(self));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, nonCreatureVehicleAboard, aboardNonCreatureVehicle, 1));
        modifiers.add(new ManeuverModifier(self, nonCreatureVehicleAboard, aboardNonCreatureVehicle, 1));
        modifiers.add(new LandspeedModifier(self, nonCreatureVehicleAboard, aboardNonCreatureVehicle, 1));
        return modifiers;
    }
}
