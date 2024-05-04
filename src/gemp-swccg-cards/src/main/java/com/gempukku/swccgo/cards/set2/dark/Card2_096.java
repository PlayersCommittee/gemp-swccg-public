package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.PresentCondition;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: A New Hope
 * Type: Character
 * Subtype: Imperial
 * Title: Lt. Shann Childsen
 */
public class Card2_096 extends AbstractImperial {
    public Card2_096() {
        super(Side.DARK, 2, 2, 1, 1, 3, "Lt. Shann Childsen", Uniqueness.UNIQUE, ExpansionSet.A_NEW_HOPE, Rarity.U1);
        setLore("Demoted after a superior blamed him for a clerical error. Considered a bully by fellow officers. Fanatically supports the New Order doctrine of alien subjugation.");
        setGameText("Power +2 when at Detention Block Corridor or Detention Block Control Room. Also, power +2 when an opponent's alien is present (+3 if alien is a Wookiee, Talz or Ewok).");
        addIcons(Icon.A_NEW_HOPE, Icon.WARRIOR);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new AtCondition(self, Filters.or(Filters.Detention_Block_Corridor, Filters.Detention_Block_Control_Room)), 2));
        modifiers.add(new PowerModifier(self, new PresentCondition(self, Filters.and(Filters.opponents(self), Filters.alien)),
                new ConditionEvaluator(2, 3, new PresentCondition(self, Filters.and(Filters.opponents(self), Filters.or(Filters.Wookiee, Filters.Talz, Filters.Ewok))))));
        return modifiers;
    }
}
