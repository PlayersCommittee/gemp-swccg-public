package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractTransportVehicle;
import com.gempukku.swccgo.cards.conditions.HasAboardCondition;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.MayMoveAsReactModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MovesForFreeModifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Premiere
 * Type: Vehicle
 * Subtype: Transport
 * Title: SoroSuub V-35 Landspeeder
 */
public class Card1_151 extends AbstractTransportVehicle {
    public Card1_151() {
        super(Side.LIGHT, 4, 2, 0, null, 4, 3, 3, "SoroSuub V-35 Landspeeder");
        setLore("Typical old model Courier landspeeder such as the one used by Lars family for trips to Anchorhead. Has cargo compartment. Enclosed seating.");
        setGameText("May add 1 driver and 3 passengers. Moves free if Owen Lars, Beru Lars or Luke aboard. May move as a 'react'.");
        addKeywords(Keyword.ENCLOSED);
        setDriverCapacity(1);
        setPassengerCapacity(3);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MovesForFreeModifier(self, new HasAboardCondition(self, Filters.or(Filters.Owen, Filters.Beru, Filters.Luke))));
        modifiers.add(new MayMoveAsReactModifier(self));
        return modifiers;
    }
}
