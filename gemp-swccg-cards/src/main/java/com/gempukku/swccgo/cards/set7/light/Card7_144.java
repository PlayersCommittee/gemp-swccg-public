package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractCapitalStarship;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.MayDeployToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Starship
 * Subtype: Capital
 * Title: Medium Bulk Freighter
 */
public class Card7_144 extends AbstractCapitalStarship {
    public Card7_144() {
        super(Side.LIGHT, 3, 3, 3, 4, null, 4, 4, "Medium Bulk Freighter", Uniqueness.UNRESTRICTED, ExpansionSet.SPECIAL_EDITION, Rarity.U);
        setLore("Modern Corellian design. Length 100 meters. Features engine system similar to that of a Corellian corvette. Dorsal hatch reveals hangar bay.");
        setGameText("Deploys and moves like a starfighter. May add 2 pilots, 6 passengers and 1 vehicle. Permanent pilot provides ability of 1. Has ship-docking capability. Quad Laser Cannon may deploy aboard.");
        addIcons(Icon.SPECIAL_EDITION, Icon.INDEPENDENT, Icon.PILOT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addModelType(ModelType.YV_CLASS_FREIGHTER);
        setPilotCapacity(2);
        setPassengerCapacity(6);
        setVehicleCapacity(1);
    }

    @Override
    public boolean isDeploysAndMovesLikeStarfighter() {
        return true;
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(1) {});
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiersEvenIfUnpiloted(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployToTargetModifier(self, Filters.and(Filters.your(self), Filters.Quad_Laser_Cannon), self));
        return modifiers;
    }
}
