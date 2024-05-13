package com.gempukku.swccgo.cards.set212.dark;

import com.gempukku.swccgo.cards.AbstractFirstOrder;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.GameTextModificationCondition;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.evaluators.HereEvaluator;
import com.gempukku.swccgo.cards.evaluators.MaxLimitEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.AttritionModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 12
 * Type: Character
 * Subtype: First Order
 * Title: Allegiant General Pryde
 */
public class Card212_006 extends AbstractFirstOrder {
    public Card212_006() {
        super(Side.DARK, 3, 3, 3, 3, 5, "Allegiant General Pryde", Uniqueness.UNIQUE, ExpansionSet.SET_12, Rarity.V);
        setLore("Leader.");
        setGameText("[Pilot] 2. Hux is forfeit -3. While with a Resistance character, adds one battle destiny. While Emperor on table, attrition against opponent here is +1 for each First Order character here (limit +3).");
        addPersona(Persona.PRYDE);
        addIcons(Icon.EPISODE_VII, Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_12);
        addKeywords(Keyword.LEADER, Keyword.GENERAL);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new AddsBattleDestinyModifier(self, new WithCondition(self, Filters.and(Filters.opponents(self.getOwner()), Filters.Resistance_character)), 1));
        modifiers.add(new AttritionModifier(self, Filters.here(self), new AndCondition(new OnTableCondition(self, Filters.Emperor),
                new NotCondition(new AndCondition(new GameTextModificationCondition(self, ModifyGameTextType.PRYDE__DOES_NOT_ADD_ATTRITION_AT_JAKKU_SYSTEM), new AtCondition(self, Filters.Jakku_system)))),
                new MaxLimitEvaluator(new HereEvaluator(self, Filters.First_Order_character), 3), game.getOpponent(self.getOwner())));
        modifiers.add(new ForfeitModifier(self, Filters.Hux, -3));
        return modifiers;
    }
}
