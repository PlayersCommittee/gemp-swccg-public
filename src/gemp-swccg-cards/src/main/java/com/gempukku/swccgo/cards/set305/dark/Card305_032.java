package com.gempukku.swccgo.cards.set305.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.evaluators.PresentInBattleEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.AttritionModifier;
import com.gempukku.swccgo.logic.modifiers.DefenseValueModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: A Better Tomorrow
 * Type: Character
 * Subtype: Alien
 * Title: Baron Sirra Uvam
 */
public class Card305_032 extends AbstractAlien {
    public Card305_032() {
        super(Side.DARK, 2, 3, 2, 3, 4, "Baron Sirra Uvam", Uniqueness.UNIQUE, ExpansionSet.ABT, Rarity.R);
        setLore("A leader of the Vauzem Dominion, Baron Sirra Uvam has been tasked with the invasion and occupation of Quermia. To achieve this goal he's reactivated an old CIS droid factory.");
        setGameText("While at Senate Council Chambers, your attrition against opponent in battles at same and related Quermia sites is +X, where X = number of battle droids present at that site. While with a battle droid, Sirra is power and defense value +2.");
        addPersona(Persona.SIRRA);
        addIcons(Icon.ABT);
        addKeywords(Keyword.LEADER);
        setSpecies(Species.SEPHI);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition withBattleDroid = new WithCondition(self, Filters.battle_droid);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AttritionModifier(self, Filters.and(Filters.Quermia_site, Filters.sameOrRelatedSite(self)),
                new AtCondition(self, Filters.Senate_Council_Chambers), new PresentInBattleEvaluator(self, Filters.battle_droid),
                game.getOpponent(self.getOwner())));
        modifiers.add(new PowerModifier(self, withBattleDroid, 2));
        modifiers.add(new DefenseValueModifier(self, withBattleDroid, 2));
        return modifiers;
    }
}
