package com.gempukku.swccgo.cards.set8.dark;

import com.gempukku.swccgo.cards.AbstractCombatVehicle;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayMoveAsReactModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Endor
 * Type: Vehicle
 * Subtype: Combat
 * Title: Tempest Scout
 */
public class Card8_171 extends AbstractCombatVehicle {
    public Card8_171() {
        super(Side.DARK, 3, 3, 3, 4, null, 3, 4, "Tempest Scout");
        setLore("Manufactured by Kuat Drive Yards under the close supervision of the Empire. Often used in conjunction with AT-ATs in an anti-personnel role. Enclosed.");
        setGameText("May add 1 pilot or passenger. May move as a 'react' for 1 additional Force. Permanent pilot provides ability of 1. Your Elite Squadron stormtroopers are deploy -1 to same site.");
        addModelType(ModelType.AT_ST);
        addIcons(Icon.ENDOR, Icon.PILOT, Icon.SCOMP_LINK);
        addKeywords(Keyword.ENCLOSED);
        setPilotOrPassengerCapacity(1);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayMoveAsReactModifier(self, 1));
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.your(self), Filters.Elite_Squadron_Stormtrooper), -1, Filters.sameSite(self)));
        return modifiers;
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(1) {});
    }
}
