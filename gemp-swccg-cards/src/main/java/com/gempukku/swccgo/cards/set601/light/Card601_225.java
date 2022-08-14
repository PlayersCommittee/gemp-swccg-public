package com.gempukku.swccgo.cards.set601.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.ArmedWithCondition;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.evaluators.PowerEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Block 3
 * Subtype: Alien
 * Title: Han (V)
 */
public class Card601_225 extends AbstractAlien {
    public Card601_225() {
        super(Side.LIGHT, 1, 3, 4, 3, 6, "Han", Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("Corellian. Graduated with honors from the Imperial Academy. Dishonorably discharged. Wanders the galaxy building a reputation as a gambler and a hot-shot pilot.");
        setGameText("[Pilot] 3. Force retrieval with Or Be Destroyed is immune to Secret Plans. While armed with a blaster, Han is defense value +2. While Or Be Destroyed on table, immune to attrition < Han's power.");
        addPersona(Persona.HAN);
        addIcons(Icon.PREMIUM, Icon.PILOT, Icon.WARRIOR, Icon.LEGACY_BLOCK_3);
        addKeywords(Keyword.GAMBLER);
        setSpecies(Species.CORELLIAN);
        setAsLegacy(true);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new ForceRetrievalImmuneToSecretPlansModifier(self, Filters.Or_Be_Destroyed));
        modifiers.add(new DefenseValueModifier(self, new ArmedWithCondition(self, Filters.blaster), 2));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new OnTableCondition(self, Filters.Or_Be_Destroyed), new PowerEvaluator(self)));
        return modifiers;
    }
}
