package com.gempukku.swccgo.cards.set302.light;

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
 * Set: Dark Jedi Brotherhood Core
 * Type: Starship
 * Subtype: Starfighter
 * Title: E-wing
 */
public class Card302_008 extends AbstractStarfighter {
    public Card302_008() {
        super(Side.LIGHT, 2, 2, 3, null, 4, 5, 4, "E-wing", Uniqueness.UNRESTRICTED, ExpansionSet.DJB_CORE, Rarity.C2);
        setLore("Manufactured by FreiTek Incorporated during the end of the reign of the Empire. The E-wing saw limited use before the New Republic committed ot the newer T-70 and later T-85 X-wings.");
        setGameText("Permanent pilot aboard provides ability of 2.");
        addIcons(Icon.PILOT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addModelType(ModelType.E_WING);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(2) {});
    }
}
