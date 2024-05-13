package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.cards.AbstractCapitalStarship;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Dagobah
 * Type: Starship
 * Subtype: Capital
 * Title: Executor
 */
public class Card4_167 extends AbstractCapitalStarship {
    public Card4_167() {
        super(Side.DARK, 1, 15, 12, 12, null, 2, 15, Title.Executor, Uniqueness.UNIQUE, ExpansionSet.DAGOBAH, Rarity.R);
        setAsHorizontal(true);
        setLore("Flagship of Death Squadron. Over eight kilometers long. Carries 38,000 troops, Can conquer entire star systems by sheer intimidation and terror. Lord Vader's personal command ship.");
        setGameText("May add unlimited pilots, passengers, vehicles and starfighters. Has ship-docking capability. Permanent pilots aboard provide total ability of 3. Immune to attrition < 12.");
        addPersona(Persona.EXECUTOR);
        addIcons(Icon.DAGOBAH, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addIcon(Icon.PILOT, 3);
        addModelType(ModelType.SUPER_CLASS_STAR_DESTROYER);
        addKeywords(Keyword.DEATH_SQUADRON);
        setPilotCapacity(Integer.MAX_VALUE);
        setPassengerCapacity(Integer.MAX_VALUE);
        setVehicleCapacity(Integer.MAX_VALUE);
        setStarfighterCapacity(Integer.MAX_VALUE);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        List<AbstractPermanentAboard> permanentsAboard = new ArrayList<AbstractPermanentAboard>();
        permanentsAboard.add(new AbstractPermanentPilot(1) {});
        permanentsAboard.add(new AbstractPermanentPilot(1) {});
        permanentsAboard.add(new AbstractPermanentPilot(1) {});
        return permanentsAboard;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 12));
        return modifiers;
    }
}
