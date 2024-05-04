package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: A New Hope
 * Type: Starship
 * Subtype: Starfighter
 * Title: Red 2
 */
public class Card2_070 extends AbstractStarfighter {
    public Card2_070() {
        super(Side.LIGHT, 2, 2, 3, null, 4, 5, 5, "Red 2", Uniqueness.UNIQUE, ExpansionSet.A_NEW_HOPE, Rarity.R1);
        setLore("Wedge's X-wing at Battle of Yavin. Wedge had to nurse the fighter home after a hit in the Death Star trench destroyed its micro-maneuvering controls.");
        setGameText("May add 1 pilot and 1 astromech. Immune to attrition < 3 if Wedge piloting. Must have pilot aboard to use power, maneuver or hyperspeed.");
        addPersona(Persona.RED_2);
        addIcons(Icon.A_NEW_HOPE, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addModelType(ModelType.X_WING);
        setPilotCapacity(1);
        setAstromechCapacity(1);
        addKeywords(Keyword.RED_SQUADRON);
        setMatchingPilotFilter(Filters.Wedge);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new HasPilotingCondition(self, Filters.Wedge), 3));
        return modifiers;
    }
}
