package com.gempukku.swccgo.cards.set209.light;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
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
 * Title: Red 5 (V)
 */
public class Card209_031 extends AbstractStarfighter {
    public Card209_031() {
        super(Side.LIGHT, 2, 2, 3, null, 4, 5, 6, "Red 5", Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("Luke's Incom T-65 X-wing at the Battle of Yavin. Instrumentation similarities between Red 5 and the T-16 skyhopper allowed Luke to play a pivotal role in the conflict.");
        setGameText("May add 1 pilot and 1 astromech. While Luke piloting: maneuver +2, immune to attrition, and you may initiate battles and Force drains here regardless of Objective restrictions.");
        addPersona(Persona.RED_5);
        addIcons(Icon.A_NEW_HOPE, Icon.NAV_COMPUTER, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_9);
        addModelType(ModelType.X_WING);
        setPilotCapacity(1);
        setAstromechCapacity(1);
        addKeywords(Keyword.RED_SQUADRON);
        setMatchingPilotFilter(Filters.Luke);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        Condition lukePiloting = new HasPilotingCondition(self, Filters.Luke);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ManeuverModifier(self, lukePiloting, 2));
        modifiers.add(new ImmuneToAttritionModifier(self, lukePiloting));

        modifiers.add(new IgnoresObjectiveRestrictionsWhenForceDrainingAtLocationModifier(self, null, lukePiloting, Filters.sameLocation(self)));
        return modifiers;
    }

}
