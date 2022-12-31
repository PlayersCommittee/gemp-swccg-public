package com.gempukku.swccgo.cards.set9.light;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
import com.gempukku.swccgo.cards.conditions.PresentCondition;
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
import com.gempukku.swccgo.logic.modifiers.ManeuverModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Death Star II
 * Type: Starship
 * Subtype: Starfighter
 * Title: Green Squadron 3
 */
public class Card9_072 extends AbstractStarfighter {
    public Card9_072() {
        super(Side.LIGHT, 3, 2, 3, null, 5, 4, 3, "Green Squadron 3", Uniqueness.UNIQUE, ExpansionSet.DEATH_STAR_II, Rarity.R);
        setLore("Flown by Tycho Celchu at the Battle of Endor. Modified canopy improves pilot vision in tight confines. Assigned to fly top cover for Millennium Falcon.");
        setGameText("May add 1 pilot. Power -1 when opponent has a starfighter present with higher maneuver. Maneuver +2 at a Death Star II sector. Immune to attrition < 4 when Tycho Celchu piloting.");
        addPersona(Persona.GREEN_SQUADRON_3);
        addIcons(Icon.DEATH_STAR_II, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addModelType(ModelType.A_WING);
        addKeywords(Keyword.GREEN_SQUADRON);
        setPilotCapacity(1);
        setMatchingPilotFilter(Filters.Tycho);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new PresentCondition(self, Filters.and(Filters.opponents(self),
                Filters.starfighter, Filters.maneuverHigherThanManeuverOf(self))), -1));
        modifiers.add(new ManeuverModifier(self, new AtCondition(self, Filters.Death_Star_II_sector), 2));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new HasPilotingCondition(self, Filters.Tycho), 4));
        return modifiers;
    }
}
