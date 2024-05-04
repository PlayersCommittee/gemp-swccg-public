package com.gempukku.swccgo.cards.set601.light;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.DeployCostForSimultaneouslyDeployingPilotModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Block 1
 * Type: Starship
 * Subtype: Starfighter
 * Title: Millennium Falcon (V)
 */
public class Card601_142 extends AbstractStarfighter {
    public Card601_142() {
        super(Side.LIGHT, 2, 3, 3, null, 5, 6, 7, "Millennium Falcon", Uniqueness.UNIQUE, ExpansionSet.LEGACY, Rarity.V);
        setVirtualSuffix(true);
        setLore("Modified YT-1300 freighter. Owned by Lando Calrissian until won by Han in a sabacc game. 26.7 meters long. 'She may not look like much, but she's got it where it counts.'");
        setGameText("May add 2 pilots. Chewie deploys -3 aboard. If Falcon is about to use hyperspeed, draw destiny; if destiny < 3, hyperspeed = 0 this turn. Immune to attrition < 4 if Han or Chewie piloting (< 9 if both).");
        addPersona(Persona.FALCON);
        addIcons(Icon.NAV_COMPUTER, Icon.SCOMP_LINK, Icon.LEGACY_BLOCK_1);
        addModelType(ModelType.MODIFIED_LIGHT_FREIGHTER);
        setPilotCapacity(2);
        setMatchingPilotFilter(Filters.or(Filters.Han, Filters.Chewie));
        setAsLegacy(true);
        hideFromDeckBuilder();
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostForSimultaneouslyDeployingPilotModifier(self, Filters.Chewie, -3));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiersEvenIfUnpiloted(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToTargetModifier(self, Filters.Chewie, -3, self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition hanOrChewiePiloting = new HasPilotingCondition(self, Filters.or(Filters.Han, Filters.Chewie));
        Condition hanAndChewiePiloting = new AndCondition(new HasPilotingCondition(self, Filters.Han), new HasPilotingCondition(self, Filters.Chewie));
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, hanOrChewiePiloting, new ConditionEvaluator(4, 9, hanAndChewiePiloting)));
        return modifiers;
    }

    //TODO If Falcon is about to use hyperspeed, draw destiny; if destiny < 3, hyperspeed = 0 this turn.
}
