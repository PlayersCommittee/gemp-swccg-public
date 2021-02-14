package com.gempukku.swccgo.cards.set8.light;

import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.FiresForFreeModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Endor
 * Type: Starship
 * Subtype: Starfighter
 * Title: YT-1300 Transport
 */
public class Card8_080 extends AbstractStarfighter {
    public Card8_080() {
        super(Side.LIGHT, 3, 3, 2, null, 3, 5, 5, Title.YT_1300_Transport);
        setLore("Reliable and durable. Widely used freighter made by Corellian Engineering Corporation. Sales have dramatically increased in proportion to the fame of Han Solo's ship.");
        setGameText("May add 1 pilot, 2 passengers and 1 vehicle. Has ship-docking capability. Permanent pilot provides ability of 1. Quad Laser Cannon and Surface Defense Cannon may deploy (and fire free) aboard.");
        addIcons(Icon.ENDOR, Icon.INDEPENDENT, Icon.PILOT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addKeywords(Keyword.SHIP_DOCKING_CAPABILITY, Keyword.TRANSPORT_SHIP);
        addModelType(ModelType.MODIFIED_LIGHT_FREIGHTER);
        setPilotCapacity(1);
        setPassengerCapacity(2);
        setVehicleCapacity(1);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(1) {});
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiersEvenIfUnpiloted(SwccgGame game, final PhysicalCard self) {
        Filter quadLaserCannonAndSurfaceDefenseCannon = Filters.or(Filters.Quad_Laser_Cannon, Filters.Surface_Defense_Cannon);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployToTargetModifier(self, Filters.and(Filters.your(self), quadLaserCannonAndSurfaceDefenseCannon), self));
        modifiers.add(new DeploysFreeToTargetModifier(self, quadLaserCannonAndSurfaceDefenseCannon, self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter quadLaserCannonAndSurfaceDefenseCannon = Filters.or(Filters.Quad_Laser_Cannon, Filters.Surface_Defense_Cannon);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new FiresForFreeModifier(self, Filters.and(quadLaserCannonAndSurfaceDefenseCannon, Filters.attachedTo(self))));
        return modifiers;
    }
}
