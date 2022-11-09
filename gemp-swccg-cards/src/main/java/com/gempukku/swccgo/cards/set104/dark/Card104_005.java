package com.gempukku.swccgo.cards.set104.dark;

import com.gempukku.swccgo.cards.AbstractCombatVehicle;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;

import java.util.Collections;
import java.util.List;


/**
 * Set: Premium (Empire Strikes Back Introductory Two Player Game)
 * Type: Vehicle
 * Subtype: Combat
 * Title: Imperial Walker
 */
public class Card104_005 extends AbstractCombatVehicle {
    public Card104_005() {
        super(Side.DARK, 2, 4, 4, 6, null, 1, 4, "Imperial Walker", Uniqueness.UNRESTRICTED, ExpansionSet.ESB_INTRO_TWO_PLAYER, Rarity.PM);
        setLore("Four-legged, enclosed combat vehicle. The monstrous, plodding AT-AT (All Terrain Armored Transport) can carry an entire squadron of armed troops.");
        setGameText("May add 1 pilot and 8 passengers. Permanent pilot aboard provides ability of 1.");
        addModelType(ModelType.AT_AT);
        addIcons(Icon.PREMIUM, Icon.PILOT, Icon.SCOMP_LINK);
        addKeywords(Keyword.ENCLOSED);
        setPilotCapacity(1);
        setPassengerCapacity(8);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(1) {});
    }
}
