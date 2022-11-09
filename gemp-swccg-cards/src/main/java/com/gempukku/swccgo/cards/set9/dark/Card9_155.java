package com.gempukku.swccgo.cards.set9.dark;

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
import com.gempukku.swccgo.logic.modifiers.MayBeFiredTwicePerBattleModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Death Star II
 * Type: Starship
 * Subtype: Capital
 * Title: Dominator
 */
public class Card9_155 extends AbstractCapitalStarship {
    public Card9_155() {
        super(Side.DARK, 1, 7, 6, 5, null, 4, 7, "Dominator", Uniqueness.UNIQUE, ExpansionSet.DEATH_STAR_II, Rarity.R);
        setLore("Victory-class hull overhauled with powerful thrusters and latest hyperdrive technology. Engineered to support task forces combating Rebel starfighters.");
        setGameText("May add 4 pilots, 6 passengers, 1 vehicle and 2 TIEs. Has ship-docking capability. Permanent pilot provides ability of 2. Laser Cannon Battery may deploy aboard (and may fire twice per battle).");
        addIcons(Icon.DEATH_STAR_II, Icon.PILOT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addModelType(ModelType.VICTORY_CLASS_STAR_DESTROYER);
        setPilotCapacity(4);
        setPassengerCapacity(6);
        setVehicleCapacity(1);
        setTIECapacity(2);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(2) {});
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiersEvenIfUnpiloted(SwccgGame game, final PhysicalCard self) {
        Filter laserCannonBattery = Filters.and(Filters.your(self), Filters.Laser_Cannon_Battery);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployToTargetModifier(self, laserCannonBattery, self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter laserCannonBattery = Filters.Laser_Cannon_Battery;

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayBeFiredTwicePerBattleModifier(self, Filters.and(laserCannonBattery, Filters.attachedTo(self))));
        return modifiers;
    }
}
