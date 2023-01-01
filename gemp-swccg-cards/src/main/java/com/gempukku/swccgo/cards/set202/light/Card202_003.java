package com.gempukku.swccgo.cards.set202.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.evaluators.CardMatchesEvaluator;
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
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.ManeuverModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalBattleDestinyModifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 2
 * Type: Character
 * Subtype: Rebel
 * Title: Wedge Antilles (V)
 */
public class Card202_003 extends AbstractRebel {
    public Card202_003() {
        super(Side.LIGHT, 4, 2, 2, 2, 6, "Wedge Antilles", Uniqueness.UNIQUE, ExpansionSet.SET_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("Highly decorated Corellian. Piloted Red 2 at the Battle of Yavin. A wealthy orphan, he bought a freighter with his inheritance. First joined the Alliance as a weapons smuggler.");
        setGameText("[Pilot] 3. While piloting an [Independent] starfighter, it is maneuver +1 (+2 if Pulsar Skate) and Wedge draws one battle destiny if unable to otherwise. While with Booster or Mirax, your total battle destiny here is +1.");
        addPersona(Persona.WEDGE);
        addIcons(Icon.A_NEW_HOPE, Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_2);
        addKeywords(Keyword.SMUGGLER);
        setSpecies(Species.CORELLIAN);
        setMatchingStarshipFilter(Filters.Pulsar_Skate);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        Condition pilotingIndependentStarfighter = new PilotingCondition(self, Filters.and(Icon.INDEPENDENT, Filters.starfighter));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new ManeuverModifier(self, Filters.hasPiloting(self), pilotingIndependentStarfighter, new CardMatchesEvaluator(1, 2, Filters.Pulsar_Skate)));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, pilotingIndependentStarfighter, 1));
        modifiers.add(new TotalBattleDestinyModifier(self, Filters.here(self), new WithCondition(self, Filters.or(Filters.Booster, Filters.Mirax)), 1, playerId));
        return modifiers;
    }
}
