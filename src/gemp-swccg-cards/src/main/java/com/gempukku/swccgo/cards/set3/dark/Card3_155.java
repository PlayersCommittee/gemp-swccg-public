package com.gempukku.swccgo.cards.set3.dark;

import com.gempukku.swccgo.cards.AbstractCombatVehicle;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Hoth
 * Type: Vehicle
 * Subtype: Combat
 * Title: Blizzard 2
 */
public class Card3_155 extends AbstractCombatVehicle {
    public Card3_155() {
        super(Side.DARK, 2, 6, 6, 8, null, 1, 6, Title.Blizzard_2, Uniqueness.UNIQUE, ExpansionSet.HOTH, Rarity.R2);
        setLore("AT-AT commanded by the treacherous General Nevar before he was assassinated. Fortified with an extra layer of armor by the paranoid general. Enclosed.");
        setGameText("May add 1 pilot and 8 passengers. Immune to attrition < 4. Permanent pilot aboard provides ability of 2. Landspeed may not be increased.");
        addModelType(ModelType.AT_AT);
        addIcons(Icon.HOTH, Icon.PILOT, Icon.SCOMP_LINK);
        addKeywords(Keyword.ENCLOSED);
        setPilotCapacity(1);
        setPassengerCapacity(8);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 4));
        return modifiers;
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(2) {});
    }
}
