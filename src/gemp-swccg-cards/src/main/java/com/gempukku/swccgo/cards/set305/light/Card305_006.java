package com.gempukku.swccgo.cards.set305.light;

import com.gempukku.swccgo.cards.AbstractCapitalStarship;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;

import java.util.Collections;
import java.util.List;


/**
 * Set: A Better Tomorrow
 * Type: Starship
 * Subtype: Capital
 * Title: Nebula-Class Star Destroyer
 */
public class Card305_006 extends AbstractCapitalStarship {
    public Card305_006() {
        super(Side.LIGHT, 1, 8, 8, 6, null, 3, 9, "Nebula-Class Star Destroyer", Uniqueness.UNRESTRICTED, ExpansionSet.ABT, Rarity.U);
        setLore("Also known as the Defender-class Star Destroyer, was the largest, most powerful warship design in the New Republic's New   Class Modernization Program.");
        setGameText("May add 6 pilots, 8 passengers, 2 vehicles and 4 starfighters. Has ship-docking capability. Permanent pilot aboard provides ability of 1.");
        addIcons(Icon.ABT, Icon.PILOT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addModelType(ModelType.NEBULA_CLASS_STAR_DESTROYER);
        setPilotCapacity(6);
        setPassengerCapacity(8);
        setVehicleCapacity(2);
        setStarfighterCapacity(4);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(1) {});
    }
}
