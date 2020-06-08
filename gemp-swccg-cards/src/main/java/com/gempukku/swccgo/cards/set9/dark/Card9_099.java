package com.gempukku.swccgo.cards.set9.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.cards.evaluators.InBattleEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Death Star II
 * Type: Character
 * Subtype: Imperial
 * Title: Baron Soontir Fel
 */
public class Card9_099 extends AbstractImperial {
    public Card9_099() {
        super(Side.DARK, 1, 3, 2, 3, 5, Title.Fel, Uniqueness.UNIQUE);
        setLore("Corellian Baron. Leader of famed 181st Imperial Fighter Wing. Taught at the Imperial Academy on Prefsbelt IV. Instructed Biggs Darklighter.");
        setGameText("Adds 3 to power of anything he pilots. When piloting Saber 1, adds one battle destiny and 2 to maneuver. Adds 1 to your total battle destiny for each of your piloted TIE Interceptors in same battle.");
        addIcons(Icon.DEATH_STAR_II, Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.LEADER, Keyword.SABER_SQUADRON);
        setSpecies(Species.CORELLIAN);
        setMatchingStarshipFilter(Filters.Saber_1);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition pilotingSaber1 = new PilotingCondition(self, Filters.Saber_1);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new AddsBattleDestinyModifier(self, pilotingSaber1, 1));
        modifiers.add(new ManeuverModifier(self, Filters.hasPiloting(self), pilotingSaber1, 2));
        modifiers.add(new TotalBattleDestinyModifier(self, new InBattleEvaluator(self, Filters.and(Filters.your(self),
                Filters.piloted, Filters.TIE_Interceptor)), self.getOwner()));
        return modifiers;
    }
}
