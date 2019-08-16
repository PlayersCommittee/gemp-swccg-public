package com.gempukku.swccgo.cards.set200.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.PresentCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.evaluators.HereEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 0
 * Type: Character
 * Subtype: Alien
 * Title: Aayla Secura
 */
public class Card200_001 extends AbstractAlien {
    public Card200_001() {
        super(Side.LIGHT, 1, 5, 4, 6, 7, "Aayla Secura", Uniqueness.UNIQUE);
        setLore("Female Twi'lek.");
        setGameText("Power +1 for each opponent's character here. When with two of your aliens (or clones), adds one battle destiny. May not be targeted by weapons unless each of your other aliens and clones present are 'hit'. Immune to You Are Beaten and attrition < 5.");
        addIcons(Icon.WARRIOR, Icon.VIRTUAL_SET_0, Icon.EPISODE_I, Icon.CLONE_ARMY);
        addKeywords(Keyword.FEMALE);
        setSpecies(Species.TWILEK);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition withTwoAliensOrClones = new OrCondition(new WithCondition(self, 2, Filters.and(Filters.your(self), Filters.alien)),
                new WithCondition(self, 2, Filters.and(Filters.your(self), Filters.clone)));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new HereEvaluator(self, Filters.and(Filters.opponents(self), Filters.character))));
        modifiers.add(new AddsBattleDestinyModifier(self, withTwoAliensOrClones, 1));
        modifiers.add(new MayNotBeTargetedByWeaponsModifier(self, new PresentCondition(self, Filters.and(Filters.your(self),
                Filters.other(self), Filters.or(Filters.alien, Filters.clone), Filters.not(Filters.hit)))));
        modifiers.add(new ImmuneToTitleModifier(self, Title.You_Are_Beaten));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 5));
        return modifiers;
    }
}
