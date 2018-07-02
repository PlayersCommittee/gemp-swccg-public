package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.AbstractShuttleVehicle;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextLandspeedModifier;
import com.gempukku.swccgo.logic.modifiers.MayMoveAsReactModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: A New Hope
 * Type: Vehicle
 * Subtype: Shuttle
 * Title: Incom T-16 Skyhopper
 */
public class Card2_075 extends AbstractShuttleVehicle {
    public Card2_075() {
        super(Side.LIGHT, 3, 2, 1, 5, null, 4, "Incom T-16 Skyhopper");
        setLore("Enclosed vehicle used for shuttling and hot-rodding. E-16/x ion engine pushes T-16 up to 1200 kph.");
        setGameText("May add 1 passenger. Permanent pilot provides ability of 1. May move as a 'react.' * Landspeed = 4. OR 1 character may shuttle to or from same site for free.");
        addIcons(Icon.A_NEW_HOPE, Icon.PILOT);
        addKeywords(Keyword.ENCLOSED);
        addModelType(ModelType.T_16);
        setPassengerCapacity(1);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(1) {});
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayMoveAsReactModifier(self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextLandspeedModifier(self, 4));
        return modifiers;
    }
}
