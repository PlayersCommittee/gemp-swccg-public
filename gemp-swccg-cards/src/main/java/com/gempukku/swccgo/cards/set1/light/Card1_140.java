package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractCapitalStarship;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.common.*;

import java.util.Collections;
import java.util.List;


/**
 * Set: Premiere
 * Type: Starship
 * Subtype: Capital
 * Title: Corellian Corvette
 */
public class Card1_140 extends AbstractCapitalStarship {
    public Card1_140() {
        super(Side.LIGHT, 1, 4, 5, 4, null, 3, 8, Title.Corellian_Corvette);
        setLore("Multi-purpose Rebel Blockade Runner. Modular interior designed for troop or cargo transport. 150 meters long. Used by Rebels, pirates, corporations and the Empire.");
        setGameText("May add 3 pilots, 4 passengers and 1 vehicle. Has ship-docking capability. Permanent pilot aboard provides ability of 1.");
        addIcons(Icon.PILOT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addModelType(ModelType.CORELLIAN_CORVETTE);
        setPilotCapacity(3);
        setPassengerCapacity(4);
        setVehicleCapacity(1);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(1) {});
    }
}
