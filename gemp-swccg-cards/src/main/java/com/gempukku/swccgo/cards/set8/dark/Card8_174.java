package com.gempukku.swccgo.cards.set8.dark;

import com.gempukku.swccgo.cards.AbstractCombatVehicle;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.MayDeployAsReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayMoveAsReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Endor
 * Type: Vehicle
 * Subtype: Combat
 * Title: Tempest Scout 3
 */
public class Card8_174 extends AbstractCombatVehicle {
    public Card8_174() {
        super(Side.DARK, 3, 3, 3, 4, null, 4, 4, Title.Tempest_Scout_3, Uniqueness.UNIQUE);
        setLore("Enclosed. Assigned to the task of searching for potential Rebel traps. Modified to help cover more territory to accomplish this task.");
        setGameText("May deploy or move as a 'react' to a forest or Endor site. May add 1 pilot or passenger. Permanent pilot provides ability of 2.");
        addModelType(ModelType.AT_ST);
        addIcons(Icon.ENDOR, Icon.PILOT, Icon.SCOMP_LINK);
        addKeywords(Keyword.ENCLOSED);
        setPilotOrPassengerCapacity(1);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployAsReactToLocationModifier(self, Filters.or(Filters.forest, Filters.Endor_site)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayMoveAsReactToLocationModifier(self, Filters.or(Filters.forest, Filters.Endor_site)));
        return modifiers;
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(2) {});
    }
}
