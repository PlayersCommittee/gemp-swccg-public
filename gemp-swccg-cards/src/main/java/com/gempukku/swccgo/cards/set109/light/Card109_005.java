package com.gempukku.swccgo.cards.set109.light;

import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.HasPilotSeatOccupiedCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.modifiers.SuspendPermanentPilotModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Premium (Enhanced Cloud City)
 * Type: Starship
 * Subtype: Starfighter
 * Title: Z-95 Bespin Defense Fighter
 */
public class Card109_005 extends AbstractStarfighter {
    public Card109_005() {
        super(Side.LIGHT, 2, 2, 2, null, 4, 2, 3, "Z-95 Bespin Defense Fighter", Uniqueness.RESTRICTED_3, ExpansionSet.ENHANCED_CLOUD_CITY, Rarity.PM);
        setLore("Used to combat pirate activity in and around Cloud City. Top speed in atmosphere 1,150 kph. Hyperdrive installed by Bespin Motors.");
        setGameText("Permanent pilot provides ability of 1 and adds 1 to power. May add one alien pilot (suspends permanent pilot). Power +2 at Bespin locations. May be carried aboard starships like a vehicle.");
        addIcons(Icon.PREMIUM, Icon.INDEPENDENT, Icon.PILOT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addModelType(ModelType.MODIFIED_Z_95_HEADHUNTER);
        setPilotCapacity(1);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(
                new AbstractPermanentPilot(1) {
                    @Override
                    public List<Modifier> getGameTextModifiers(PhysicalCard self) {
                        List<Modifier> modifiers = new LinkedList<Modifier>();
                        modifiers.add(new PowerModifier(self, 1));
                        return modifiers;
                    }
                });
    }

    @Override
    protected Filter getGameTextValidPilotFilter(String playerId, SwccgGame game, PhysicalCard self) {
        return Filters.alien;
    }

    @Override
    protected List<Modifier> getGameTextWhileInPlayEvenIfGameTextCanceledModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new SuspendPermanentPilotModifier(self, new HasPilotSeatOccupiedCondition(self)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new AtCondition(self, Filters.Bespin_location), 2));
        return modifiers;
    }

    @Override
    public boolean isVehicleSlotOfStarshipCompatible() {
        return true;
    }
}
