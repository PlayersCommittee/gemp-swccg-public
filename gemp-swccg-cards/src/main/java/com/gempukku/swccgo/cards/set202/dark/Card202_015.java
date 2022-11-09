package com.gempukku.swccgo.cards.set202.dark;

import com.gempukku.swccgo.cards.AbstractCombatVehicle;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.conditions.HitCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.modifiers.MayMoveAsReactModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTargetedByModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 2
 * Type: Vehicle
 * Subtype: Combat
 * Title: Tempest Scout 3 (V)
 */
public class Card202_015 extends AbstractCombatVehicle {
    public Card202_015() {
        super(Side.DARK, 3, 3, 3, 3, null, 4, 4, Title.Tempest_Scout_3, Uniqueness.UNIQUE, ExpansionSet.SET_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("Enclosed. Assigned to the task of searching for potential Rebel traps. Modified to help cover more territory to accomplish this task.");
        setGameText("May add 1 pilot. May move as a 'react'. Permanent pilot provides ability of 2. Unless this vehicle 'hit', your other vehicles here may not be targeted by artillery weapons, vehicle weapons, or opponent's Interrupts.");
        addModelType(ModelType.AT_ST);
        addIcons(Icon.ENDOR, Icon.PILOT, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_2);
        addKeywords(Keyword.ENCLOSED);
        setPilotCapacity(1);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition unlessHit = new UnlessCondition(new HitCondition(self));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayMoveAsReactModifier(self));
        modifiers.add(new MayNotBeTargetedByModifier(self, Filters.and(Filters.your(self), Filters.other(self), Filters.vehicle, Filters.here(self)),
                unlessHit, Filters.and(Filters.opponents(self), Filters.or(Filters.artillery_weapon, Filters.vehicle_weapon, Filters.Interrupt))));
        return modifiers;
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(2) {});
    }
}
