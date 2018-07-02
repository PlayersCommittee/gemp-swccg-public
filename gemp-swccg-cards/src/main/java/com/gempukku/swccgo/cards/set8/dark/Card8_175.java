package com.gempukku.swccgo.cards.set8.dark;

import com.gempukku.swccgo.cards.AbstractCombatVehicle;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.MayTargetAdjacentSiteModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Endor
 * Type: Vehicle
 * Subtype: Combat
 * Title: Tempest Scout 4
 */
public class Card8_175 extends AbstractCombatVehicle {
    public Card8_175() {
        super(Side.DARK, 4, 3, 4, 4, null, 2, 4, Title.Tempest_Scout_4, Uniqueness.UNIQUE);
        setLore("Enclosed vehicle. Equipped with a prototype targeting computer. Maintains accuracy even in dense foliage. Energy diverted from propulsion to weapons systems.");
        setGameText("May add 1 pilot or passenger. Permanent pilot provides ability of 1. AT-ST Dual Cannon deploys free aboard and may target at an adjacent site.");
        addModelType(ModelType.AT_ST);
        addIcons(Icon.ENDOR, Icon.PILOT, Icon.SCOMP_LINK);
        addKeywords(Keyword.ENCLOSED);
        setPilotOrPassengerCapacity(1);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(1) {});
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiersEvenIfUnpiloted(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeploysFreeToTargetModifier(self, Filters.AT_ST_Dual_Cannon, self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayTargetAdjacentSiteModifier(self, Filters.and(Filters.AT_ST_Dual_Cannon, Filters.attachedTo(self))));
        return modifiers;
    }
}
