package com.gempukku.swccgo.cards.set214.light;

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
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 14
 * Type: Starship
 * Subtype: Starfighter
 * Title: Odd Ball's Torrent Starfighter
 */
public class Card214_022 extends AbstractStarfighter {
    public Card214_022() {
        super(Side.LIGHT, 3, 1, 2, null, 3, 3, 3, "Odd Ball's Torrent Starfighter", Uniqueness.UNIQUE);
        setGameText("May add 1 clone pilot. Odd Ball deploys -1 aboard. While Odd Ball piloting, power, hyperspeed, and forfeit +1, and adds one destiny to total power. Immune to attrition < 3 if a clone piloting (< 5 if Odd Ball).");
        addIcons(Icon.EPISODE_I, Icon.REPUBLIC, Icon.NAV_COMPUTER, Icon.VIRTUAL_SET_14);
        addModelType(ModelType.V_19_TORRENT_STARFIGHTER);
        setPilotCapacity(1);
        setMatchingPilotFilter(Filters.persona(Persona.ODD_BALL));
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostForSimultaneouslyDeployingPilotModifier(self, Persona.ODD_BALL, -1));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiersEvenIfUnpiloted(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToTargetModifier(self, Persona.ODD_BALL, -1, self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new ArrayList<>();
        Condition oddBallPilotingCondition = new HasPilotingCondition(self, Persona.ODD_BALL);
        modifiers.add(new PowerModifier(self, oddBallPilotingCondition, 1));
        modifiers.add(new HyperspeedModifier(self, self, oddBallPilotingCondition, 1));
        modifiers.add(new ForfeitModifier(self, oddBallPilotingCondition, 1));
        modifiers.add(new AddsDestinyToPowerModifier(self, oddBallPilotingCondition, 1));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new ConditionEvaluator(3, 5, oddBallPilotingCondition)));
        return modifiers;
    }

    @Override
    protected Filter getGameTextValidPilotFilter(String playerId, SwccgGame game, PhysicalCard self) {
        return Filters.clone;
    }
}
