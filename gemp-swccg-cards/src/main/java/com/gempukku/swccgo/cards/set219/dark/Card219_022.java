package com.gempukku.swccgo.cards.set219.dark;

import com.gempukku.swccgo.cards.AbstractCapitalStarship;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.conditions.HasAboardCondition;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 19
 * Type: Starship
 * Subtype: Capital
 * Title: Thunderflare (V)
 */
public class Card219_022 extends AbstractCapitalStarship {
    public Card219_022() {
        super(Side.DARK, 1, 7, 9, 6, null, 2, 7, Title.Thunderflare, Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("Overpowered Star Destroyer. Energy is transferred from hyperdrive to weapons. Patrol duties in the Core Worlds make it a common first assignment for junior officers.");
        setGameText("May add 6 pilots, 8 passengers, and 4 TIEs. While Khurgee aboard, power, armor, and hyperspeed +2. " +
                    "While Death Star II on table, Thunderflare moves for free. Permanent pilot provides ability of 2.");
        addIcons(Icon.DEATH_STAR_II, Icon.PILOT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_19);
        addModelType(ModelType.IMPERIAL_CLASS_STAR_DESTROYER);
        setPilotCapacity(6);
        setPassengerCapacity(8);
        setVehicleCapacity(2);
        setTIECapacity(4);
        setMatchingPilotFilter(Filters.persona(Persona.KHURGEE));
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(2) {});
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition khurgeeAboard = new HasAboardCondition(self, Filters.persona(Persona.KHURGEE));
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new PowerModifier(self, self, khurgeeAboard, 2));
        modifiers.add(new ArmorModifier(self, self, khurgeeAboard, 2));
        modifiers.add(new HyperspeedModifier(self, self, khurgeeAboard, 2));
        modifiers.add(new MovesForFreeModifier(self, new OnTableCondition(self, Filters.Death_Star_II_system)));
        return modifiers;
    }
}
