package com.gempukku.swccgo.cards.set12.dark;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.conditions.InSenateMajorityCondition;
import com.gempukku.swccgo.cards.evaluators.AtSameSiteEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Coruscant
 * Type: Character
 * Subtype: Republic
 * Title: Passel Argente
 */
public class Card12_116 extends AbstractRepublic {
    public Card12_116() {
        super(Side.DARK, 2, 3, 1, 2, 3, "Passel Argente", Uniqueness.UNIQUE);
        setPolitics(2);
        setLore("A senator known for his ability to deflect blame. It is rumored that Argente receives kickbacks from a few corporations to thwart other companies' developments.");
        setGameText("Agendas: ambition, taxation. While in a senate majority, opponent's non-unique starships and non-unique vehicles are each deploy +2 and power -1. Argente is politics +X, where X = number of opponent's senators at same site.");
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I);
        addKeywords(Keyword.SENATOR);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AgendaModifier(self, Agenda.AMBITION, Agenda.TAXATION));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition inSenateMajority = new InSenateMajorityCondition(self);
        Filter opponentsNonuniqueStarshipsAndVehicles = Filters.and(Filters.opponents(self), Filters.non_unique,
                Filters.or(Filters.starship, Filters.vehicle));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostModifier(self, opponentsNonuniqueStarshipsAndVehicles, inSenateMajority, 2));
        modifiers.add(new PowerModifier(self, opponentsNonuniqueStarshipsAndVehicles, inSenateMajority, -1));
        modifiers.add(new PoliticsModifier(self, new AtSameSiteEvaluator(self, Filters.and(Filters.opponents(self), Filters.senator))));
        return modifiers;
    }
}
