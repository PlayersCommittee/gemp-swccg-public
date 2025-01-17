package com.gempukku.swccgo.cards.set201.light;

import com.gempukku.swccgo.cards.AbstractCombatVehicle;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.conditions.HitCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTargetedByModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 1
 * Type: Vehicle
 * Subtype: Combat
 * Title: Dash In Rogue 12
 */
public class Card201_020 extends AbstractCombatVehicle {
    public Card201_020() {
        super(Side.LIGHT, 2, 3, 5, null, 5, 4, 5, "Dash In Rogue 12", Uniqueness.UNIQUE, ExpansionSet.SET_1, Rarity.V);
        setLore("Enclosed.");
        setGameText("May add 1 pilot. Permanent pilot is •Dash, who provides ability of 3. Unless 'hit', your other vehicles here may not be targeted by weapons, High-Speed Tactics, or Crash Landing. Immune to attrition < 4.");
        addModelType(ModelType.T_47);
        addIcons(Icon.HOTH, Icon.PILOT, Icon.VIRTUAL_SET_1);
        addKeywords(Keyword.ENCLOSED, Keyword.SNOWSPEEDER, Keyword.ROGUE_SQUADRON);
        setPilotCapacity(1);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(Persona.DASH, 3) {});
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition unlessHit = new UnlessCondition(new HitCondition(self));
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotBeTargetedByModifier(self, Filters.and(Filters.your(self), Filters.other(self), Filters.T_47, Filters.here(self)),
                unlessHit, Filters.or(Filters.weapon, Filters.Highspeed_Tactics, Filters.Crash_Landing)));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 4));
        return modifiers;
    }
}
