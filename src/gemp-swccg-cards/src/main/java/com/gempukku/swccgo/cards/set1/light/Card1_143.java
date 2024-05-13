package com.gempukku.swccgo.cards.set1.light;

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
 * Set: Premiere
 * Type: Starship
 * Subtype: Starfighter
 * Title: Millennium Falcon
 */
public class Card1_143 extends AbstractStarfighter {
    public Card1_143() {
        super(Side.LIGHT, 2, 3, 3, null, 4, 6, 7, "Millennium Falcon", Uniqueness.UNIQUE, ExpansionSet.PREMIERE, Rarity.R1);
        setLore("Modified YT-1300 freighter. Owned by Lando Calrissian until won by Han in a sabacc game. 26.7 meters long. 'She may not look like much, but she's got it where it counts.'");
        setGameText("May add 2 pilots and 2 passengers. Immune to attrition < 5 if Han, Chewie or Lando piloting. Has ship-docking capability. Must have pilot aboard to use power, maneuver or hyperspeed.");
        addPersona(Persona.FALCON);
        addIcons(Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addModelType(ModelType.MODIFIED_LIGHT_FREIGHTER);
        addKeywords(Keyword.SHIP_DOCKING_CAPABILITY);
        setPilotCapacity(2);
        setPassengerCapacity(2);
        setMatchingPilotFilter(Filters.or(Filters.Han, Filters.Chewie, Filters.Lando));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new HasPilotingCondition(self, Filters.or(Filters.Han, Filters.Chewie, Filters.Lando)), 5));
        return modifiers;
    }
}
