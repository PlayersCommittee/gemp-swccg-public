package com.gempukku.swccgo.cards.set209.dark;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.HasAboardCondition;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 9
 * Type: Starship
 * Subtype: Starfighter
 * Title: First Order Special Forces Tie
 */

public class Card209_052 extends AbstractStarfighter {
    public Card209_052() {
        super(Side.DARK, 2, 2, 3, null, 4, 3, 4, "First Order Special Forces TIE", Uniqueness.RESTRICTED_3);
        setLore("");
        setGameText("May add 1 pilot and 1 passenger. Matching starfighter for any First Order pilot of ability < 3. Power +1 at opponent's system. While matching pilot aboard, immune to attrition < 3.");
        addModelType(ModelType.TIE_SF);
        setPilotCapacity(1);
        setPassengerCapacity(1);
        setMatchingPilotFilter(Filters.and(Filters.First_Order_pilot, Filters.abilityLessThan(3)));
        addIcons(Icon.FIRST_ORDER, Icon.NAV_COMPUTER, Icon.EPISODE_VII, Icon.VIRTUAL_SET_9);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition FOOnBoard = new HasAboardCondition(self, Filters.and(Filters.First_Order_pilot, Filters.abilityLessThan(3)));
        //Condition FOPiloting = new HasPilotingCondition(self, Filters.and(Filters.First_Order_pilot, Filters.abilityLessThan(3)));
        Condition AtOpponentsSystem = new AtCondition(self, Filters.and(Filters.opponents(self), Filters.system));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, FOOnBoard, 3));

        modifiers.add(new PowerModifier(self, AtOpponentsSystem, 1));

        return modifiers;
    }

}
