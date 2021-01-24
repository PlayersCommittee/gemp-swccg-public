package com.gempukku.swccgo.cards.set501.dark;

import com.gempukku.swccgo.cards.AbstractFirstOrder;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.evaluators.HereEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 12
 * Type: Character
 * Subtype: First Order
 * Title: Allegiant General Pryde
 */
public class Card501_011 extends AbstractFirstOrder {
    public Card501_011() {
        super(Side.DARK, 3, 3, 3, 3, 5, "Allegiant General Pryde", Uniqueness.UNIQUE);
        setLore("Leader");
        setGameText("[Pilot] 2. Hux is forfeit -3. While with a Resistance character, adds one battle destiny. If Emperor on table, your total attrition against opponent here is +1 for each First Order character here.");
        addPersona(Persona.PRYDE);
        addIcons(Icon.EPISODE_VII, Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_12);
        addKeywords(Keyword.LEADER, Keyword.GENERAL);
        setTestingText("Allegiant General Pryde (ERRATA)");
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new AddsBattleDestinyModifier(self, new WithCondition(self, Filters.and(Filters.opponents(self.getOwner()), Filters.Resistance_character)), 1));
        modifiers.add(new AttritionModifier(self, Filters.here(self), new OnTableCondition(self, Filters.Emperor), new HereEvaluator(self, Filters.First_Order_character), game.getOpponent(self.getOwner())));
        modifiers.add(new ForfeitModifier(self, Filters.Hux, -3));
        return modifiers;
    }
}
