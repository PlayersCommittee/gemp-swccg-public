package com.gempukku.swccgo.cards.set9.light;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.modifiers.CalculationTotalWhenTargetedModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostForSimultaneouslyDeployingPilotModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.DeployForFreeForSimultaneouslyDeployingPilotModifier;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Death Star II
 * Type: Starship
 * Subtype: Starfighter
 * Title: Red Squadron 1
 */
public class Card9_081 extends AbstractStarfighter {
    public Card9_081() {
        super(Side.LIGHT, 3, 3, 3, null, 4, 5, 5, "Red Squadron 1", Uniqueness.UNIQUE, ExpansionSet.DEATH_STAR_II, Rarity.R);
        setLore("Flown by Wedge Antilles as Red 2 at the Battle of Yavin. Redesignated at Endor. Rugged Incom fighter. Victory markers show it role in the attack on the first Death Star.");
        setGameText("May add 1 pilot. [Death Star II] Wedge deploys -2 aboard (for free if deploying to Endor). When Wedge piloting, immune to attrition < 5 and adds 6 to total when targeted by Tallon Roll.");
        addPersona(Persona.RED_2);
        addIcons(Icon.DEATH_STAR_II, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addKeywords(Keyword.RED_SQUADRON);
        addModelType(ModelType.X_WING);
        setPilotCapacity(1);
        setMatchingPilotFilter(Filters.Wedge);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        Filter deathStarIIWedge = Filters.and(Icon.DEATH_STAR_II, Filters.Wedge);
        Filter toEndor = Filters.locationAndCardsAtLocation(Filters.Endor_location);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostForSimultaneouslyDeployingPilotModifier(self, deathStarIIWedge, -2, Filters.not(toEndor)));
        modifiers.add(new DeployForFreeForSimultaneouslyDeployingPilotModifier(self, deathStarIIWedge, toEndor));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiersEvenIfUnpiloted(SwccgGame game, final PhysicalCard self) {
        Filter deathStarIIWedge = Filters.and(Icon.DEATH_STAR_II, Filters.Wedge);
        Condition atEndor = new AtCondition(self, Filters.Endor_location);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToTargetModifier(self, deathStarIIWedge, new NotCondition(atEndor), -2, self));
        modifiers.add(new DeploysFreeToTargetModifier(self, deathStarIIWedge, atEndor, self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition wedgePiloting = new HasPilotingCondition(self, Filters.Wedge);
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, wedgePiloting, 5));
        modifiers.add(new CalculationTotalWhenTargetedModifier(self, wedgePiloting, 6, Filters.Tallon_Roll));
        return modifiers;
    }
}
