package com.gempukku.swccgo.cards.set3.light;

import com.gempukku.swccgo.cards.AbstractCombatVehicle;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.MayMoveAsReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Hoth
 * Type: Vehicle
 * Subtype: Combat
 * Title: Snowspeeder
 */
public class Card3_069 extends AbstractCombatVehicle {
    public Card3_069() {
        super(Side.LIGHT, 1, 2, 3, null, 4, 4, 4, "Snowspeeder");
        setLore("Modified Incom T-47 airspeeder. Enclosed. Adapted to the cold by installation of regulator coil heaters. Rebels typically nickname converted speeders after the intended environment.");
        setGameText("May add 1 pilot or passenger. Permanent pilot aboard provides ability of 1. May move as a 'react' to Hoth sites.");
        addModelType(ModelType.T_47);
        addIcons(Icon.HOTH, Icon.PILOT);
        addKeywords(Keyword.ENCLOSED, Keyword.SNOWSPEEDER);
        setPilotOrPassengerCapacity(1);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(1) {});
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayMoveAsReactToLocationModifier(self, Filters.Hoth_site));
        return modifiers;
    }
}
