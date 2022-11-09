package com.gempukku.swccgo.cards.set3.light;

import com.gempukku.swccgo.cards.AbstractCombatVehicle;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
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
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayMoveAsReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Hoth
 * Type: Vehicle
 * Subtype: Combat
 * Title: Rogue 3
 */
public class Card3_068 extends AbstractCombatVehicle {
    public Card3_068() {
        super(Side.LIGHT, 2, 2, 3, null, 4, 4, 5, Title.Rogue_3, Uniqueness.UNIQUE, ExpansionSet.HOTH, Rarity.R1);
        setLore("Enclosed. Piloted by Commander Wedge Antilles. Successful walker tripper. Abandoned during the Rebel evacuation of Echo Base.");
        setGameText("May add 2 pilots or passengers. Immune to attrition < 3 if Wedge piloting. May move as a 'react' only to Hoth sites.");
        addModelType(ModelType.T_47);
        addIcons(Icon.HOTH);
        addKeywords(Keyword.ENCLOSED, Keyword.SNOWSPEEDER, Keyword.ROGUE_SQUADRON);
        setPilotOrPassengerCapacity(2);
        setMatchingPilotFilter(Filters.Wedge);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new HasPilotingCondition(self, Filters.Wedge), 3));
        modifiers.add(new MayMoveAsReactToLocationModifier(self, Filters.Hoth_site));
        return modifiers;
    }
}
