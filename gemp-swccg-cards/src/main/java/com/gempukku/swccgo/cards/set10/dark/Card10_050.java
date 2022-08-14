package com.gempukku.swccgo.cards.set10.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.conditions.CanAddDestinyToPowerCondition;
import com.gempukku.swccgo.cards.conditions.DuringBattleAtCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Reflections II
 * Type: Effect
 * Title: Sunsdown & Too Cold For Speeders
 */
public class Card10_050 extends AbstractNormalEffect {
    public Card10_050() {
        super(Side.DARK, 2, PlayCardZoneOption.ATTACHED, "Sunsdown & Too Cold For Speeders");
        addComboCardTitles(Title.Sunsdown, Title.Too_Cold_For_Speeders);
        setGameText("Deploy on any planet system. At related sites: nighttime conditions are in effect; non-creature vehicles are power = 0, maneuver = 0, and landspeed = 0; spies deploy -1; and in battles there both sides add one destiny to power only.");
        addKeywords(Keyword.DEPLOYS_ON_LOCATION);
        addIcons(Icon.REFLECTIONS_II);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.planet_system;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        Filter relatedSites = Filters.relatedSite(self);
        Filter nonCreatureVehiclesAtRelatedSites = Filters.and(Filters.non_creature_vehicle, Filters.at(relatedSites));
        Condition duringBattleAtRelatedSite = new DuringBattleAtCondition(relatedSites);
        Condition playerCanAddDestiniesToPower = new CanAddDestinyToPowerCondition(playerId);
        Condition opponentCanAddDestiniesToPower = new CanAddDestinyToPowerCondition(opponent);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new NighttimeConditionsModifier(self, Filters.relatedSite(self)));
        modifiers.add(new ResetPowerModifier(self, nonCreatureVehiclesAtRelatedSites, 0));
        modifiers.add(new ResetManeuverModifier(self, nonCreatureVehiclesAtRelatedSites, 0));
        modifiers.add(new ResetLandspeedModifier(self, nonCreatureVehiclesAtRelatedSites, 0));
        modifiers.add(new DeployCostToLocationModifier(self, Filters.spy, -1, relatedSites));
        modifiers.add(new AddsDestinyToPowerModifier(self, new AndCondition(duringBattleAtRelatedSite, playerCanAddDestiniesToPower), 1, playerId));
        modifiers.add(new AddsDestinyToPowerModifier(self, new AndCondition(duringBattleAtRelatedSite, opponentCanAddDestiniesToPower), 1, opponent));
        return modifiers;
    }
}