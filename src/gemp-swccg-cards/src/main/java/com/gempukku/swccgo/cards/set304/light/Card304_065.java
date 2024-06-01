package com.gempukku.swccgo.cards.set304.light;

import com.gempukku.swccgo.cards.AbstractCombatVehicle;
import com.gempukku.swccgo.cards.conditions.PilotedCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.CharactersAboardMayJumpOffModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayMoveAsReactModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ResetPowerModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployToTargetModifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: The Great Hutt Expansion
 * Type: Vehicle
 * Subtype: Combat
 * Title: Ixtal's Speeder
 */
public class Card304_065 extends AbstractCombatVehicle {
    public Card304_065() {
        super(Side.LIGHT, 4, 2, 1, null, 6, 5, 4, "Ixtal's Speeder", Uniqueness.UNIQUE, ExpansionSet.GREAT_HUTT_EXPANSION, Rarity.V);
        setLore("Custom modified Product Concepts Limited X-X landspeeder used by Ixtal Noxus. While built for speed, rumor has it Ixtal added a blaster to ensure his victory in illegal street races.");
        setGameText("May add 1 pilot and 1 passenger. May be targeted by Dual Laser Cannons. May move as a 'react'. When piloted, vehicle and Ixtal aboard are immune to attrition < 6. Pilot's power = 0. If lost, characters aboard may 'jump off' (disembark).");
        addModelType(ModelType.SPEEDER_BIKE);
        setPilotCapacity(1);
        setPassengerCapacity(1);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayMoveAsReactModifier(self));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, Filters.or(self, Filters.and(Filters.Ixtal, Filters.aboard(self))), new PilotedCondition(self), 6));
        modifiers.add(new ResetPowerModifier(self, Filters.piloting(self), 0));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiersEvenIfUnpiloted(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new CharactersAboardMayJumpOffModifier(self));
		modifiers.add(new MayDeployToTargetModifier(self, Filters.and(Filters.your(self), Filters.Dual_Laser_Cannon), self));
        return modifiers;
    }
}
