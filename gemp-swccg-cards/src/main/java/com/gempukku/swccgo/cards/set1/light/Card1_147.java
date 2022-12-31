package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.AbstractStarfighter;
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
 * Subtype: Starfighter
 * Title: Y-wing
 */
public class Card1_147 extends AbstractStarfighter {
    public Card1_147() {
        super(Side.LIGHT, 3, 1, 2, null, 3, 4, 2, "Y-wing", Uniqueness.UNRESTRICTED, ExpansionSet.PREMIERE, Rarity.C2);
        setLore("Rugged Rebel Alliance fighter. BTL-S3 has room for a second pilot to assist weapons operations. 16 meters long. Built by Koensayr.");
        setGameText("May add 1 pilot or passenger. Permanent pilot aboard provides ability of 1.");
        addIcons(Icon.PILOT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addModelType(ModelType.Y_WING);
        setPilotOrPassengerCapacity(1);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(1) {});
    }
}
