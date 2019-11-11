package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractTransportVehicle;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.MayMoveAsReactModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Premiere
 * Type: Vehicle
 * Subtype: Transport
 * Title: Ubrikkian 9000 Z001
 */
public class Card1_310 extends AbstractTransportVehicle {
    public Card1_310() {
        super(Side.DARK, 2, 2, 2, null, 6, 3, 4, "Ubrikkian 9000 Z001");
        setLore("Enclosed repulsorlift landspeeder. Micro-thrusters placed around spherical hull. Seats three. Extremely maneuverable. Top speed of 160 km per hour.");
        setGameText("May add 1 driver and 2 passengers. May move as a 'react.'");
        addKeywords(Keyword.ENCLOSED, Keyword.LANDSPEEDER);
        setDriverCapacity(1);
        setPassengerCapacity(2);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayMoveAsReactModifier(self));
        return modifiers;
    }
}
