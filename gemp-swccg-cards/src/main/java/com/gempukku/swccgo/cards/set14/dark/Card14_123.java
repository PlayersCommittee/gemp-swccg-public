package com.gempukku.swccgo.cards.set14.dark;

import com.gempukku.swccgo.cards.AbstractCombatVehicle;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Theed Palace
 * Type: Vehicle
 * Subtype: Combat
 * Title: Single Trooper Aerial Platform
 */
public class Card14_123 extends AbstractCombatVehicle {
    public Card14_123() {
        super(Side.DARK, 3, 1, 3, null, 4, 4, 2, "Single Trooper Aerial Platform");
        setLore("STAPs are used by infantry battle droids to provide a variety of range-based advantages on the battlefield.");
        setGameText("May add 1 pilot. May move as a 'react'. When piloted by a battle droid, vehicle and pilot are immune to attrition < 4. Pilot's power = 0. If lost, character aboard may 'jump off' (disembark).");
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I);
        addModelType(ModelType.STAP);
        setPilotCapacity(1);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayMoveAsReactModifier(self));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, Filters.or(self, Filters.piloting(self)),
                new HasPilotingCondition(self, Filters.battle_droid), 4));
        modifiers.add(new ResetPowerModifier(self, Filters.piloting(self), 0));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiersEvenIfUnpiloted(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new CharactersAboardMayJumpOffModifier(self));
        return modifiers;
    }
}
