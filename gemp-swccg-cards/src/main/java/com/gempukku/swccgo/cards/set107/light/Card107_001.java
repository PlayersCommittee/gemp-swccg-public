package com.gempukku.swccgo.cards.set107.light;

import com.gempukku.swccgo.cards.AbstractCapitalStarship;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;

import java.util.Collections;
import java.util.List;


/**
 * Set: Second Anthology
 * Type: Starship
 * Subtype: Capital
 * Title: Mon Calamari Star Cruiser
 */
public class Card107_001 extends AbstractCapitalStarship {
    public Card107_001() {
        super(Side.LIGHT, 1, 8, 7, 5, null, 3, 9, "Mon Calamari Star Cruiser");
        setLore("Mon Cal MC80 cruiser. Originally a civilian ship. Converted to military use following the liberation of Mon Calamari from the Empire.");
        setGameText("Deploys only at Mon Calamari or any Rebel Base. May add 5 pilots, 6 passengers, 1 vehicle and 3 starfighters. Has ship-docking capability. Permanent pilot aboard provides ability of 2.");
        addIcons(Icon.DEATH_STAR_II, Icon.PILOT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addModelType(ModelType.MON_CALAMARI_STAR_CRUISER);
        setPilotCapacity(5);
        setPassengerCapacity(6);
        setVehicleCapacity(1);
        setStarfighterCapacity(3);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.or(Filters.Deploys_at_Mon_Calamari, Filters.Deploys_at_Rebel_Base);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(2) {});
    }
}
