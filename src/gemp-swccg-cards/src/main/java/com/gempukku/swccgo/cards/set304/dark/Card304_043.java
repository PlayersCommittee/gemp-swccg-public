package com.gempukku.swccgo.cards.set304.dark;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.HasAboardCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployToTargetModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: The Great Hutt Expansion
 * Type: Starship
 * Subtype: Starfighter
 * Title: TIE/BA
 */

public class Card304_043 extends AbstractStarfighter {
    public Card304_043() {
        super(Side.DARK, 2, 2, 2, null, 4, 3, 4, "TIE/BA", Uniqueness.UNRESTRICTED, ExpansionSet.GREAT_HUTT_EXPANSION, Rarity.V);
        setLore("Also know as the TIE Baron, this particular model was used during the recent Cold War. Scholae Palatinae engineers obtained the schematics and improved upon the design.");
        setGameText("May add 1 pilot . Matching starfighter for any [CSP Icon] pilot of ability < 5. Boosted TIE Cannon may deploy aboard. While matching pilot aboard, immune to attrition < 3.");
        addModelType(ModelType.TIE_BA);
        setPilotCapacity(1);
        setMatchingPilotFilter(Filters.and(Filters.CSP_pilot, Filters.abilityLessThan(6)));
        addIcons(Icon.CSP, Icon.NAV_COMPUTER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition CSPOnBoard = new HasAboardCondition(self, Filters.and(Filters.CSP_pilot, Filters.abilityLessThan(5)));
        //Condition CSPPiloting = new HasPilotingCondition(self, Filters.and(Filters.CSP_pilot, Filters.abilityLessThan(3)));
        Condition AtOpponentsSystem = new AtCondition(self, Filters.and(Filters.opponents(self), Filters.system));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, CSPOnBoard, 3));

        return modifiers;
    }
	
	@Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiersEvenIfUnpiloted(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployToTargetModifier(self, Filters.and(Filters.your(self), Filters.Boosted_TIE_Cannon), self));
        return modifiers;
    }

}
