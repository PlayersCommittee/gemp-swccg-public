package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.conditions.OnCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.InBattleCondition;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.modifiers.AttritionModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Character
 * Subtype: Imperial
 * Title: Sergeant Major Bursk
 */
public class Card7_202 extends AbstractImperial {
    public Card7_202() {
        super(Side.DARK, 3, 2, 3, 2, 3, "Sergeant Major Bursk", Uniqueness.UNIQUE, ExpansionSet.SPECIAL_EDITION, Rarity.U);
        setLore("Leader of most brutal snowtrooper platoon in the battle of Hoth. Cunning planner. Coordinates attacks and manages troop movements.");
        setGameText("When in a battle on Hoth, immune to attrition < 3 and adds 2 to attrition against opponent. Your snowtroopers are deploy -1 to same Hoth site. Power -1 when not on Hoth.");
        addIcons(Icon.SPECIAL_EDITION, Icon.WARRIOR);
        addKeywords(Keyword.LEADER, Keyword.SNOWTROOPER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition inBattleOnHothCondition = new AndCondition(new InBattleCondition(self), new OnCondition(self, Title.Hoth));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, inBattleOnHothCondition, 3));
        modifiers.add(new AttritionModifier(self, inBattleOnHothCondition, 2, game.getOpponent(self.getOwner())));
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.your(self), Filters.not(self), Filters.snowtrooper),
                -1, Filters.and(Filters.sameSite(self), Filters.Hoth_site)));
        modifiers.add(new PowerModifier(self, new NotCondition(new OnCondition(self, Title.Hoth)), -1));
        return modifiers;
    }
}
