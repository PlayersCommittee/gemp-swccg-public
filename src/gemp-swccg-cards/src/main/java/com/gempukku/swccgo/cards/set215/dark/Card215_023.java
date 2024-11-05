package com.gempukku.swccgo.cards.set215.dark;

import com.gempukku.swccgo.cards.AbstractCombatVehicle;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
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
public class Card215_023 extends AbstractCombatVehicle {
    public Card215_023() {
        super(Side.DARK, 1, 7, 6, 6, null, 1, 7, "Blizzard 8", Uniqueness.UNIQUE, ExpansionSet.SET_15, Rarity.V);
        setLore("Enclosed. Death Squadron");
        setGameText("May add 1 pilot and 8 passengers. Permanent pilot provides ability of 2. Draws one battle destiny if unable to otherwise. Immune to attrition < 4.");
        addModelType(ModelType.AT_AT);
        addIcons(Icon.PILOT, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_15);
        addKeywords(Keyword.ENCLOSED, Keyword.DEATH_SQUADRON);
        setPilotCapacity(1);
        setPassengerCapacity(8);
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
