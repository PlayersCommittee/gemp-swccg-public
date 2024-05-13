package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractTransportVehicle;
import com.gempukku.swccgo.cards.conditions.HasAboardCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
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
 * Title: Luke's X-34 Landspeeder
 */
public class Card1_149 extends AbstractTransportVehicle {
    public Card1_149() {
        super(Side.LIGHT, 3, 2, 1, null, 5, 4, 4, Title.Lukes_X34_Landspeeder, Uniqueness.UNIQUE, ExpansionSet.PREMIERE, Rarity.U2);
        setLore("Skywalker's SoroSuub. Common open (AI)r transport. Top speed 250 km per hour. Repulsorlift drive keeps it suspended one meter above ground, even when parked.");
        setGameText("May add 1 driver and 2 passengers. Moves free if Luke aboard. May move as a 'react.'");
        setDriverCapacity(1);
        setPassengerCapacity(2);
        addKeyword(Keyword.LANDSPEEDER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MovesForFreeModifier(self, new HasAboardCondition(self, Filters.Luke)));
        modifiers.add(new MayMoveAsReactModifier(self));
        return modifiers;
    }
}
