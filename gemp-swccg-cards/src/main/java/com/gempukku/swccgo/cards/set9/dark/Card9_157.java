package com.gempukku.swccgo.cards.set9.dark;

import com.gempukku.swccgo.cards.AbstractCapitalStarship;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.conditions.HasAttachedCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Death Star II
 * Type: Starship
 * Subtype: Capital
 * Title: Flagship Executor
 */
public class Card9_157 extends AbstractCapitalStarship {
    public Card9_157() {
        super(Side.DARK, 4, 12, 12, 12, null, 2, 15, "Flagship Executor", Uniqueness.UNIQUE);
        setLore("Command ship of the Imperial Fleet at the Battle of Endor. Originally constructed at the Fondor shipyards. Admiral Piett stationed aboard.");
        setGameText("May add unlimited pilots, passengers and starfighters. Has ship-docking capability. Permanent pilot provides ability of 3. Immune to attrition if target of Flagship Operations.");
        addPersona(Persona.EXECUTOR);
        addIcons(Icon.DEATH_STAR_II, Icon.PILOT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addModelType(ModelType.SUPER_CLASS_STAR_DESTROYER);
        setPilotCapacity(Integer.MAX_VALUE);
        setPassengerCapacity(Integer.MAX_VALUE);
        setStarfighterCapacity(Integer.MAX_VALUE);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(3) {});
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionModifier(self, new HasAttachedCondition(self, Filters.Flagship_Operations)));
        return modifiers;
    }
}
