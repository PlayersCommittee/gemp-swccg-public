package com.gempukku.swccgo.cards.set223.dark;

import com.gempukku.swccgo.cards.AbstractCapitalStarship;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
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
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 23
 * Type: Starship
 * Subtype: Capital
 * Title: Eli Vanto In Dreadnaught
 */
public class Card223_012 extends AbstractCapitalStarship {
    public Card223_012() {
        super(Side.DARK, 1, 4, 5, 6, null, 3, 6, "Eli Vanto In Dreadnaught", Uniqueness.UNIQUE, ExpansionSet.SET_23, Rarity.V);
        setLore("");
        setGameText("May add 2 pilots, and 2 passengers. " +
                "Permanent pilot is •Vanto, who provides ability of 3. " +
                "Immune to attrition < 4 " +
                "(< 6 while piloted by Thrawn or while a 'probed' or 'liberated' system on table).");
        addIcons(Icon.PILOT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_23);
        addModelType(ModelType.DREADNAUGHT_CLASS_HEAVY_CRUISER);
        setMatchingPilotFilter(Filters.Thrawn);
        setPilotCapacity(2);
        setPassengerCapacity(2);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(Persona.VANTO, 3) {
        });
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        HasPilotingCondition hasThrawnPiloting = new HasPilotingCondition(self, Filters.Thrawn);
        OnTableCondition probedOrLiberatedSystemOnTable = new OnTableCondition(self, Filters.or(Filters.liberated_system, Filters.probeCard));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new ConditionEvaluator(4, 6,
                new OrCondition(hasThrawnPiloting, probedOrLiberatedSystemOnTable))));
        return modifiers;
    }
}
