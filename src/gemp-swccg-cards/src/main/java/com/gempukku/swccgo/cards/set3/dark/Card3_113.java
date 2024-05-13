package com.gempukku.swccgo.cards.set3.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextDeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ResetLandspeedModifier;
import com.gempukku.swccgo.logic.modifiers.ResetManeuverModifier;
import com.gempukku.swccgo.logic.modifiers.ResetPowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Hoth
 * Type: Effect
 * Title: Too Cold For Speeders
 */
public class Card3_113 extends AbstractNormalEffect {
    public Card3_113() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Too_Cold_For_Speeders, Uniqueness.UNRESTRICTED, ExpansionSet.HOTH, Rarity.U1);
        setLore("'We're having some trouble adapting them to the cold.'");
        setGameText("Use 2 Force to deploy on your side of table. Non-creature vehicles at marker sites under 'nighttime conditions' are power = 0, maneuver = 0 and landspeed = 0.");
        addIcons(Icon.HOTH);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextDeployCostModifier(self, 2));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        Filter nonCreatureVehiclesAtMarkerSitesUnderNighttimeConditions = Filters.and(Filters.non_creature_vehicle,
                Filters.at(Filters.and(Filters.marker_site, Filters.under_nighttime_conditions)));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ResetPowerModifier(self, nonCreatureVehiclesAtMarkerSitesUnderNighttimeConditions, 0));
        modifiers.add(new ResetManeuverModifier(self, nonCreatureVehiclesAtMarkerSitesUnderNighttimeConditions, 0));
        modifiers.add(new ResetLandspeedModifier(self, nonCreatureVehiclesAtMarkerSitesUnderNighttimeConditions, 0));
        return modifiers;
    }
}