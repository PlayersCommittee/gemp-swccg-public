package com.gempukku.swccgo.cards.set1.light;

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
 * Title: X-wing
 */
public class Card1_146 extends AbstractStarfighter {
    public Card1_146() {
        super(Side.LIGHT, 2, 2, 3, null, 4, 5, 4, "X-wing");
        setLore("Model T-65 by Incom Corporation. Delivered to Alliance by defecting design team. 12.5 meters long. Wings deploy in an 'X' position for better weapons coverage.");
        setGameText("Permanent pilot aboard provides ability of 1.");
        addIcons(Icon.PILOT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addModelType(ModelType.X_WING);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(1) {});
    }
}
