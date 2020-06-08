package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.LandsForFreeModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TakesOffForFreeModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Starship
 * Subtype: Starfighter
 * Title: Sentinel-Class Landing Craft
 */
public class Card7_307 extends AbstractStarfighter {
    public Card7_307() {
        super(Side.DARK, 2, 3, 3, null, 3, 3, 4, "Sentinel-Class Landing Craft");
        setLore("Modified light freighter. Length 20 meters. Heavily armored for a landing craft. Has ion cannon mounts. Often carries speeder bikes for reconnaissance purposes.");
        setGameText("May add 1 pilot, 4 passengers and 2 speeder bikes. Permanent pilot provides ability of 1. Any starship cannon may deploy here. Takes off and lands for free.");
        addIcons(Icon.SPECIAL_EDITION, Icon.PILOT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addModelTypes(ModelType.SENTINEL_CLASS_LANDING_CRAFT, ModelType.MODIFIED_LIGHT_FREIGHTER);
        setPilotCapacity(1);
        setPassengerCapacity(4);
        setVehicleCapacity(2, Filters.speeder_bike);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(1) {});
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiersEvenIfUnpiloted(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployToTargetModifier(self, Filters.and(Filters.starship_cannon, Filters.starship_weapon_that_deploys_on_starfighters), self));
        modifiers.add(new TakesOffForFreeModifier(self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new LandsForFreeModifier(self));
        return modifiers;
    }
}
