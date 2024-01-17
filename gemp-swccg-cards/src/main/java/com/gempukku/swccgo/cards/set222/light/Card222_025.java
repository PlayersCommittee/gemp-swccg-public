package com.gempukku.swccgo.cards.set222.light;

import com.gempukku.swccgo.cards.AbstractAlienRebel;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DefenseValueModifier;
import com.gempukku.swccgo.logic.modifiers.ImmunityToAttritionLimitedToModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 22
 * Type: Character
 * Subtype: Rebel
 * Title: Nien Nunb, Sullustan Smuggler
 */
public class Card222_025 extends AbstractAlienRebel {
    public Card222_025() {
        super(Side.LIGHT, 2, 2, 2, 2, 4, "Nien Nunb, Sullustan Smuggler", Uniqueness.UNIQUE, ExpansionSet.SET_22, Rarity.V);
        setLore("Brilliant navigator. Former SoroSuub employee. Turned to pirating when that corporation backed the Empire. Tall for a Sullustan. Smuggler.");
        setGameText("Adds 2 to power of anything he pilots. While piloting a freighter, " +
                "it is defense value +2 and opponent’s immunity to attrition is limited to < 7 here (< 5 if Lando piloting here).");
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_22);
        addPersona(Persona.NIEN_NUNB);
        addKeyword(Keyword.SMUGGLER);
        setSpecies(Species.SULLUSTAN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        PilotingCondition pilotingFreighterCondition = new PilotingCondition(self, Filters.freighter);
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new DefenseValueModifier(self, Filters.hasPiloting(self), pilotingFreighterCondition, 2));
        modifiers.add(new ImmunityToAttritionLimitedToModifier(self, Filters.and(Filters.opponents(self.getOwner()), Filters.here(self)), pilotingFreighterCondition,
                new ConditionEvaluator(7, 5, new HereCondition(self, Filters.hasPiloting(self, Filters.Lando)))));
        return modifiers;
    }
}
