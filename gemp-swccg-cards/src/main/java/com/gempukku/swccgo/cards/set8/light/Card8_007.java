package com.gempukku.swccgo.cards.set8.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.conditions.PresentAtCondition;
import com.gempukku.swccgo.cards.conditions.PresentWithCondition;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Endor
 * Type: Character
 * Subtype: Rebel
 * Title: Corporal Kensaric
 */
public class Card8_007 extends AbstractRebel {
    public Card8_007() {
        super(Side.LIGHT, 2, 2, 2, 2, 4, "Corporal Kensaric", Uniqueness.UNIQUE, ExpansionSet.ENDOR, Rarity.R);
        setLore("Scout responsible for covering the tracks of General Solo's strike team. Served under Bren Derlin during the Battle of Hoth.");
        setGameText("When present at an exterior battleground site with your other scout, adds 2 (or 1 if Goo Nee Tay on table) to deploy cost of opponent's characters, vehicles and starships to same site.");
        addIcons(Icon.ENDOR, Icon.WARRIOR);
        addKeywords(Keyword.SCOUT);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.opponents(self), Filters.or(Filters.character, Filters.vehicle, Filters.starship)),
                new AndCondition(new PresentAtCondition(self, Filters.exterior_battleground_site),
                        new PresentWithCondition(self, Filters.and(Filters.your(self), Filters.other(self), Filters.scout))),
                new ConditionEvaluator(2, 1, new OnTableCondition(self, Filters.Goo_Nee_Tay)), Filters.sameSite(self)));
        return modifiers;
    }
}
