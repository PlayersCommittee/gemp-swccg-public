package com.gempukku.swccgo.cards.set8.dark;

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
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayMoveAsReactModifier;
import com.gempukku.swccgo.logic.modifiers.MayMoveOtherCardsAsReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Endor
 * Type: Vehicle
 * Subtype: Combat
 * Title: Tempest Scout 2
 */
public class Card8_173 extends AbstractCombatVehicle {
    public Card8_173() {
        super(Side.DARK, 3, 2, 3, 4, null, 3, 4, Title.Tempest_Scout_2, Uniqueness.UNIQUE, ExpansionSet.ENDOR, Rarity.R);
        setLore("Assigned to coordinate battle activities with the Endor biker scout detachment. Enclosed. First saw battle in Kashyyyk forests.");
        setGameText("Deploy -1 to Endor. May add 2 pilots or passengers. May move as a 'react'. Immune to attrition < 3 when Marquand piloting. Your scouts may move here as a 'react'.");
        addModelType(ModelType.AT_ST);
        addIcons(Icon.ENDOR, Icon.SCOMP_LINK);
        addKeywords(Keyword.ENCLOSED);
        setPilotOrPassengerCapacity(2);
        setMatchingPilotFilter(Filters.Marquand);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, -1, Filters.Deploys_on_Endor));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayMoveAsReactModifier(self));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new HasPilotingCondition(self, Filters.Marquand), 3));
        modifiers.add(new MayMoveOtherCardsAsReactToLocationModifier(self, "Move scout as a 'react'",
                self.getOwner(), Filters.and(Filters.your(self), Filters.scout), Filters.here(self)));
        return modifiers;
    }
}
