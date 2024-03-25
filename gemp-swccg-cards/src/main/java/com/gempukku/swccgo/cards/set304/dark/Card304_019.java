package com.gempukku.swccgo.cards.set304.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.cards.evaluators.PerThranGuardEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.modifiers.DefenseValueModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotCancelBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotSubstituteBattleDestinyModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: The Great Hutt Expansion
 * Type: Character
 * Subtype: Imperial
 * Title: Thran Occasus, Usurper
 */
public class Card304_019 extends AbstractImperial {
    public Card304_019() {
        super(Side.DARK, 1, 6, 5, 6, 7, "Thran Occasus, Usurper", Uniqueness.UNIQUE, ExpansionSet.GREAT_HUTT_EXPANSION, Rarity.V);
        setLore("Born as Derc Kast on Bakura, Following Thran's first tenure as Emperor of Scholae Palatinae he has schemed to return to the throne. As Proconsul it is known that he will usurp Kamjin one day.");
        setGameText("Adds 3 to anything he pilots. Adds 2 to power, 3 to defense value, and 3 to forfeit of each Thran Personal Guard member at same and related locations. While with Kamjin (or while piloting I.S.N. Palpatine), opponent may not cancel or substitute battle destiny draws here.");
        addIcons(Icon.CSP, Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.LEADER);
		addPersona(Persona.THRAN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter yourThranGuardAtSameAndRelatedLocations = Filters.and(Filters.your(self), Filters.THRAN_GUARD, Filters.atSameOrRelatedLocation(self));
		String opponent = game.getOpponent(self.getOwner());
		Condition pilotingPalpatineOrWithKamjin = new OrCondition(new PilotingCondition(self, Filters.title("I.S.N. Palpatine")), new WithCondition(self, Filters.Kamjin));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new PowerModifier(self, yourThranGuardAtSameAndRelatedLocations, new PerThranGuardEvaluator(2)));
        modifiers.add(new ForfeitModifier(self, yourThranGuardAtSameAndRelatedLocations, new PerThranGuardEvaluator(3)));
		modifiers.add(new DefenseValueModifier(self, yourThranGuardAtSameAndRelatedLocations, new PerThranGuardEvaluator(3)));
        modifiers.add(new MayNotCancelBattleDestinyModifier(self, Filters.here(self), pilotingPalpatineOrWithKamjin, opponent));
        modifiers.add(new MayNotSubstituteBattleDestinyModifier(self, Filters.here(self), pilotingPalpatineOrWithKamjin, opponent));
        return modifiers;
    }
}