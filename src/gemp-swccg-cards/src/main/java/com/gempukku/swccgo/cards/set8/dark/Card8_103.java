package com.gempukku.swccgo.cards.set8.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.conditions.PresentWithCondition;
import com.gempukku.swccgo.cards.evaluators.PresentEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.modifiers.UsedInterruptModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Endor
 * Type: Character
 * Subtype: Imperial
 * Title: Lieutenant Renz
 */
public class Card8_103 extends AbstractImperial {
    public Card8_103() {
        super(Side.DARK, 2, 2, 2, 2, 4, "Lieutenant Renz", Uniqueness.UNIQUE, ExpansionSet.ENDOR, Rarity.R);
        setLore("Leader of one of the Emperor's finest legions of troops. His command was placed at the disposal of Colonel Dyer. Always on the lookout for Rebel activity.");
        setGameText("Power +2 while present with a stormtrooper. You Rebel Scum targeting a Rebel at same site is a Used Interrupt and is immune to Sense. Immune to attrition < number of stormtroopers present.");
        addIcons(Icon.ENDOR, Icon.WARRIOR);
        addKeywords(Keyword.LEADER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter youRebelScum = Filters.and(Filters.You_Rebel_Scum, Filters.cardBeingPlayedTargeting(self, Filters.and(Filters.Rebel, Filters.atSameSite(self))));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new PresentWithCondition(self, Filters.stormtrooper), 2));
        modifiers.add(new UsedInterruptModifier(self, youRebelScum));
        modifiers.add(new ImmuneToTitleModifier(self, youRebelScum, Title.Sense));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new PresentEvaluator(self, Filters.stormtrooper)));
        return modifiers;
    }
}
