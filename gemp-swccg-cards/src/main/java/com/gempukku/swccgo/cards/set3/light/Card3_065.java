package com.gempukku.swccgo.cards.set3.light;

import com.gempukku.swccgo.cards.AbstractCapitalStarship;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Side;

import java.util.Collections;
import java.util.List;


/**
 * Set: Hoth
 * Type: Starship
 * Subtype: Capital
 * Title: Medium Transport
 */
public class Card3_065 extends AbstractCapitalStarship {
    public Card3_065() {
        super(Side.LIGHT, 3, 2, 1, 3, null, 4, 4, "Medium Transport");
        setLore("Passenger and cargo transport built by Gallofree Yards. Enormous cargo bays can be outfitted to safely accommodate large numbers of troops, munitions or supplies.");
        setGameText("May add 1 pilot, 4 passengers and 1 vehicle. Deploys and moves like a starfighter. Has ship-docking capability. Permanent pilot aboard provides ability of 1.");
        addIcons(Icon.HOTH, Icon.PILOT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addKeywords(Keyword.MEDIUM_TRANSPORT);
        addModelType(ModelType.TRANSPORT);
        setPilotCapacity(1);
        setPassengerCapacity(4);
        setVehicleCapacity(1);
    }

    @Override
    public boolean isDeploysAndMovesLikeStarfighter() {
        return true;
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(1) {});
    }
}
