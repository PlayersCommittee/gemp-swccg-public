package com.gempukku.swccgo.cards.set8.dark;

import com.gempukku.swccgo.cards.AbstractCombatVehicle;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.evaluators.PresentEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.InBattleCondition;
import com.gempukku.swccgo.logic.modifiers.MayMoveAsReactModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalPowerModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Endor
 * Type: Vehicle
 * Subtype: Combat
 * Title: Tempest Scout 5
 */
public class Card8_176 extends AbstractCombatVehicle {
    public Card8_176() {
        super(Side.DARK, 3, 3, 3, 4, null, 3, 4, Title.Tempest_Scout_5, Uniqueness.UNIQUE);
        setLore("Uses experimental command and control software to coordinate combat data. Enclosed.");
        setGameText("May add 1 pilot or passenger. May move as a 'react'. Permanent pilot provides ability of 1. Adds 1 to your total power in battle at same site for each of your other piloted combat vehicles present.");
        addModelType(ModelType.AT_ST);
        addIcons(Icon.ENDOR, Icon.PILOT, Icon.SCOMP_LINK);
        addKeywords(Keyword.ENCLOSED);
        setPilotOrPassengerCapacity(1);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayMoveAsReactModifier(self));
        modifiers.add(new TotalPowerModifier(self, Filters.sameSite(self), new InBattleCondition(self),
                new PresentEvaluator(self, Filters.and(Filters.your(self), Filters.other(self), Filters.piloted, Filters.combat_vehicle)),
                self.getOwner()));
        return modifiers;
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(1) {});
    }
}
