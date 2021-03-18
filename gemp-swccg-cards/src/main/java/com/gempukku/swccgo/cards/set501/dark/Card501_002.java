package com.gempukku.swccgo.cards.set501.dark;

import com.gempukku.swccgo.cards.AbstractCombatVehicle;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.CalculationTotalModifier;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 15
 * Type: Vehicle
 * Subtype: Combat
 * Title: Blizzard 8
 */
public class Card501_002 extends AbstractCombatVehicle {
    public Card501_002() {
        super(Side.DARK, 1, 7, 6, 5, null, 1, 7, "Blizzard 8", Uniqueness.UNIQUE);
        setLore("Death Squadron");
        setGameText("Permanent pilot provides ability 2. Draws one battle destiny if unable to otherwise. May add 1 pilot and 8 passengers. Immune to attrition <4.");
        addModelType(ModelType.AT_AT);
        addIcons(Icon.PILOT, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_15);
        addKeywords(Keyword.ENCLOSED);
        setPilotCapacity(1);
        setPassengerCapacity(8);
        setTestingText("Blizzard 8");
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(2) {
        });
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, 1));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 4));
        return modifiers;
    }
}
