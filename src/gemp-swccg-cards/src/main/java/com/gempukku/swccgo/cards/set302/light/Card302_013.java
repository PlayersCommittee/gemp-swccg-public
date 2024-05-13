package com.gempukku.swccgo.cards.set302.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.PresentCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.evaluators.HereEvaluator;
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
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTargetedByWeaponsModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Dark Jedi Brotherhood Core
 * Type: Character
 * Subtype: Alien
 * Title: Rajhin Cindertail
 */
public class Card302_013 extends AbstractAlien {
    public Card302_013() {
        super(Side.LIGHT, 1, 5, 4, 6, 7, "Rajhin Cindertail", Uniqueness.UNIQUE, ExpansionSet.DJB_CORE, Rarity.V);
        setLore("Rajhin Cindertail is a Togorian male Force Disciple Adept currently serving on the Dark Council as Fist of the Brotherhood.");
        setGameText("Power +1 for each opponent's character here. When with two of your aliens, adds one battle destiny. May not be targeted by weapons unless each of your other aliens present are 'hit'. Immune to You Are Beaten and attrition < 5.");
		addIcons(Icon.WARRIOR);
        addKeywords(Keyword.LEADER, Keyword.COUNCILOR);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition withTwoAliens = new OrCondition(new WithCondition(self, 3, Filters.and(Filters.your(self), Filters.alien)),
                new WithCondition(self, 3, Filters.and(Filters.your(self))));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new HereEvaluator(self, Filters.and(Filters.opponents(self), Filters.character))));
        modifiers.add(new AddsBattleDestinyModifier(self, withTwoAliens, 1));
        modifiers.add(new MayNotBeTargetedByWeaponsModifier(self, new PresentCondition(self, Filters.and(Filters.your(self),
                Filters.other(self), Filters.or(Filters.alien), Filters.not(Filters.hit)))));
        modifiers.add(new ImmuneToTitleModifier(self, Title.You_Are_Beaten));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 5));
        return modifiers;
    }
}
