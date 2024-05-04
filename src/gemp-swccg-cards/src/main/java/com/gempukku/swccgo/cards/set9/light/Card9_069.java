package com.gempukku.swccgo.cards.set9.light;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.EachWeaponDestinyForWeaponFiredByModifier;
import com.gempukku.swccgo.logic.modifiers.FiresForFreeModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Death Star II
 * Type: Starship
 * Subtype: Starfighter
 * Title: Gray Squadron 1
 */
public class Card9_069 extends AbstractStarfighter {
    public Card9_069() {
        super(Side.LIGHT, 3, 2, 2, null, 3, 4, 3, "Gray Squadron 1", Uniqueness.UNIQUE, ExpansionSet.DEATH_STAR_II, Rarity.U);
        setLore("Y-wing flown by Colonel Salm at the battle of Endor. Ordered to disable capital starships between the Death star and General Calrissian.");
        setGameText("May add 2 pilots or passengers. Ion cannons may fire free aboard. Each of its ion cannon destiny draws is +2. Immune to attrition < 4 when Salm or Kian piloting.");
        addIcons(Icon.DEATH_STAR_II, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addModelType(ModelType.Y_WING);
        addKeywords(Keyword.GRAY_SQUADRON);
        setPilotOrPassengerCapacity(2);
        setMatchingPilotFilter(Filters.or(Filters.Salm, Filters.Kian));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new FiresForFreeModifier(self, Filters.and(Filters.ion_cannon, Filters.attachedTo(self))));
        modifiers.add(new EachWeaponDestinyForWeaponFiredByModifier(self, 2, Filters.ion_cannon));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new HasPilotingCondition(self, Filters.or(Filters.Salm, Filters.Kian)), 4));
        return modifiers;
    }
}
