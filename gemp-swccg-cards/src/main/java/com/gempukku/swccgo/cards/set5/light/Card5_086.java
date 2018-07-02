package com.gempukku.swccgo.cards.set5.light;

import com.gempukku.swccgo.cards.AbstractCapitalStarship;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.common.*;

import java.util.Collections;
import java.util.List;


/**
 * Set: Cloud City
 * Type: Starship
 * Subtype: Capital
 * Title: Bright Hope
 */
public class Card5_086 extends AbstractCapitalStarship {
    public Card5_086() {
        super(Side.LIGHT, 3, 3, 1, 4, null, 4, 5, "Bright Hope", Uniqueness.UNIQUE);
        setLore("Modified medium transport. Well armored. Has expanded passenger capacity to facilitate evacuation. The last transport to escape Hoth. Nearly destroyed by the Stalker.");
        setGameText("May add 1 pilot and 6 passengers. Deploys and moves like a starfighter. Has ship-docking capability. Permanent pilot aboard provides ability of 1.");
        addIcons(Icon.CLOUD_CITY, Icon.PILOT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addKeywords(Keyword.MEDIUM_TRANSPORT);
        addModelType(ModelType.TRANSPORT);
        setPilotCapacity(1);
        setPassengerCapacity(6);
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
