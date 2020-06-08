package com.gempukku.swccgo.cards.set8.dark;

import com.gempukku.swccgo.cards.AbstractCombatVehicle;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.InBattleCondition;
import com.gempukku.swccgo.logic.modifiers.AttritionModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayMoveAsReactForFreeModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Endor
 * Type: Vehicle
 * Subtype: Combat
 * Title: Tempest Scout 1
 */
public class Card8_172 extends AbstractCombatVehicle {
    public Card8_172() {
        super(Side.DARK, 3, 2, 3, 4, null, 3, 4, Title.Tempest_Scout_1, Uniqueness.UNIQUE);
        setLore("Enclosed AT-ST assigned as first response to incidents at Endor control bunker. Piloted by Lieutenant Arnet.");
        setGameText("May add 2 pilots or passengers. May move as a 'react' for free. When Arnet piloting, immune to attrition < 4 and adds 1 to attrition against opponent here.");
        addModelType(ModelType.AT_ST);
        addIcons(Icon.ENDOR, Icon.SCOMP_LINK);
        addKeywords(Keyword.ENCLOSED);
        setPilotOrPassengerCapacity(2);
        setMatchingPilotFilter(Filters.Arnet);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition arnetPiloting = new HasPilotingCondition(self, Filters.Arnet);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayMoveAsReactForFreeModifier(self));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, arnetPiloting, 4));
        modifiers.add(new AttritionModifier(self, new AndCondition(new InBattleCondition(self), arnetPiloting), 1, game.getOpponent(self.getOwner())));
        return modifiers;
    }
}
