package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractDevice;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.HyperspeedModifier;
import com.gempukku.swccgo.logic.modifiers.ManeuverModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premiere
 * Type: Device
 * Title: Fusion Generator Supply Tanks
 */
public class Card1_036 extends AbstractDevice {
    public Card1_036() {
        super(Side.LIGHT, 4, PlayCardZoneOption.ATTACHED, Title.Fusion_Generator_Supply_Tanks, Uniqueness.UNRESTRICTED, ExpansionSet.PREMIERE, Rarity.C2);
        setLore("Uses standard fusion technology. Provides starships with energy for hyperspace travel. Installed at docking bays and throughout the Outer Rim Territories.");
        setGameText("Deploy on your starship at a system or sector where a related docking bay is on table. Adds 1 to hyperspeed, power and maneuver.");
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.starship, Filters.at(Filters.and(Filters.system_or_sector, Filters.relatedLocationTo(self, Filters.docking_bay))));
    }

    @Override
    protected Filter getGameTextValidToUseDeviceFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.starship;
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedTo(final SwccgGame game, final PhysicalCard self) {
        return Filters.starship;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter hasAttached = Filters.hasAttached(self);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new HyperspeedModifier(self, hasAttached, 1));
        modifiers.add(new PowerModifier(self, hasAttached, 1));
        modifiers.add(new ManeuverModifier(self, hasAttached, 1));
        return modifiers;
    }
}