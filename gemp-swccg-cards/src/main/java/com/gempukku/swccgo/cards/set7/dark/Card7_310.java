package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractCapitalStarship;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.evaluators.PerTIElnEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Starship
 * Subtype: Capital
 * Title: Vengeance
 */
public class Card7_310 extends AbstractCapitalStarship {
    public Card7_310() {
        super(Side.DARK, 2, 9, 8, 6, null, 3, 9, "Vengeance", Uniqueness.UNIQUE);
        setLore("Part of Darth Vader's Death Squadron. Support vessel for the fleet's starfighters. Cargo areas converted into hangar space for additional TIE fighters.");
        setGameText("May add 6 pilots, 8 passengers, 1 vehicle and 6 TIEs. Has ship-docking capability. Permanent pilot provides ability of 2. Your TIE/lns are forfeit +2 at same location. Power +2 at Nal Hutta.");
        addIcons(Icon.SPECIAL_EDITION, Icon.PILOT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addModelType(ModelType.IMPERIAL_CLASS_STAR_DESTROYER);
        addKeywords(Keyword.DEATH_SQUADRON);
        setPilotCapacity(6);
        setPassengerCapacity(8);
        setVehicleCapacity(1);
        setTIECapacity(6);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(2) {});
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForfeitModifier(self, Filters.and(Filters.your(self), Filters.TIE_ln, Filters.atSameLocation(self)),
                new PerTIElnEvaluator(2)));
        modifiers.add(new PowerModifier(self, new AtCondition(self, Filters.Nal_Hutta_system), 2));
        return modifiers;
    }
}
