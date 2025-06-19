package com.gempukku.swccgo.cards.set4.light;

import com.gempukku.swccgo.cards.AbstractCharacterDevice;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Dagobah
 * Type: Device
 * Title: Rebel Flight Suit
 */
public class Card4_014 extends AbstractCharacterDevice {

    private static final Filter StarfighterCombatOrShuttleVehicle = Filters.or(
                Filters.starfighter, Filters.combat_vehicle, Filters.shuttle_vehicle
            );

    private static final Filter AttachedPilotMatchesShip(PhysicalCard self) {
        return Filters.and(
                Filters.hasMatchingPilotAboard(self),
                Filters.hasPiloting(self.getAttachedTo()),
                StarfighterCombatOrShuttleVehicle
        );
    }
    public Card4_014() {
        super(Side.LIGHT, 5, "Rebel Flight Suit", Uniqueness.UNRESTRICTED, ExpansionSet.DAGOBAH, Rarity.C);
        setLore("Pilot fatigues feature digital technology which can be customized for particular starfighters. Increases interface efficiency with a newly assigned craft.");
        setGameText("Deploy on your pilot character.  While piloting any starfighter, combat vehicle, or shuttle vehicle, that character is considered to be the \"matching pilot\" (pilot adds 2 to maneuver (limit +2) and draws one battle destiny if not able to otherwise).");
        addIcons(Icon.DAGOBAH);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.pilot);
    }

    @Override
    protected Filter getGameTextValidToUseDeviceFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.pilot;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        var modifiers = new LinkedList<Modifier>();
        if(self.getAttachedTo() == null)
            return modifiers;

        modifiers.add(
                new ConsideredMatchingPilotModifier(
                        self,
                        Filters.hasAttached(self),
                        new OnTableCondition(self, Filters.and(
                                Filters.samePermanentCardId(self.getAttachedTo()),
                                Filters.piloting(StarfighterCombatOrShuttleVehicle)))
                )
        );

        modifiers.add(
                new ManeuverModifier(
                        //The pilot is marked as the source of the +2 maneuver so that it gets limited alongside any
                        // other maneuver bonus that they provide due to the "limit +2" clause.
                        self.getAttachedTo(),
                        Filters.hasPiloting(self, Filters.hasAttached(self)),
                        new OnTableCondition(self, AttachedPilotMatchesShip(self)),
                        2,
                        //Although this is not a true cumulative modifier, because we are pretending that this is
                        // "from" the pilot and not the flight suit, we do not want to run awry of the cumulative
                        // rule stomping out one of the maneuver bonuses due to being "from" the same source.
                        true
                )
        );

        modifiers.add(
                new ManeuverLimitFromPilotModifier(
                        self,
                        Filters.hasAttached(self),
                        new OnTableCondition(self, AttachedPilotMatchesShip(self)),
                        2
                )
        );

        modifiers.add(
                new DrawsBattleDestinyIfUnableToOtherwiseModifier(
                        self,
                        self.getAttachedTo(),
                        new OnTableCondition(self, AttachedPilotMatchesShip(self)),
                        1)
        );
        return modifiers;
    }
}