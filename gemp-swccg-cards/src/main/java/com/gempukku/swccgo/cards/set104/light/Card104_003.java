package com.gempukku.swccgo.cards.set104.light;

import com.gempukku.swccgo.cards.AbstractCombatVehicle;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Side;

import java.util.Collections;
import java.util.List;


/**
 * Set: Premium (Empire Strikes Back Introductory Two Player Game)
 * Type: Vehicle
 * Subtype: Combat
 * Title: Rebel Snowspeeder
 */
public class Card104_003 extends AbstractCombatVehicle {
    public Card104_003() {
        super(Side.LIGHT, 3, 1, 2, null, 3, 4, 2, "Rebel Snowspeeder");
        setLore("Technicians at the Rebel Base on Hoth modified T-47 airspeeders to fly in the frigid atmosphere of the ice planet. Enclosed.");
        setGameText("May add 1 pilot or passenger. Permanent pilot aboard provides ability of 1.");
        addModelType(ModelType.T_47);
        addIcons(Icon.PREMIUM, Icon.PILOT);
        addKeywords(Keyword.ENCLOSED, Keyword.SNOWSPEEDER);
        setPilotOrPassengerCapacity(1);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(1) {});
    }
}
