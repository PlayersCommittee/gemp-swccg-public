package com.gempukku.swccgo.cards.set601.light;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.HasAboardCondition;
import com.gempukku.swccgo.cards.evaluators.AbilityOfPilotEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextManeuverModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostForSimultaneouslyDeployingPilotModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Block 5
 * Type: Starship
 * Subtype: Starfighter
 * Title: Jedi Starfighter
 */
public class Card601_170 extends AbstractStarfighter {
    public Card601_170() {
        super(Side.LIGHT, 2, 3, 2, null, 0, 6, 5, "Jedi Starfighter", Uniqueness.RESTRICTED_2, ExpansionSet.LEGACY, Rarity.V);
        setGameText("May add 1 pilot and 1 astromech. * = pilot's ability. Obi-wan and Plo are matching pilots and deploy -2 aboard. While a Jedi aboard, power +2 and immune to Lateral Damage and attrition < 5.");
        addIcons(Icon.EPISODE_I, Icon.REPUBLIC, Icon.NAV_COMPUTER, Icon.LEGACY_BLOCK_5, Icon.SCOMP_LINK, Icon.DEATH_STAR_II);
        addModelType(ModelType.JEDI_INTERCEPTOR);
        setPilotCapacity(1);
        setAstromechCapacity(1);
        setMatchingPilotFilter(Filters.or(Filters.ObiWan, Filters.persona(Persona.PLO)));
        setAsLegacy(true);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new DeployCostForSimultaneouslyDeployingPilotModifier(self, Filters.or(Filters.ObiWan, Filters.persona(Persona.PLO)), -2));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiersEvenIfUnpiloted(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new DeployCostToTargetModifier(self, Filters.or(Filters.ObiWan, Filters.persona(Persona.PLO)), -2, self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        Condition jediAboardCondition = new HasAboardCondition(self, Filters.Jedi);
        modifiers.add(new DefinedByGameTextManeuverModifier(self, new AbilityOfPilotEvaluator(self)));
        modifiers.add(new PowerModifier(self, jediAboardCondition, 2));
        modifiers.add(new ImmuneToTitleModifier(self, jediAboardCondition, Title.Lateral_Damage));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, jediAboardCondition, 5));
        return modifiers;
    }
}
