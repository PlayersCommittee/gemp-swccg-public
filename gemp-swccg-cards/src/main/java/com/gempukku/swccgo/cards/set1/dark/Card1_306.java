package com.gempukku.swccgo.cards.set1.dark;

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
 * Title: Vader's Custom TIE
 */
public class Card1_306 extends AbstractStarfighter {
    public Card1_306() {
        super(Side.DARK, 2, 2, 2, null, 3, 2, 4, "Vader's Custom TIE", Uniqueness.UNIQUE, ExpansionSet.PREMIERE, Rarity.R1);
        setLore("TIE advanced x1 prototype. First of a limited production run leading to the development of the TIE Interceptor. At Vader's insistence a hyperdrive was installed.");
        setGameText("May add 1 pilot. If Vader is pilot, Custom TIE is immune to attrition < 4. Must have pilot aboard to use power, maneuver or hyperspeed.");
        addPersona(Persona.VADERS_CUSTOM_TIE);
        addIcons(Icon.NAV_COMPUTER);
        addModelType(ModelType.TIE_ADVANCED_X1);
        setPilotCapacity(1);
        setMatchingPilotFilter(Filters.Vader);
        addKeywords(Keyword.BLACK_SQUADRON);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new HasPilotingCondition(self, Filters.Vader), 4));
        return modifiers;
    }
}
