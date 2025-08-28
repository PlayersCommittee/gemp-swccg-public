package com.gempukku.swccgo.cards.set225.dark;

import com.gempukku.swccgo.cards.AbstractCapitalStarship;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.conditions.HasAboardCondition;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 25
 * Type: Starship
 * Subtype: Capital
 * Title: Supremacy
 */
public class Card225_027 extends AbstractCapitalStarship {
    public Card225_027() {
        super(Side.DARK, 1, 16, 13, 10, null, 2, 16, Title.Supremacy, Uniqueness.UNIQUE, ExpansionSet.SET_25, Rarity.V);
        setGameText("May add unlimited pilots, passengers, starfighters, and vehicles. Permanent pilot provides ability of 4. Immune to attrition < 10 (< 12 while Snoke aboard). While Tracked Fleet here, immune to attrition.");
        addPersona(Persona.SUPREMACY);
        addIcons(Icon.SCOMP_LINK, Icon.EPISODE_VII, Icon.FIRST_ORDER, Icon.NAV_COMPUTER, Icon.VIRTUAL_SET_25);
        addIcon(Icon.PILOT, 1);
        addModelType(ModelType.MEGA_CLASS_DREADNAUGHT);
        setPilotCapacity(Integer.MAX_VALUE);
        setPassengerCapacity(Integer.MAX_VALUE);
        setStarfighterCapacity(Integer.MAX_VALUE);
        setVehicleCapacity(Integer.MAX_VALUE);
        setMatchingPilotFilter(Filters.Snoke);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        List<AbstractPermanentAboard> permanentsAboard = new ArrayList<>();
        permanentsAboard.add(new AbstractPermanentPilot(4) {});
        return permanentsAboard;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();

        Condition snokeAboard = new HasAboardCondition(self, Filters.Snoke);
        Condition trackedFleetHere = new HereCondition(self, Filters.Tracked_Fleet);

        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new ConditionEvaluator(10, 12, snokeAboard)));
        modifiers.add(new ImmuneToAttritionModifier(self, trackedFleetHere));
        return modifiers;
    }
}
