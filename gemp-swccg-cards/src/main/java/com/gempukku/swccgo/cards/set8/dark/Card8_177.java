package com.gempukku.swccgo.cards.set8.dark;

import com.gempukku.swccgo.cards.AbstractCombatVehicle;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.MayDeployAsReactModifier;
import com.gempukku.swccgo.logic.modifiers.MayMoveAsReactForFreeModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Endor
 * Type: Vehicle
 * Subtype: Combat
 * Title: Tempest Scout 6
 */
public class Card8_177 extends AbstractCombatVehicle {
    public Card8_177() {
        super(Side.DARK, 3, 3, 3, 3, null, 3, 4, Title.Tempest_Scout_6, Uniqueness.UNIQUE);
        setLore("Enclosed. Modified for perimeter patrol. Elite crew trained for many months over heavily forested terrain.");
        setGameText("May deploy -2 as a 'react'. May move as a 'react' for free. Permanent pilots provide total ability of 2.");
        addModelType(ModelType.AT_ST);
        addIcons(Icon.ENDOR, Icon.SCOMP_LINK);
        addIcon(Icon.PILOT, 2);
        addKeywords(Keyword.ENCLOSED);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployAsReactModifier(self, -2));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayMoveAsReactForFreeModifier(self));
        return modifiers;
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        List<AbstractPermanentAboard> permanentsAboard = new ArrayList<AbstractPermanentAboard>();
        permanentsAboard.add(new AbstractPermanentPilot(1) {});
        permanentsAboard.add(new AbstractPermanentPilot(1) {});
        return permanentsAboard;
    }
}
