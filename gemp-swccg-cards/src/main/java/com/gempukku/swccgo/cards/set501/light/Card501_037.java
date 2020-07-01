package com.gempukku.swccgo.cards.set501.light;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Set: Set 13
 * Type: Starship
 * Subtype: Starfighter
 * Title: Oddball's Torrent Starfighter
 */
public class Card501_037 extends AbstractStarfighter {
    public Card501_037() {
        super(Side.LIGHT, 3, 1, 2, null, 3, 3, 3, "Oddball's Torrent Starfighter", Uniqueness.UNIQUE);
        setGameText("May add 1 clone pilot. Oddball deploys -1 aboard. While Oddball piloting, power, hyperspeed, and forfeit +1, and adds one destiny to total power. Immune to attrition < 3 if a clone piloting (< 5 if Oddball).");
        addIcons(Icon.EPISODE_I, Icon.REPUBLIC, Icon.NAV_COMPUTER, Icon.VIRTUAL_SET_13);
        addModelType(ModelType.V_19_TORRENT_STARFIGHTER);
        setPilotCapacity(1);
        setMatchingPilotFilter(Filters.persona(Persona.ODDBALL));
        setTestingText("Oddball's Torrent Starfighter ");
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new ArrayList<>();
        Condition oddballPilotingCondition = new HasPilotingCondition(self, Persona.ODDBALL);
        modifiers.add(new DeployCostToTargetModifier(self, Persona.ODDBALL, -1, self));
        modifiers.add(new PowerModifier(self, oddballPilotingCondition, 1));
        modifiers.add(new HyperspeedModifier(self, self, oddballPilotingCondition, 1));
        modifiers.add(new ForfeitModifier(self, oddballPilotingCondition, 1));
        modifiers.add(new AddsDestinyToPowerModifier(self, oddballPilotingCondition, 1));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new ConditionEvaluator(3, 5, oddballPilotingCondition)));
        return modifiers;
    }

    @Override
    protected Filter getGameTextValidPilotFilter(String playerId, SwccgGame game, PhysicalCard self) {
        return Filters.clone;
    }
}
