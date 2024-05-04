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
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.MayDeployAsReactModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Starship
 * Subtype: Capital
 * Title: Spiral
 */
public class Card7_149 extends AbstractCapitalStarship {
    public Card7_149() {
        super(Side.LIGHT, 2, 5, 5, 5, null, 3, 7, "Spiral", Uniqueness.UNIQUE, ExpansionSet.SPECIAL_EDITION, Rarity.R);
        setLore("Has conducted several successful ambushes on Imperial convoys. Veteran crew. Has a reputation for arriving when least expected.");
        setGameText("May deploy -3 as a 'react'. May add 3 pilots, 4 passengers and 1 vehicle. Has ship-docking ability. Permanent pilot provides ability of 2.");
        addIcons(Icon.SPECIAL_EDITION, Icon.PILOT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addModelType(ModelType.CORELLIAN_CORVETTE);
        setPilotCapacity(3);
        setPassengerCapacity(4);
        setVehicleCapacity(1);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployAsReactModifier(self, -3));
        return modifiers;
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(2) {});
    }
}
