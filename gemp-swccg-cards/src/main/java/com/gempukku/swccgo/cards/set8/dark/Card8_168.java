package com.gempukku.swccgo.cards.set8.dark;

import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.LandsForFreeModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TakesOffForFreeModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Endor
 * Type: Starship
 * Subtype: Starfighter
 * Title: Lambda-Class Shuttle
 */
public class Card8_168 extends AbstractStarfighter {
    public Card8_168() {
        super(Side.DARK, 4, 2, 2, null, 2, 3, 3, "Lambda-Class Shuttle");
        setLore("Manufactured by Sienar Fleet Systems. Length 20 meters. Bottom wings fold for docking and landing. Boarding ramp allows easy access to passenger and cargo areas.");
        setGameText("Deploy -1 if Sienar Fleet Systems on table. May add 1 pilot and 6 passengers. Permanent pilot provides ability of 1. Takes off and lands for free.");
        addIcons(Icon.ENDOR, Icon.PILOT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addModelType(ModelType.LAMBDA_CLASS_SHUTTLE);
        setPilotCapacity(1);
        setPassengerCapacity(6);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostModifier(self, new OnTableCondition(self, Filters.Sienar_Fleet_Systems), -1));
        return modifiers;
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(1) {});
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new LandsForFreeModifier(self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiersEvenIfUnpiloted(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new TakesOffForFreeModifier(self));
        return modifiers;
    }
}
