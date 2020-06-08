package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.PresentWithCondition;
import com.gempukku.swccgo.cards.evaluators.ForceIconsPresentEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ResetInitiateBattleCostModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premiere
 * Type: Character
 * Subtype: Alien
 * Title: Feltipern Trevagg
 */
public class Card1_176 extends AbstractAlien {
    public Card1_176() {
        super(Side.DARK, 4, 2, 2, 1, 3, Title.Feltipern_Trevagg, Uniqueness.UNIQUE);
        setLore("Corrupt tax collector. A male Gotal. Bounty hunter. Once sought a bounty on Obi-Wan. Has limited sensing ability to droid emanations. Romanced M'iiyoom Onith.");
        setGameText("While no droid present with Trevagg, to initiate battles at same location as Trevagg, player must use X Force, where X = total number of [Dark Side Force] and [Light Side Force] present.");
        addIcons(Icon.WARRIOR);
        addKeywords(Keyword.BOUNTY_HUNTER, Keyword.TAX_COLLECTOR);
        setSpecies(Species.GOTAL);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ResetInitiateBattleCostModifier(self, Filters.sameLocation(self),
                new NotCondition(new PresentWithCondition(self, Filters.droid)), new ForceIconsPresentEvaluator(self, true, true)));
        return modifiers;
    }
}
