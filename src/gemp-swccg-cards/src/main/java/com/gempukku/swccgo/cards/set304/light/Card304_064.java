package com.gempukku.swccgo.cards.set304.light;

import com.gempukku.swccgo.cards.AbstractCombatVehicle;
import com.gempukku.swccgo.cards.conditions.PilotedCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.CharactersAboardMayJumpOffModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayMoveAsReactModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ResetPowerModifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: The Great Hutt Expansion
 * Type: Vehicle
 * Subtype: Combat
 * Title: Hutt Speeder Bike
 */
public class Card304_064 extends AbstractCombatVehicle {
    public Card304_064() {
        super(Side.LIGHT, 4, 1, 1, null, 5, 5, 3, "Hutt Speeder Bike", Uniqueness.UNRESTRICTED, ExpansionSet.GREAT_HUTT_EXPANSION, Rarity.V);
        setLore("Where there are Hutts there are biker gangs. Ixtal has put a lot of time into modifying these specific Hutt speeder bikes.");
        setGameText("May add 1 pilot and 1 passenger. May move as a 'react'. When piloted, vehicle and aliens aboard are immune to attrition < 4. Pilot's power = 0. If lost, characters aboard may 'jump off' (disembark).");
        addModelType(ModelType.SPEEDER_BIKE);
        setPilotCapacity(1);
        setPassengerCapacity(1);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayMoveAsReactModifier(self));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, Filters.or(self, Filters.and(Filters.alien, Filters.aboard(self))), new PilotedCondition(self), 4));
        modifiers.add(new ResetPowerModifier(self, Filters.piloting(self), 0));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiersEvenIfUnpiloted(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new CharactersAboardMayJumpOffModifier(self));
        return modifiers;
    }
}
