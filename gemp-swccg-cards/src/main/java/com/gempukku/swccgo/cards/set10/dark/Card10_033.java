package com.gempukku.swccgo.cards.set10.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.conditions.AboardCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.evaluators.CardMatchesEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ArmorModifier;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.HyperspeedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Reflections II
 * Type: Character
 * Subtype: Imperial
 * Title: Captain Gilad Pellaeon
 */
public class Card10_033 extends AbstractImperial {
    public Card10_033() {
        super(Side.DARK, 2, 3, 3, 3, 5, "Captain Gilad Pellaeon", Uniqueness.UNIQUE, ExpansionSet.REFLECTIONS_II, Rarity.PM);
        setLore("Corellian leader. Lied about his age to enter the Academy. Took command of Chimaera when its captain was killed at the Battle of Endor. Admires Grand Admiral Thrawn.");
        setGameText("Deploys for free to Thrawn's location. Each other Imperial aboard same capital starship is forfeit +2. Adds 2 to power of any capital starship he pilots (3 if Chimaera). While aboard Chimaera with Thrawn, also adds 2 to that starship's armor and hyperspeed.");
        addIcons(Icon.REFLECTIONS_II, Icon.PILOT);
        addKeywords(Keyword.LEADER, Keyword.CAPTAIN);
        setSpecies(Species.CORELLIAN);
        setMatchingStarshipFilter(Filters.Chimaera);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeploysFreeToLocationModifier(self, Filters.sameLocationAs(self, Filters.Thrawn)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition aboardChimaeraWithThrawn = new AndCondition(new AboardCondition(self, Filters.Chimaera), new WithCondition(self, Filters.Thrawn));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, new CardMatchesEvaluator(2, 3, Filters.Chimaera), Filters.capital_starship));
        modifiers.add(new ForfeitModifier(self, Filters.and(Filters.other(self), Filters.Imperial,
                Filters.aboard(Filters.and(Filters.capital_starship, Filters.hasAboard(self)))), 2));
        modifiers.add(new ArmorModifier(self, Filters.Chimaera, aboardChimaeraWithThrawn, 2));
        modifiers.add(new HyperspeedModifier(self, Filters.Chimaera, aboardChimaeraWithThrawn, 2));
        return modifiers;
    }
}
