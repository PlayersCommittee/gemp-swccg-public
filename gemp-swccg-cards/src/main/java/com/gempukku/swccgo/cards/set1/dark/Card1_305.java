package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Side;

import java.util.Collections;
import java.util.List;


/**
 * Set: Premiere
 * Type: Starship
 * Subtype: Starfighter
 * Title: TIE Scout
 */
public class Card1_305 extends AbstractStarfighter {
    public Card1_305() {
        super(Side.DARK, 4, 2, 1, null, 1, 5, 3, "TIE Scout");
        setLore("Limited production, light reconnaissance starship. Minimal armor and weapons. Long-range sensor and communications array. Scouts for Rebel activity.");
        setGameText("May add 1 pilot and 1 passenger, or 2 passengers. Permanent pilot aboard provides ability of 1.");
        addIcons(Icon.PILOT, Icon.NAV_COMPUTER);
        addModelType(ModelType.TIE_SR);
        setPilotOrPassengerCapacity(1);
        setPassengerCapacity(1);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(1) {});
    }
}
