package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.conditions.CanAddDestinyToPowerCondition;
import com.gempukku.swccgo.cards.conditions.DuringBattleAtCondition;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.AddsDestinyToPowerModifier;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.NighttimeConditionsModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premiere
 * Type: Effect
 * Title: Nightfall
 */
public class Card1_056 extends AbstractNormalEffect {
    public Card1_056() {
        super(Side.LIGHT, 3, PlayCardZoneOption.ATTACHED, Title.Nightfall);
        setLore("Tatoo I and Tatoo II, twin suns of Tatooine, create a harsh desert environment, but provide spectacular sunsets. At night, many dangers emerge from the shadows.");
        setGameText("Deploy on any planet system to cause 'nighttime conditions' at related sites. During battles there, both sides add one destiny to power only. Spies deploy free to sites under 'nighttime conditions.'");
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
        Condition duringBattleAtRelatedSite = new DuringBattleAtCondition(relatedSites);
        Condition playerCanAddDestiniesToPower = new CanAddDestinyToPowerCondition(playerId);
        Condition opponentCanAddDestiniesToPower = new CanAddDestinyToPowerCondition(opponent);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new NighttimeConditionsModifier(self, Filters.relatedSite(self)));
        modifiers.add(new AddsDestinyToPowerModifier(self, new AndCondition(duringBattleAtRelatedSite, playerCanAddDestiniesToPower), 1, playerId));
        modifiers.add(new AddsDestinyToPowerModifier(self, new AndCondition(duringBattleAtRelatedSite, opponentCanAddDestiniesToPower), 1, opponent));
        modifiers.add(new DeploysFreeToLocationModifier(self, Filters.spy, Filters.and(Filters.site, Filters.under_nighttime_conditions)));
        return modifiers;
    }
}