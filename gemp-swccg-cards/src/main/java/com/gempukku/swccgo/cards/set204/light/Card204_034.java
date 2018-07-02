package com.gempukku.swccgo.cards.set204.light;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ManeuverModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PassengerAppliesAbilityForBattleDestinyModifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 4
 * Type: Starship
 * Subtype: Starfighter
 * Title: Stolen First Order TIE Fighter
 */
public class Card204_034 extends AbstractStarfighter {
    public Card204_034() {
        super(Side.LIGHT, 2, 2, 2, null, 4, 3, 4, "Stolen First Order TIE Fighter", Uniqueness.UNIQUE);
        setGameText("May add Poe (or a spy) as a pilot and 1 passenger. Deploys free to opponent's system. While Poe piloting, maneuver +2. If Finn aboard, apply his ability towards drawing battle destiny.");
        addIcons(Icon.EPISODE_VII, Icon.NAV_COMPUTER, Icon.RESISTANCE, Icon.VIRTUAL_SET_4);
        addModelType(ModelType.TIE_SF);
        setPilotCapacity(1);
        setPassengerCapacity(1);
        setMatchingPilotFilter(Filters.or(Filters.Poe, Filters.Finn));
        setAlwaysStolen(true);
    }

    @Override
    protected Filter getGameTextValidPilotFilter(String playerId, SwccgGame game, PhysicalCard self) {
        return Filters.or(Filters.Poe, Filters.spy);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeploysFreeToLocationModifier(self, Filters.and(Filters.opponents(self), Filters.system)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ManeuverModifier(self, new HasPilotingCondition(self, Filters.Poe), 2));
        modifiers.add(new PassengerAppliesAbilityForBattleDestinyModifier(self, Filters.and(Filters.Finn, Filters.aboard(self))));
        return modifiers;
    }
}
