package com.gempukku.swccgo.cards.set2.dark;

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
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalPowerModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: A New Hope
 * Type: Starship
 * Subtype: Capital
 * Title: Victory-Class Star Destroyer
 */
public class Card2_155 extends AbstractCapitalStarship {
    public Card2_155() {
        super(Side.DARK, 2, 6, 6, 5, null, 4, 5, "Victory-Class Star Destroyer", Uniqueness.UNRESTRICTED, ExpansionSet.A_NEW_HOPE, Rarity.U1);
        setLore("Commissioned by the Old Republic at end of the Clone Wars, Rendili StarDrive's Victory-class starship is atmosphere-capable but has a low sublight speed.");
        setGameText("May add 4 pilots, 6 passengers, 1 vehicle and 3 TIEs. Has ship-docking capability. Permanent pilot provides ability of 1. Adds 1 to your total power in battles at related planet sites.");
        addIcons(Icon.A_NEW_HOPE, Icon.PILOT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addModelType(ModelType.VICTORY_CLASS_STAR_DESTROYER);
        setPilotCapacity(4);
        setPassengerCapacity(6);
        setVehicleCapacity(1);
        setTIECapacity(3);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(1) {});
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new TotalPowerModifier(self, Filters.and(Filters.planet_site, Filters.relatedSite(self),
                Filters.locationWherePowerCanBeAddedInBattleFromStarshipsControllingSystem(playerId)), 1, playerId));
        return modifiers;
    }
}
