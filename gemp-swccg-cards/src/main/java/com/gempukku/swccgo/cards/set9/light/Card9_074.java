package com.gempukku.swccgo.cards.set9.light;

import com.gempukku.swccgo.cards.AbstractCapitalStarship;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
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
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Death Star II
 * Type: Starship
 * Subtype: Capital
 * Title: Home One
 */
public class Card9_074 extends AbstractCapitalStarship {
    public Card9_074() {
        super(Side.LIGHT, 1, 12, 9, 8, null, 3, 12, "Home One", Uniqueness.UNIQUE, ExpansionSet.DEATH_STAR_II, Rarity.R);
        setLore("Command ship of the Rebel fleet. 1200 meters long. Masterpiece of aesthetic form. Most heavily armed and armored ship in the fleet. Admiral Ackbar's personal flagship.");
        setGameText("May add unlimited pilots, passengers, vehicles and starfighters. Has ship-docking capability. Permanent pilots provide total ability of 4. Immune to attrition < 8 (< 10 when Ackbar piloting).");
        addPersona(Persona.HOME_ONE);
        addIcons(Icon.DEATH_STAR_II, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addIcon(Icon.PILOT, 2);
        addModelType(ModelType.MON_CALAMARI_STAR_CRUISER);
        setPilotCapacity(Integer.MAX_VALUE);
        setPassengerCapacity(Integer.MAX_VALUE);
        setVehicleCapacity(Integer.MAX_VALUE);
        setStarfighterCapacity(Integer.MAX_VALUE);
        setMatchingPilotFilter(Filters.Ackbar);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        List<AbstractPermanentAboard> permanentsAboard = new ArrayList<AbstractPermanentAboard>();
        permanentsAboard.add(new AbstractPermanentPilot(2) {});
        permanentsAboard.add(new AbstractPermanentPilot(2) {});
        return permanentsAboard;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new ConditionEvaluator(8, 10, new HasPilotingCondition(self, Filters.Ackbar))));
        return modifiers;
    }
}
