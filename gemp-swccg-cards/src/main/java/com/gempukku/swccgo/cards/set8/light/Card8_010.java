package com.gempukku.swccgo.cards.set8.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.conditions.OnCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Endor
 * Type: Character
 * Subtype: Rebel
 * Title: Endor Scout Trooper
 */
public class Card8_010 extends AbstractRebel {
    public Card8_010() {
        super(Side.LIGHT, 3, 2, 2, 1, 3, "Endor Scout Trooper");
        setLore("Rebel troopers receive Scout training that helps them to survive combat in harsh environments. Often protect 'specialists' while they accomplish mission tasks.");
        setGameText("Power -1 while not on Endor. While Endor Scout Trooper is on Endor, Lieutenant Page and each of your Rebel scouts of ability < 3 at same and adjacent exterior sites are immune to attrition < 2.");
        addIcons(Icon.ENDOR, Icon.WARRIOR);
        addKeywords(Keyword.TROOPER, Keyword.SCOUT);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition onEndor = new OnCondition(self, Title.Endor);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new NotCondition(onEndor), -1));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, Filters.and(Filters.or(Filters.Lieutenant_Page,
                Filters.and(Filters.your(self), Filters.Rebel_scout, Filters.abilityLessThan(3))),
                Filters.at(Filters.and(Filters.exterior_site, Filters.sameOrAdjacentSite(self)))), onEndor, 2));
        return modifiers;
    }
}
