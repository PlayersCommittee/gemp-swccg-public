package com.gempukku.swccgo.cards.set9.light;

import com.gempukku.swccgo.cards.AbstractCapitalStarship;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
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
import com.gempukku.swccgo.logic.modifiers.DeploysFreeToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Death Star II
 * Type: Starship
 * Subtype: Capital
 * Title: Nebulon-B Frigate
 */
public class Card9_080 extends AbstractCapitalStarship {
    public Card9_080() {
        super(Side.LIGHT, 3, 5, 4, 5, null, 4, 6, "Nebulon-B Frigate", Uniqueness.UNRESTRICTED, ExpansionSet.DEATH_STAR_II, Rarity.U);
        setLore("Product of Imperial Kuat Drive Yards. Several captured by Alliance. Imperial crews promptly defected. Can be outfitted for a variety of missions requiring mid-size capital starships.");
        setGameText("May add 4 pilots, 4 passengers, and 2 starfighters. Has ship-docking capability. Permanent pilot provides ability of 1. Turbolaser batteries and laser cannons may deploy aboard for free.");
        addIcons(Icon.DEATH_STAR_II, Icon.PILOT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addModelType(ModelType.NEBULON_B_FRIGATE);
        setPilotCapacity(4);
        setPassengerCapacity(4);
        setStarfighterCapacity(2);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(1) {});
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiersEvenIfUnpiloted(SwccgGame game, final PhysicalCard self) {
        Filter turbolaserBatteryAndLaserCannon = Filters.and(Filters.your(self), Filters.or(Filters.turbolaser_battery, Filters.and(Filters.laser_cannon, Filters.starship_weapon_that_deploys_on_capitals)));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployToTargetModifier(self, turbolaserBatteryAndLaserCannon, self));
        modifiers.add(new DeploysFreeToTargetModifier(self, turbolaserBatteryAndLaserCannon, self));
        return modifiers;
    }
}
