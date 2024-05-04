package com.gempukku.swccgo.cards.set14.dark;

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
 * Set: Theed Palace
 * Type: Character
 * Subtype: Republic
 * Title: Rune Haako, Legal Counsel
 */
public class Card14_086 extends AbstractRepublic {
    public Card14_086() {
        super(Side.DARK, 2, 3, 2, 3, 4, "Rune Haako, Legal Counsel", Uniqueness.UNIQUE, ExpansionSet.THEED_PALACE, Rarity.R);
        setLore("The Trade Federation's only Neimoidian leader to have ever encountered a Jedi Knight. Assumed Daultay Dofine's responsibilities after Dofine questioned their Sith Lord's plans.");
        setGameText("While at Theed Palace Throne Room, your attrition against opponent in battles at same and related Naboo sites is +X, where X = number of battle droids present at that site. While with a battle droid, Haako is power and defense value +2.");
        addPersona(Persona.HAAKO);
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I);
        addKeywords(Keyword.LEADER);
        setSpecies(Species.NEIMOIDIAN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition withBattleDroid = new WithCondition(self, Filters.battle_droid);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AttritionModifier(self, Filters.and(Filters.Naboo_site, Filters.sameOrRelatedSite(self)),
                new AtCondition(self, Filters.Theed_Palace_Throne_Room), new PresentInBattleEvaluator(self, Filters.battle_droid),
                game.getOpponent(self.getOwner())));
        modifiers.add(new PowerModifier(self, withBattleDroid, 2));
        modifiers.add(new DefenseValueModifier(self, withBattleDroid, 2));
        return modifiers;
    }
}
