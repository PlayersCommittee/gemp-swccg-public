package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.AbstractSquadron;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;

import java.util.ArrayList;
import java.util.List;


/**
 * Set: A New Hope
 * Type: Starship
 * Subtype: Squadron
 * Title: Y-wing Assault Squadron
 */
public class Card2_074 extends AbstractSquadron {
    public Card2_074() {
        super(Side.LIGHT, 3, null, 6, null, 3, 4, 6, "Y-wing Assault Squadron", Uniqueness.UNRESTRICTED, ExpansionSet.A_NEW_HOPE, Rarity.U1);
        setLore("The Rebel Alliance deploys starfighters in triangular formations to minimize exposure to enemy fire. Two function as wingmen to cover one leader.");
        setGameText("* Replaces 3 Y-wings at one location (Y-wings go to Used Pile). May add 3 pilots or passengers. Permanent pilots provide total ability of 3.");
        addIcons(Icon.A_NEW_HOPE);
        addIcon(Icon.PILOT, 3);
        addIcon(Icon.NAV_COMPUTER, 3);
        addIcon(Icon.SCOMP_LINK, 3);
        addModelTypes(ModelType.Y_WING, ModelType.Y_WING, ModelType.Y_WING);
        setPilotOrPassengerCapacity(3);
        setReplacementForSquadron(3, Filters.Y_wing);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        List<AbstractPermanentAboard> permanentsAboard = new ArrayList<AbstractPermanentAboard>();
        permanentsAboard.add(new AbstractPermanentPilot(1) {});
        permanentsAboard.add(new AbstractPermanentPilot(1) {});
        permanentsAboard.add(new AbstractPermanentPilot(1) {});
        return permanentsAboard;
    }
}
