package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.evaluators.PresentEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Dagobah
 * Type: Character
 * Subtype: Imperial
 * Title: Lieutenant Commander Ardan
 */
public class Card4_103 extends AbstractImperial {
    public Card4_103() {
        super(Side.DARK, 3, 3, 2, 3, 3, "Lieutenant Commander Ardan", Uniqueness.UNIQUE, ExpansionSet.DAGOBAH, Rarity.U);
        setLore("Commander of Executor bridge pit crews. Native of Brentaal. Participated in the subjugation of Ithor, Kashyyyk, Firro, Sinton and other planets populated by non-humans.");
        setGameText("If at a site, draws one battle destiny if not able to otherwise. Immune to attrition < X, where X = the number of opponent's aliens present.");
        addIcons(Icon.DAGOBAH, Icon.WARRIOR);
        addKeywords(Keyword.COMMANDER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, new AtCondition(self, Filters.site), 1));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new PresentEvaluator(self, Filters.and(Filters.opponents(self), Filters.alien))));
        return modifiers;
    }
}
