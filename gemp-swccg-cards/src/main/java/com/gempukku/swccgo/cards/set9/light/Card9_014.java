package com.gempukku.swccgo.cards.set9.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.evaluators.PerAwingEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
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

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Death Star II
 * Type: Character
 * Subtype: Rebel
 * Title: General Walex Blissex
 */
public class Card9_014 extends AbstractRebel {
    public Card9_014() {
        super(Side.LIGHT, 3, 2, 1, 2, 3, "General Walex Blissex", Uniqueness.UNIQUE, ExpansionSet.DEATH_STAR_II, Rarity.U);
        setLore("Engineer who worked with Jan Dodonna to design the A-wing starfighter. Given honorary rank due to his service to the Rebellion.");
        setGameText("Deploys -2 aboard your Star Cruiser or to same location as your Admiral. Adds 1 to the power of anything he pilots. Adds 1 to the power and forfeit of each of your A-wings at same and related locations.");
        addIcons(Icon.DEATH_STAR_II, Icon.PILOT);
        addKeywords(Keyword.GENERAL);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        Filter yourStarCruiser = Filters.and(Filters.your(self), Filters.Star_Cruiser);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToTargetModifier(self, -2, Filters.or(yourStarCruiser,
                Filters.locationAndCardsAtLocation(Filters.or(Filters.siteOfStarshipOrVehicle(yourStarCruiser),
                        Filters.sameLocationAs(self, Filters.and(Filters.your(self), Filters.admiral)))))));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter yourAwingsAtSameAndRelatedLocations = Filters.and(Filters.your(self), Filters.A_wing, Filters.atSameOrRelatedLocation(self));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 1));
        modifiers.add(new PowerModifier(self, yourAwingsAtSameAndRelatedLocations, new PerAwingEvaluator(1)));
        modifiers.add(new ForfeitModifier(self, yourAwingsAtSameAndRelatedLocations, new PerAwingEvaluator(1)));
        return modifiers;
    }
}
