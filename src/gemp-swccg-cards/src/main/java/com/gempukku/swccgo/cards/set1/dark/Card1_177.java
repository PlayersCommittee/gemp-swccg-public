package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.AtSameLocationAsCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.PhaseCondition;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MovesFreeToLocationModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premiere
 * Type: Character
 * Subtype: Alien
 * Title: Garindan
 */
public class Card1_177 extends AbstractAlien {
    public Card1_177() {
        super(Side.DARK, 4, 2, 1, 1, 3, "Garindan", Uniqueness.UNIQUE, ExpansionSet.PREMIERE, Rarity.R2);
        setLore("Long-nosed, male Kubaz from Kubindi. Spy. Squealed on Obi-Wan and Luke outside Docking Bay 94. Works for Jabba the Hutt or the highest bidder. Not particularly brave.");
        setGameText("When at same location as a Rebel of ability > 2, deploy -1 there for any Imperials, and during your move phase, Imperials at an adjacent site may move there for free.");
        addKeywords(Keyword.SPY);
        setSpecies(Species.KUBAZ);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition atSameLocationAsRebelOfAbilityMoreThan2 = new AtSameLocationAsCondition(self, Filters.and(Filters.Rebel, Filters.abilityMoreThan(2)));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, Filters.Imperial, atSameLocationAsRebelOfAbilityMoreThan2,
                -1, Filters.sameLocation(self)));
        modifiers.add(new MovesFreeToLocationModifier(self, Filters.and(Filters.Imperial, Filters.at(Filters.adjacentSite(self))),
                new AndCondition(atSameLocationAsRebelOfAbilityMoreThan2, new PhaseCondition(Phase.MOVE, self.getOwner())),
                Filters.sameLocation(self)));
        return modifiers;
    }
}
