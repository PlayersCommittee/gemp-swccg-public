package com.gempukku.swccgo.cards.set1.dark;

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
 * Set: Premiere
 * Type: Starship
 * Subtype: Capital
 * Title: Imperial-Class Star Destroyer
 */
public class Card1_302 extends AbstractCapitalStarship {
    public Card1_302() {
        super(Side.DARK, 1, 8, 8, 6, null, 3, 9, "Imperial-Class Star Destroyer", Uniqueness.UNRESTRICTED, ExpansionSet.PREMIERE, Rarity.U1);
        setLore("Mainstay of Imperial Navy. 1.6 kilometers long. Has hangars and facilities for TIE fighter squadrons, shuttles, drop-ships and combat vehicles such as AT-ATs and AT-STs.");
        setGameText("May add 6 pilots, 8 passengers, 2 vehicles and 4 TIEs. Has ship-docking capability. Permanent pilot aboard provides ability of 1.");
        addIcons(Icon.PILOT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addModelType(ModelType.IMPERIAL_CLASS_STAR_DESTROYER);
        setPilotCapacity(6);
        setPassengerCapacity(8);
        setVehicleCapacity(2);
        setTIECapacity(4);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(1) {});
    }
}
