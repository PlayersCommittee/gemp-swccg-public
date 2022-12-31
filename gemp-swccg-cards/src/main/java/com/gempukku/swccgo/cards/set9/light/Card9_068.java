package com.gempukku.swccgo.cards.set9.light;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Death Star II
 * Type: Starship
 * Subtype: Starfighter
 * Title: Gold Squadron 1
 */
public class Card9_068 extends AbstractStarfighter {
    public Card9_068() {
        super(Side.LIGHT, 3, 3, 3, null, 5, 6, 7, "Gold Squadron 1", Uniqueness.UNIQUE, ExpansionSet.DEATH_STAR_II, Rarity.R);
        setLore("The Millennium Falcon relies on heavily upgraded speed and maneuverability to survive in combat. General Calrissian's starship at Battle of Endor.");
        setGameText("Deploy -2 to Endor. May add 2 pilots and 2 passengers. Immune to Tallon Roll. Immune to attrition < 4 when Lando or Nien Nunb piloting (< 6 when both).");
        addPersona(Persona.FALCON);
        addIcons(Icon.DEATH_STAR_II, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addModelType(ModelType.HEAVILY_MODIFIED_LIGHT_FREIGHTER);
        addKeywords(Keyword.GOLD_SQUADRON);
        setPilotCapacity(2);
        setPassengerCapacity(2);
        setMatchingPilotFilter(Filters.or(Filters.Lando, Filters.Nien_Nunb));
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, -2, Filters.Deploys_at_Endor));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition landoPiloting = new HasPilotingCondition(self, Filters.Lando);
        Condition nienNunbPiloting = new HasPilotingCondition(self, Filters.Nien_Nunb);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToTitleModifier(self, Title.Tallon_Roll));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new OrCondition(landoPiloting, nienNunbPiloting),
                new ConditionEvaluator(4, 6, new AndCondition(landoPiloting, nienNunbPiloting))));
        return modifiers;
    }
}
